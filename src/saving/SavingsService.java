package saving;

import database.AccountDao;
import database.DatabaseManager;
import database.UserDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class SavingsService {
    private final Scanner scanner;
    private final UserDao userDao;
    private final AccountDao accountDao;

    public SavingsService(UserDao userDao, AccountDao accountDao){
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
    }
    public void doSavingService(String loggedInUserId) {

        try {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Scanner scan = new Scanner(System.in);
            System.out.println("[정기예금 서비스]");
            System.out.println("============================================");
            System.out.println("현재 예금 가능한 최대 금액: ₩");
            System.out.println("'q'를 입력할 시 이전 화면으로 돌아갑니다.");
            System.out.println("============================================");

            while (true) {
                System.out.print("예금할 금액을 입력하세요 (₩1,000 ~ ₩1,000,000,000): ₩ ");
                int inputMoney = scan.nextInt();

                if (inputMoney < 1000 || inputMoney > 1000000000) {
                    System.out.println("범위 내 금액을 힙력하세요.");
                }

                String amount = Integer.toString(inputMoney);
                String account = userDao.findUserToAccount(loggedInUserId);
                String startDate = dateFormat.format(date);
                accountDao.updateSavings(account, 0, amount, startDate);
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

}
