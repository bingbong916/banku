package saving;

import database.AccountDao;
import database.DatabaseManager;
import database.DateDao;
import database.UserDao;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class SavingsService {
    private final Scanner scanner;
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final DecimalFormat decimalFormat;
    private final SavingServiceManager savingServiceManager;
    private final DateDao dateDao;

    public SavingsService(UserDao userDao, AccountDao accountDao, SavingServiceManager savingServiceManager){
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
        this.decimalFormat = new DecimalFormat("#,###");
        this.savingServiceManager = savingServiceManager;
        dateDao = new DateDao(new DatabaseManager());
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
    }
    public void doSavingService(String loggedInUserId) {

        try {
            String account = userDao.findUserToAccount(loggedInUserId);
            if(accountDao.hasSavings(account, 1)) {
                System.out.println("이미 가입한 예금입니다.");
                savingServiceManager.printSavingMenu(loggedInUserId);
                return; // 이미 가입한 예금인지 확인
            }

            Scanner scan = new Scanner(System.in);
            System.out.println();
            System.out.println("\n\n[정기예금 서비스]");
            System.out.println("============================================");
            System.out.println("현재 예금 가능한 최대 금액: ₩ 1,000,000,000");
            System.out.println("'q'를 입력할 시 이전 화면으로 돌아갑니다.");
            System.out.println("============================================");

            while (true) {
                System.out.print("예금할 금액을 입력하세요 (₩1,000 ~ ₩1,000,000,000): ₩ ");
                String inputMoney = scan.nextLine();
                if (inputMoney.equals("q")){
                    savingServiceManager.printSavingMenu(loggedInUserId);
                    return;
                }

                if(!inputMoney.matches("^[0-9]+$")){
                    System.out.println("숫자만 입력하세요.");
                    continue;
                }

                int money = Integer.parseInt(inputMoney);

                if (money < 1000 || money > 1000000000) {
                    System.out.println("올바른 금액을 입력하세요.");
                    continue;
                }


                int currentBalance = accountDao.getSavings(account);


                if (money > currentBalance) {
                    System.out.println("현재 잔액이 부족합니다. 현재 남은 잔액은 ₩" + decimalFormat.format(currentBalance) + "입니다.");
                    continue;
                }

                savingServiceManager.updateSavingProductAmount(money);
                String startDate = dateDao.getDate();

                // 첫 달 납입금을 현재 계좌에서 차감
                accountDao.withdrawalSavings(account, money);
                // 첫 달 납입금을 예금 계좌에 적립
                accountDao.updateSavings(account, 0, inputMoney, startDate);
                System.out.println("예금이 완료되었습니다!");
                System.out.println();
                break;
            }
        }catch (Exception e){
            e.getMessage();
        }
    }


}
