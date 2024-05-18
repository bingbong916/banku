package saving;

import bank.MainMenu;
import database.AccountDao;
import database.DatabaseManager;
import database.UserDao;

import java.io.IOException;
import java.lang.reflect.Parameter;
import java.util.Scanner;

public class SavingServiceManager {
    private final Scanner scanner;
    private final SavingsService savingsService;
    private final TermDepositService termDepositService;
    private final CloseSavingService closeSavingService;


    public SavingServiceManager() {
        this.scanner = new Scanner(System.in);
        this.savingsService = new SavingsService(this);
        this.termDepositService = new TermDepositService(this);
        this.closeSavingService = new CloseSavingService(this);
    }

    public void printSavingMenu(String loggedInUserId) {
        try {
                while (true) {
                    System.out.println();
                    System.out.println("\n\n[예ㆍ적금 서비스]");
                    System.out.println("============================================");
                    System.out.println("정기예금 또는 적금 상품에 가입합니다.");
                    System.out.println("============================================");
                    System.out.println("[1] 정기 예금 서비스");
                    System.out.println("[2] 적금 서비스");
                    System.out.println("[3] 적금 해지 서비스");
                    System.out.println("[0] 뒤로가기");
                    System.out.println("============================================");

                    String inputNum = "";
                    while (true){
                        System.out.print("선택하실 서비스 번호를 입력하세요 (0-3): ");
                        inputNum = scanner.nextLine();

                        if (!inputNum.matches("[0-3]")) {
                            System.out.println("원하는 메뉴의 숫자만을 입력해 주세요.");
                            continue;
                        }
                        break;
                    }

                    int menuNum = Integer.parseInt(inputNum);

                    switch (menuNum) {
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
                            return;

                        default:
                            System.out.println("원하는 메뉴의 숫자만을 입력해 주세요.");
                            break;
                    }
                }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
