package saving;

import database.AccountDao;
import database.DatabaseManager;
import database.UserDao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class TermDepositService {
    private final Scanner scanner;
    private final UserDao userDao;
    private final AccountDao accountDao;

    public TermDepositService(UserDao userDao, AccountDao accountDao){
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

            Scanner scan = new Scanner(System.in);
            System.out.println("[정기예금 서비스]");
            System.out.println("============================================");
            System.out.println("적금 가능항 상품");
            System.out.println("[1] 6개월 적금 - 연 2.0%, 월 ₩ 200,000    예상 수령액 : ₩ 1,200,000");
            System.out.println("[2] 12개월 적금 - 연 3.0%, 월 ₩ 500,000    예상 수령액 : ₩ 6,180,000");
            System.out.println("[3] 24개월 적금 - 연 5.0%, 월 ₩ 1,000,000    예상 수령액 : ₩ 25,830,000");
            System.out.println("[0] 뒤로가기");
            System.out.println("============================================");

            boolean flag = true;
            while (flag) {
                String amount = "0";
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String account = userDao.findUserToAccount(loggedInUserId);
                String startDate = dateFormat.format(date);

                System.out.print("적금하실 상품 번호의 숫자만 입력하세요 (0~3): ");
                int termDepositNum = scan.nextInt();

                switch (termDepositNum){
                    case 1:
                        accountDao.updateSavings(account, 1, amount, startDate);
                        System.out.println("적금 가입이 완료되었습니다!");
                        flag = false;
                        break;
                    case 2:
                        accountDao.updateSavings(account, 2, amount, startDate);
                        System.out.println("적금 가입이 완료되었습니다!");
                        flag = false;
                        break;
                    case 3:
                        accountDao.updateSavings(account, 3, amount, startDate);
                        System.out.println("적금 가입이 완료되었습니다!");
                        flag = false;
                        break;
                    default:
                        System.out.println("숫자만 입력하세요.");
                        break;
                }
            }
        }catch (Exception e){
            e.getMessage();
        }

    }
}
