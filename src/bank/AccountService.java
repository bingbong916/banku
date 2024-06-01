package bank;

import database.AccountDao;
import database.DatabaseManager;
import database.DateDao;
import database.UserDao;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.Random;

public class AccountService {
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final Scanner scanner;
    private final DateDao dateDao;

    public AccountService(UserDao userDao, DateDao dateDao) {
        this.userDao = userDao;
        this.dateDao = dateDao;
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
            String date = dateDao.getDate();
            while (true) {
                System.out.print("초기 입금액을 입력하세요: ₩ ");
                amount = scanner.nextLine();

                if (amount.equals("q")) {
                    return;
                }

                if (!amount.matches("\\d+")) {
                    System.out.println("올바른 숫자 형식이 아닙니다. 다시 입력해주세요.");
                    continue;
                }

                long parsedAmount;
                try {
                    parsedAmount = Long.parseLong(amount);
                } catch (NumberFormatException e) {
                    System.out.println("최대 입금 가능 범위는 9,223,372,036,854,775,807원 입니다. 다시 입력해주세요.");
                    continue;
                }

                if (parsedAmount == 0) {
                    System.out.println("최소 1원 이상 입력해주세요.");
                    continue;
                }

                // 모든 검증을 통과하면 계좌 개설 진행
                String accountNumber = generateAccountNumber();
                userDao.addAccountToUser(loggedInUserId, accountNumber);
                accountDao.createAccount(accountNumber, parsedAmount, date);

                System.out.println("============================================");
                System.out.println("계좌 개설이 완료되었습니다! 계좌번호: " + accountNumber);
                break;
            }
        } catch (IOException e) {
            System.out.println("계좌 개설 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    private String generateAccountNumber() {
        long timestamp = Instant.now().getEpochSecond(); // 현재 시간의 유닉스 타임스탬프(초)
        String fullTimestamp = Long.toString(timestamp);
        String lastEightDigits = fullTimestamp.length() > 8 ? fullTimestamp.substring(fullTimestamp.length() - 8) : fullTimestamp; // 마지막 8자리만 추출

        Random random = new Random();
        String randomPart = String.format("%08d", random.nextInt(100000000));

        return lastEightDigits + "-" + randomPart;
    }
}
