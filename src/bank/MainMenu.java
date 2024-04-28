package bank;

import database.AccountDao;
import database.DatabaseManager;
import database.DateDao;
import database.UserDao;
import transaction.TransferService;
import saving.SavingServiceManager;
import transaction.DepositService;
import transaction.WithdrawalService;
import check.CheckManager;

import java.io.IOException;
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
    private final UserDao userDao;



    public MainMenu(String userId) {
        this.scanner = new Scanner(System.in);
        this.userDao = new UserDao(new DatabaseManager());
        AccountDao accountDao = new AccountDao(new DatabaseManager());
        this.accountService = new AccountService(userDao);
        this.transferService = new TransferService(new AccountDao(new DatabaseManager()), userDao);
        this.depositService = new DepositService(userDao, accountDao);
        this.withdrawalService = new WithdrawalService(userDao, accountDao);
        this.savingServiceManager = new SavingServiceManager(userDao, accountDao);
        this.checkManager = new CheckManager(userDao, accountDao);
        this.loggedInUserId = userId;
        DateDao dateDao = new DateDao(new DatabaseManager());
    }

    public void show() throws IOException {
        printMainMenu();
        while (true) {
            System.out.print("선택하실 메뉴 번호를 입력하세요 (0-6): ");

            String selected = scanner.nextLine();
            switch (selected) {
                case "1":
                    // 계좌 존재 여부 확인
                    if (userDao.hasAccount(loggedInUserId)) {
                        System.out.println();
                        System.out.println("계좌가 이미 존재합니다.");
                        break;
                    }
                    accountService.openAccount(loggedInUserId);
                    printMainMenu();
                    break;
                case "2":
                    // 입장 시 계좌 존재 확인
                    if (!userDao.hasAccount(loggedInUserId)) {
                        System.out.println();
                        System.out.println("해당 아이디의 계좌가 존재하지 않습니다. 계좌 개설 후 다시 이용해주세요.");
                        System.out.println();
                        break;
                    }
                    // 예·적금 로직
                    savingServiceManager.printSavingMenu(loggedInUserId);
                    printMainMenu();
                    break;
                case "3":
                    // 입장 시 계좌 존재 확인
                    if (!userDao.hasAccount(loggedInUserId)) {
                        System.out.println();
                        System.out.println("해당 아이디의 계좌가 존재하지 않습니다. 계좌 개설 후 다시 이용해주세요.");
                        System.out.println();
                        break;
                    }
                    // 입금 로직
                    depositService.showDeposit(loggedInUserId);
                    printMainMenu();
                    break;
                case "4":
                    // 입장 시 계좌 존재 확인
                    if(!userDao.hasAccount(loggedInUserId)){
                        System.out.println();
                        System.out.println("해당 아이디의 계좌가 존재하지 않습니다. 계좌 개설 후 다시 이용해주세요.");
                        System.out.println();
                        break;
                    }
                    transferService.transfer(loggedInUserId);
                    printMainMenu();
                    break;
                case "5":
                    // 입장 시 계좌 존재 확인
                    if(!userDao.hasAccount(loggedInUserId)){
                        System.out.println();
                        System.out.println("해당 아이디의 계좌가 존재하지 않습니다. 계좌 개설 후 다시 이용해주세요.");
                        System.out.println();
                        break;
                    }
                    // 출금 로직
                    withdrawalService.showWithdrawal(loggedInUserId);
                    printMainMenu();
                    break;
                case "6":
                    // 입장 시 계좌 존재 확인
                    if(!userDao.hasAccount(loggedInUserId)){
                        System.out.println();
                        System.out.println("해당 아이디의 계좌가 존재하지 않습니다. 계좌 개설 후 다시 이용해주세요.");
                        System.out.println();
                        break;
                    }
                    // 계좌 및 예·적금 조회 로직
                    checkManager.printCheckingMenu(loggedInUserId);
                    printMainMenu();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
                    break;
            }
        }
    }
    private void printMainMenu() {
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
    }
}
