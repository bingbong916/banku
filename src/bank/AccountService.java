package bank;

import database.AccountDao;
import database.DatabaseManager;
import database.UserDao;

import java.io.IOException;
import java.util.Scanner;
import java.util.Random;

public class AccountService {
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final Scanner scanner;

    public AccountService(UserDao userDao) {
        this.userDao = userDao;
        this.accountDao = new AccountDao(new DatabaseManager());
        this.scanner = new Scanner(System.in);
    }

    public void openAccount(String loggedInUserId) {
        try {

            System.out.println("\n\n[계좌 개설 서비스]");
            System.out.println("============================================");
            System.out.println("계좌를 개설합니다.");
            System.out.println("============================================");
            System.out.println("('q'를 입력할 시 이전 화면으로 돌아갑니다.)");


            String amount;
            while (true) {
                // 초기 입금액 입력
                System.out.print("초기 입금액을 입력하세요: ₩ ");
                amount = scanner.nextLine();

                if (amount.equals("q")) {
                    return;
                }

                if (!amount.matches("\\d+")) {
                    System.out.println("올바른 양식이 아닙니다.");
                } else {
                    break;
                }
            }

            String accountNumber = generateAccountNumber();

            userDao.addAccountToUser(loggedInUserId, accountNumber);

            accountDao.createAccount(accountNumber, amount);

            System.out.println("============================================");
            System.out.println("계좌 개설이 완료되었습니다! 계좌번호: " + accountNumber);
        } catch (IOException e) {
            System.out.println("계좌 개설 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private String generateAccountNumber() {
        Random random = new Random();
        // \\d{6}-\\d{6} 형식의 계좌 번호 생성
        return String.format("%06d-%06d", random.nextInt(1000000), random.nextInt(1000000));
    }
}
