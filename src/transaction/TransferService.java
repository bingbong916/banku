package transaction;

import database.AccountDao;
import database.DatabaseManager;
import database.DateDao;
import database.UserDao;

import java.io.IOException;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Scanner;

public class TransferService {
    private static final long MAX_AMOUNT = Long.MAX_VALUE;
    private final AccountDao accountDao;
    private final UserDao userDao;
    private final DateDao dateDao;
    private final Scanner scanner;
    private final NumberFormat numberFormat;

    public TransferService(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.dateDao = new DateDao(new DatabaseManager());
        this.scanner = new Scanner(System.in);
        this.numberFormat = NumberFormat.getInstance();
    }

    public void transfer(String loggedInUserId) {
        try {
            String senderAccountNumber = userDao.getAccountNumber(loggedInUserId);
            String currentDate = dateDao.getDate();

            System.out.println("\n\n[송금 서비스]");
            System.out.println("============================================");
            System.out.println("('q'를 입력할 시 이전 화면으로 돌아갑니다.)");
            System.out.println("송금할 계좌번호와 금액을 입력하세요");
            System.out.println("============================================");

            while (true) {
                System.out.print("받는 사람 계좌번호: ");
                String receiverAccountNumber = scanner.nextLine();

                if (receiverAccountNumber.equals("q")) {
                    System.out.println("이전 화면으로 돌아갑니다.");
                    return;
                }

                if (!isValidAccountNumber(receiverAccountNumber)) {
                    System.out.println("올바른 계좌 번호를 입력하세요!");
                    continue;
                }

                if (!userDao.checkRecAccNumber(receiverAccountNumber)) {
                    System.out.println("받는 사람의 계좌번호가 존재하지 않습니다.");
                    continue;
                }

                if (senderAccountNumber.equals(receiverAccountNumber)) {
                    System.out.println("자신의 계좌로의 송금은 불가능합니다.");
                    continue;
                }

                while (true) {
                    System.out.print("송금할 금액: ₩ ");
                    String amountStr = scanner.nextLine();

                    if (amountStr.equals("q")) {
                        System.out.println("이전 화면으로 돌아갑니다.");
                        return;
                    }

                    if (!isValidAmount(amountStr)) {
                        System.out.println("올바른 금액을 입력하세요!");
                        continue; // Continue to re-prompt for the amount
                    }

                    long amountLong = Long.parseLong(amountStr.replace(",", ""));

                    if (amountLong <= 0) {
                        System.out.println("송금할 금액은 1원 이상이어야 합니다.");
                        continue; // Continue to re-prompt for the amount
                    }

                    if(amountLong >= Long.MAX_VALUE){
                        System.out.println("최대 송금 가능 범위는 9,223,372,036,854,775,807원 입니다.");
                        continue; // Continue to re-prompt for the amount
                    }

                    long senderBalance = accountDao.getBalance(senderAccountNumber);
                    if (senderBalance < amountLong) {
                        System.out.println("잔액이 부족합니다!");
                        System.out.println("현재 잔액: ₩ " + numberFormat.format(senderBalance));
                        continue; // Continue to re-prompt for the amount
                    }

                    long receiverBalance = accountDao.getBalance(receiverAccountNumber);
                    if (Long.MAX_VALUE - receiverBalance < amountLong) {
                        System.out.println("받는 사람의 계좌 잔액이 너무 큽니다. 다른 금액을 입력하세요!");
                        continue; // Continue to re-prompt for the amount
                    }

                    String senderName = userDao.findUserToName(loggedInUserId);
                    String receiverName = userDao.findUserNameByAccount(receiverAccountNumber);

                    accountDao.executeTransferTransaction(senderAccountNumber, -amountLong, receiverName, "sender", currentDate);
                    accountDao.executeTransferTransaction(receiverAccountNumber, amountLong, senderName, "receiver", currentDate);

                    System.out.println();
                    System.out.println("송금이 완료되었습니다.");
                    System.out.println("현재 잔액: ₩ " + numberFormat.format(senderBalance - amountLong));
                    return; // Exit both loops after successful transfer
                }
            }
        } catch (IOException e) {
            System.out.println("송금 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private boolean isValidAccountNumber(String accountNumber) {
        return accountNumber.matches("\\d{8}-\\d{8}");
    }

    private boolean isValidAmount(String amountStr) {
        // Check if the amount is a valid number
        if (amountStr.matches("\\d+")) {
            BigInteger amount = new BigInteger(amountStr.replace(",", "")); // Convert to BigInteger

            // Check against the maximum value of a long
            if (amount.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                System.out.println("최대 송금 가능 범위는 " + numberFormat.format(MAX_AMOUNT) + "원 입니다.");
                return false; // Return false for amounts exceeding Long.MAX_VALUE
            }

            try {
                long amountLong = amount.longValueExact(); // Convert to long, will throw if out of long range
                if (amountLong <= 0) {
                    System.out.println("송금할 금액은 1원 이상이어야 합니다.");
                    return false; // Return false for non-positive amounts
                }

                return true; // Valid amount within long range and positive
            } catch (ArithmeticException e) {
                System.out.println("입력한 금액이 너무 큽니다. 올바른 금액을 입력하세요!");
                return false; // Return false for amounts too large to convert to long
            }
        }
        return false; // Not a valid numeric input
    }
}
