package transaction;

import database.AccountDao;
import database.DatabaseManager;
import database.DateDao;
import database.UserDao;

import java.io.IOException;
import java.util.Scanner;

public class WithdrawalService {
    private final Scanner scanner;
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final DateDao dateDao;

    public WithdrawalService(UserDao userDao, AccountDao accountDao){
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
        this.dateDao = new DateDao(new DatabaseManager());
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
    }

    public void showWithdrawal(String loggedInUserId){
        try{
            System.out.println("\n\n[출금 서비스]");
            System.out.println("============================================");
            System.out.println("('q'를 입력할 시 이전 화면으로 돌아갑니다.)");
            System.out.println("============================================");
            System.out.println("출금할 금액을 입력하세요");

            String money = "";
            String currentDate = dateDao.getDate();

            while (true) {
                String account = userDao.findUserToAccount(loggedInUserId);

                System.out.println("출금 가능한 금액은 ₩ " + accountDao.showSavings(account) + " 원 입니다");
                System.out.print("출금할 금액: ₩ ");
                money = scanner.nextLine();

                if (money.equals("q")) {
                    return;
                }

                if (!money.matches("\\d+")) {
                    System.out.println("올바른 양식이 아닙니다.");
                    continue;
                }

                try{
                    Long.parseLong(money);
                }catch (NumberFormatException e){
                    System.out.println("최대 출금 가능 범위는 9,223,372,036,854,775,807원 입니다. 다시 입력해주세요.");
                    continue;
                }

                if (Long.parseLong(money) == 0) {
                    System.out.println("최소 1원 이상 입력해주세요.");
                    continue;
                }

                long amount = Long.parseLong(money);

                int returnNum = accountDao.executeTransaction(account, amount, "withdrawal", currentDate);

                System.out.println();
                if(returnNum == 0){
                    System.out.println("출금이 완료되었습니다!");
                    System.out.println("현재 잔액: ₩ " + accountDao.showSavings(account));
                    break;
                } else {
                    System.out.println("현재 잔액이 부족합니다. 현재 남은 잔액은 ₩ " + accountDao.showSavings(account) + " 입니다. 출금할 금액을 다시 입력해주세요.");
                }
            }

        } catch (IOException e){
            System.out.println("출금 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
