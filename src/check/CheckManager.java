package check;

import database.*;

import java.util.Scanner;


public class CheckManager {
    private final Scanner scanner;
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final CheckAccountService checkAccountService;
    private final CheckDepositService checkDepositService;

    public CheckManager(UserDao userDao, AccountDao accountDao){
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
        this.checkAccountService = new CheckAccountService(userDao, accountDao);
        this.checkDepositService = new CheckDepositService(userDao, accountDao);
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
    }

    public void printCheckingMenu(String loggedInUserId){

        System.out.println();
        System.out.println("[계좌 및 예ㆍ적금 조회 서비스]");
        System.out.println("============================================");
        System.out.println("계좌 또는 예ㆍ적금을 조회합니다.");
        System.out.println("============================================");
        System.out.println("[1] 계좌 조회 서비스");
        System.out.println("[2] 예ㆍ적금 조회 서비스");
        System.out.println("[0] 뒤로가기");
        System.out.println("============================================");

        while(true) {
            System.out.print("선택하실 서비스 번호를 입력하세요 (0-2): ");
            int menuNum = scanner.nextInt();

            switch (menuNum){
                case 1:
                    //계좌 조회
                    checkAccountService.checkAccount(loggedInUserId);
                    break;
                case 2:
                    //예적금 조회
                    checkDepositService.checkDeposit(loggedInUserId);
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
