package check;

import database.*;
import java.util.Scanner;

public class CheckAccountService {
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final Scanner scanner;

    public CheckAccountService(UserDao userDao, AccountDao accountDao){
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
        AccountDao accountDao = new AccountDao(dbManager);
    }

    
    public static void checkAccount(String loggedInUserId) {
        Scanner scan = new Scanner(System.in);

        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
        AccountDao accountDao = new AccountDao(dbManager);


        try {
            String storedName = userDao.findUserToName(loggedInUserId);
            String storedRRN = userDao.findUserToRRN(loggedInUserId);

            System.out.println("[계좌 조회 서비스]");
            System.out.println("============================================");
            System.out.println("('q를 입력할 시 이전 화면으로 돌아갑니다.')");
            System.out.print("이름을 입력하세요: ");
            String userName = scan.nextLine();
            if(!(userName.equals(storedName))) {
                System.out.println("존재하지 않는 이름입니다.");
                return;
            }
            System.out.print("주민번호를 입력하세요(입력 예시: 991106-010101): ");
            String userRRN = scan.nextLine();
            System.out.println("============================================");

            if (userRRN.equals(storedRRN)) {
                // 주민등록번호 일치
                String storedAccount = userDao.findUserToAccount(loggedInUserId);
                int balance = accountDao.getBalance(storedAccount);
                
                System.out.println("[계좌 조회 서비스]");
                System.out.println("============================================");
                System.out.println("계좌 조회 서비스:");
                System.out.println("계좌 번호: " + storedAccount);
                System.out.println("현재 잔액: ₩ " + balance);
                System.out.println("============================================");
                System.out.println(" ");

            } else {
                System.out.println("정보가 일치하지 않습니다.");
            }
        }
        catch (Exception e){
            e.getMessage();
        }

        scan.close();
    }
}
