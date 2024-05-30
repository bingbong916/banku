package transaction;

import database.AccountDao;
import database.UserDao;

import java.io.IOException;
import java.util.Scanner;

public class TransferService {
    private final AccountDao accountDao;
    private final UserDao userDao;
    private final Scanner scanner;

    public TransferService(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
    }

    public void transfer(String loggedInUserId) {
        try {
            String senderAccountNumber = userDao.getAccountNumber(loggedInUserId);

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

                    if (!isValidAmount(amountStr) || Long.parseLong(amountStr) == 0) {
                        System.out.println("올바른 금액을 입력하세요!");
                        continue;
                    }

                    long amount = Long.parseLong(amountStr.replace(",", "")); // Remove commas if present

                    long senderBalance = accountDao.getBalance(senderAccountNumber);
                    if (senderBalance < amount) {
                        System.out.println("잔액이 부족합니다!");
                        System.out.println("현재 잔액: ₩ " + senderBalance);
                        // 사용자가 올바른 금액을 입력할 때까지 반복
                        continue;
                    }

                    long receiverBalance = accountDao.getBalance(receiverAccountNumber);

                    senderBalance -= amount;
                    receiverBalance += amount;

                    accountDao.executeTransaction(senderAccountNumber, senderBalance, "sender");
                    accountDao.executeTransaction(receiverAccountNumber, receiverBalance, "receiver");
                    System.out.println();
                    System.out.println("송금이 완료되었습니다.");
                    System.out.println("현재 잔액: ₩ " + senderBalance);
                    
                    break;
                }
                
                break;
            }
        } catch (IOException e) {
            System.out.println("송금 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

	private boolean isValidAccountNumber(String accountNumber) {
        return accountNumber.matches("\\d{8}-\\d{8}");
    }

    private boolean isValidAmount(String amountStr) {
        return amountStr.matches("\\d+");
    }

}
