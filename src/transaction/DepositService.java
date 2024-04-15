package transaction;

import database.AccountDao;
import database.DatabaseManager;
import database.UserDao;

import java.io.IOException;
import java.util.Scanner;

public class DepositService {
    private final Scanner scanner;
    private final UserDao userDao;
    private final AccountDao accountDao;

    public DepositService(UserDao userDao, AccountDao accountDao){
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
    }


    public void showDeposit(String loggedInUserId){
        try{
            // 입장 시 계좌 존재 확인
            if(!userDao.hasAccount(loggedInUserId)){
                System.out.println();
                System.out.println("해당 아이디의 계좌가 존재하지 않습니다. 계좌 개설 후 다시 이용해주세요.");
                System.out.println();
                return;
            }

            // 입금 서비스 시작
            System.out.println("[입금 서비스]");
            System.out.println("============================================");
            System.out.println("('q'를 입력할 시 이전 화면으로 돌아갑니다.)");
            System.out.println("============================================");
            System.out.println("입금할 금액을 입력하세요");
            System.out.print("입금할 금액: ");

            String money = "";

            while (true) {
                // 입금 금액 입력 받기
                System.out.print("입금할 금액: ");
                money = scanner.nextLine();

                if (money.equals("q")) {
                    return;
                }

                if (!money.matches("\\d+")) {
                    System.out.println("올바른 양식이 아닙니다.");
                } else {
                    break;
                }
            }

            // 입금 금액 입력 받기
            int amount = Integer.parseInt(money);

            // id 해당 계좌
            String account = userDao.findUserToAccount(loggedInUserId);
            accountDao.depositSavings(account, amount);

            System.out.println();
            System.out.println("입금이 완료되었습니다!");
            System.out.println("현재 잔액: " + accountDao.showSavings(account));
            System.out.println();

        } catch (IOException e){
            System.out.println("입금 중 오류가 발생했습니다: " + e.getMessage());
        }

    }
}