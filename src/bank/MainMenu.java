package bank;

import database.AccountDao;
import database.DatabaseManager;
import database.UserDao;
import transaction.TransferService;
import saving.SavingServiceManager;
import transaction.DepositService;
import transaction.WithdrawalService;
import check.CheckManager;

import java.util.Scanner;

public class MainMenu {
    private final Scanner scanner;
    private final AccountService accountService;
    private final DepositService depositService;
    private final WithdrawalService withdrawalService;
    private final SavingServiceManager savingServiceManager;
    private final String loggedInUserId;
    private final TransferService transferService;
    private final CheckManager checkManager;

    public MainMenu(String userId) {
        this.scanner = new Scanner(System.in);
        UserDao userDao = new UserDao(new DatabaseManager());
        AccountDao accountDao = new AccountDao(new DatabaseManager());
        this.accountService = new AccountService(userDao);
        this.transferService = new TransferService(new AccountDao(new DatabaseManager()), userDao);
        this.depositService = new DepositService(userDao, accountDao);
        this.withdrawalService = new WithdrawalService(userDao, accountDao);
        this.savingServiceManager = new SavingServiceManager(userDao, accountDao);
        this.checkManager = new CheckManager(userDao, accountDao);
        this.loggedInUserId = userId;
    }

    public void show() {
        while (true) {
            System.out.println("\n\n============================================");
            System.out.println("      건국 은행에 오신 것을 환영합니다.");
            System.out.println("============================================");
            System.out.println("[1] 계좌 개설");
            System.out.println("[2] 예·적금");
            System.out.println("[3] 입금");
            System.out.println("[4] 송금");
            System.out.println("[5] 출금");
            System.out.println("[6] 계좌 및 예·적금 조회");
            System.out.println("[0] 종료");
            System.out.println("============================================");
            System.out.print("선택하실 메뉴 번호를 입력하세요 (0-6): ");

            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    accountService.openAccount(loggedInUserId);
                    break;
                case 2:
                    // 예·적금 로직
                    savingServiceManager.printSavingMenu(loggedInUserId);
                    break;
                case 3:
                    // 입금 로직
                    depositService.showDeposit(loggedInUserId);
                    break;
                case 4:
                	transferService.transfer(loggedInUserId);
                    break;
                case 5:
                    // 출금 로직
                    withdrawalService.showWithdrawal(loggedInUserId);
                    break;
                case 6:
                    // 계좌 및 예·적금 조회 로직
                    checkManager.printCheckingMenu(loggedInUserId);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
                    break;
            }
        }
    }
}
