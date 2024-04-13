package saving;

import database.AccountDao;
import database.DatabaseManager;
import database.UserDao;

import java.util.Scanner;

public class CloseSavingService {
    private final Scanner scanner;
    private final UserDao userDao;
    private final AccountDao accountDao;

    public CloseSavingService(UserDao userDao, AccountDao accountDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
    }

    public void doCloseService(String loggedInUserId){
        try {
            System.out.println();
            System.out.println("[예ㆍ적금 해지 서비스]");
            System.out.println("============================================");
            System.out.println("'q'를 입력할 시 이전 화면으로 돌아갑니다.");
            System.out.println("예ㆍ적금 해지 서비스를 시작합니다. 개인정보를 입력해주세요");
            System.out.println("============================================");

            boolean flag = true;
            while (flag) {
                System.out.print("이름을 입력하세요 : ");
                String inputName = scanner.nextLine();
                if (!inputName.equals(loggedInUserId)) {
                    System.out.println("존재하지 않는 이름입니다.");
                    return;
                }

                System.out.print("주민등록번호를 입력하세요: ");
                String inputRRN = scanner.nextLine();
                if (!inputRRN.equals(userDao.findUserToRRN(loggedInUserId))) {
                    System.out.println("존재하지 않는 주민등록번호입니다.");
                    return;
                }

                System.out.println();
                System.out.println("[예ㆍ적금 해지 서비스]");
                System.out.println("============================================");
                System.out.println("예ㆍ적금 조회 결과:");

                String account = userDao.findUserToAccount(loggedInUserId);
                boolean result1 = accountDao.hasSavings(account, 1);
                boolean result2 = accountDao.hasSavings(account, 2);
                boolean result3 = accountDao.hasSavings(account, 3);
                boolean result4 = accountDao.hasSavings(account, 4);

                if (result1){
                    System.out.println("[1] 정기 예금");
                }
                if (result2){
                    System.out.println("[2] 6개월 적금");
                }
                if (result3){
                    System.out.println("[3] 12개월 적금");
                }
                if (result4){
                    System.out.println("[4] 24개월 저금");
                }

                System.out.println("[0] 뒤로가기");
                System.out.println("============================================");
                System.out.print("해지하실 예ㆍ적금 번호를 입력하세요 :");
                int inputNum = scanner.nextInt();

                switch (inputNum){
                    case 1:
                        if (!result1){
                            System.out.println("올바르지 않은 메뉴입니다.");
                        }
                    case 2:
                        if (!result2){
                            System.out.println("올바르지 않은 메뉴입니다.");
                        }
                    case 3:
                        if (!result3){
                            System.out.println("올바르지 않은 메뉴입니다.");
                        }
                    case 4:
                        if (!result4){
                            System.out.println("올바르지 않은 메뉴입니다.");
                        }
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
    }
}

