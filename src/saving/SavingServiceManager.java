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
    private final CloseSavingService closeSavingService;

    public SavingServiceManager(UserDao userDao, AccountDao accountDao){
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
        //추가
        SavingProduct product1 = new SavingProduct(6, 2.0, 200000);
        SavingProduct product2 = new SavingProduct(12, 3.0, 500000);
        SavingProduct product3 = new SavingProduct(24, 5.0, 1000000);
        this.savingsService = new SavingsService(userDao, accountDao, this);
        this.termDepositService = new TermDepositService(userDao, accountDao, this);
        this.closeSavingService = new CloseSavingService(userDao, accountDao, product1, product2, product3, this);
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
    }

    public void printSavingMenu(String loggedInUserId){

        System.out.println();
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
                    closeSavingService.doCloseService(loggedInUserId);
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
