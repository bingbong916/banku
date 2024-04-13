package saving;

import database.AccountDao;
import database.DatabaseManager;
import database.UserDao;

import java.util.Scanner;

public class SavingServiceManager {
    private final Scanner scanner;
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final SavingsService savingsService;
    private final TermDepositService termDepositService;

    public SavingServiceManager(UserDao userDao, AccountDao accountDao){
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
        this.savingsService = new SavingsService(userDao, accountDao);
        this.termDepositService = new TermDepositService(userDao, accountDao);
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
    }

    public void printSavingMenu(String loggedInUserId){

        System.out.println("[예ㆍ적금 서비스]");
        System.out.println("============================================");
        System.out.println("정기예금 또는 적금 상품에 가입합니다.");
        System.out.println("============================================");
        System.out.println("[1] 정기 예금 서비스");
        System.out.println("[2] 적금 서비스");
        System.out.println("[3] 적금 해지 서비스");
        System.out.println("[0] 뒤로가기");
        System.out.println("============================================");

        while(true) {
            System.out.print("선택하실 서비스 번호를 입력하세요 (0-3): ");
            int menuNum = scanner.nextInt();

            switch (menuNum){
                case 1:
                    savingsService.doSavingService(loggedInUserId);
                    break;
                case 2:
                    termDepositService.doSavingService(loggedInUserId);
                    break;
                case 3:
                    break;
                case 0:
                    break;
                default:
                    System.out.println("원하는 메뉴의 숫자만을 입력해 주세요.");
                    break;
            }
            break;
        }
    }
}
