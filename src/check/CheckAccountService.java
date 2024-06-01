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
        DateDao dateDao = new DateDao(dbManager);
    }
    private static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber.matches("\\d{6}-\\d{7}");
    }

    
    public static void checkAccount(String loggedInUserId) {
        Scanner scan = new Scanner(System.in);

        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
        AccountDao accountDao = new AccountDao(dbManager);



        try {
            String storedName = userDao.findUserToName(loggedInUserId);
            String storedRRN = userDao.findUserToRRN(loggedInUserId);

            System.out.println("\n\n[계좌 조회 서비스]");
            System.out.println("============================================");
            System.out.println("('q를 입력할 시 이전 화면으로 돌아갑니다.')");

            String userName;
            String userRRN;
            while(true) {
                System.out.print("이름을 입력하세요: ");
                userName = scan.nextLine();
                if(userName.equals("q")) {
                    System.out.println("이전으로 돌아갑니다.");
                    return;
                }
                if(!userName.matches("^[가-힣]*$")){
                    System.out.println("이름의 형식이 잘못되었습니다.");
                    continue;
                }
                if(userName.length() < 2 || userName.length() > 3){
                    System.out.println("이름의 길이가 잘못되었습니다.");
                    continue;
                }
                if(!(userName.equals(storedName))) {
                    System.out.println("존재하지 않는 이름입니다.");
                    return;
                }
                else break;
            }

            while(true) {
                System.out.print("주민번호를 입력하세요(입력 예시: 991106-010101): ");
                userRRN = scan.nextLine();
                if(userRRN.equals("q")) {
                    // checkManager.printCheckingMenu(loggedInUserId);
                    System.out.println("이전으로 돌아갑니다.");
                    return;
                }
                if(!isValidAccountNumber(userRRN)) {
                    System.out.println("올바른 주민번호를 입력하세요!");
                    continue;
                }
                if(!(userRRN.equals(storedRRN))) {
                    System.out.println("존재하지 않는 주민번호입니다.");
                    return;
                }
                else break;
            }

            //이름&주민번호 일치
            System.out.println("============================================");
            

            String storedAccount = userDao.findUserToAccount(loggedInUserId);
            String balance = accountDao.getBalanceToString(storedAccount);

            System.out.println("계좌 조회 결과:");
            System.out.println("계좌 번호: " + storedAccount);
            System.out.println("현재 잔액: ₩ " + balance);
            accountDao.showTransactionLog(storedAccount);

        }
        catch (Exception e){
            e.getMessage();
        }
    }
}
