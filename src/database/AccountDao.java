package database;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountDao {
    private final DatabaseManager dbManager;

    public AccountDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void createAccount(String accountNumber, String balance) throws IOException {
        List<String> lines = Arrays.asList(balance, "", "", "", "");
        dbManager.writeAccountFile(accountNumber, lines);
    }

    public void updateSavings(String accountNumber, int productIndex, String amount, String startDate) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String productLine = amount + "\t" + startDate;
        lines.set(productIndex + 1, productLine);
        dbManager.writeAccountFile(accountNumber, lines);
    }

    public long getBalance(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String balanceStr = lines.get(0);
        return Long.parseLong(balanceStr);
    }

    public String getBalanceToString(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String balanceStr = lines.get(0);
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(Long.parseLong(balanceStr));
    }

    public void showTransactionLog(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        System.out.println("============================================");
        if (lines.size() > 5) {
            for (int i = 5; i < lines.size(); i++) {
                System.out.println(lines.get(i));
            }
        }
    }


    public String getStartDate(String accountNumber, int index) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        List<String> subList = lines.subList(1, lines.size());

        String str = subList.get(index);
        if (!str.isEmpty()) {
            String[] parts = str.split("\t");
            return parts[1];
        }
        return accountNumber;
    }

    public String getCarryBack(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String carryBackStr = lines.get(1);
        if (!carryBackStr.isEmpty()) {
            String[] parts = carryBackStr.split("\t");
            DecimalFormat decimalFormat = new DecimalFormat("###,###");
            String input = parts[0];
            Long carryBack = (Long) Math.round(Double.parseDouble(input) + Double.parseDouble(input) * 0.03);
            return decimalFormat.format(carryBack);
        }
        return accountNumber;
    }

    public int executeTransaction(String accountNumber, long money, String type, String date) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        long oldBalance = Long.parseLong(lines.get(0));
        DecimalFormat decimalFormat = new DecimalFormat("₩ #,###");
        String formattedDate = parseDate(date);

        boolean dateExists = false;
        StringBuilder transactionLog = new StringBuilder();

        for (int i = 5; i < lines.size(); i++) {
            if (lines.get(i).startsWith(formattedDate)) {
                dateExists = true;
                break;
            }
        }

        if (!dateExists) {
            transactionLog.append(formattedDate).append("\n");
        }
        switch (type) {
            case "deposit": // 입금
                try {
                    oldBalance = Math.addExact(money, oldBalance);
                } catch (ArithmeticException e) {
                    return -1; // 입금 long 형 범위 초과
                }
                transactionLog.append("입금:\t+ ").append(decimalFormat.format(money)).append("\t").append(decimalFormat.format(oldBalance));
                break;

            case "withdrawal": // 출금
                if (oldBalance - money < 0) {
                    return -1; // 잔고보다 더 많은 돈을 출금할 때
                }
                oldBalance -= money;
                transactionLog.append("출금:\t- ").append(decimalFormat.format(money)).append("\t").append(decimalFormat.format(oldBalance));
                break;

            case "savings": // 적금
                if (oldBalance - money < 0) {
                    return -1; // 적금 금액보다 더 많은 돈을 출금할 때
                }
                oldBalance -= money;
                transactionLog.append("적금:\t- ").append(decimalFormat.format(money)).append("\t").append(decimalFormat.format(oldBalance));
                break;

            case "canceled": // 적금 해지
                try {
                    oldBalance = Math.addExact(money, oldBalance);
                } catch (ArithmeticException e) {
                    return -1; // 입금 long 형 범위 초과
                }
                transactionLog.append("적금 해지:\t+ ").append(decimalFormat.format(money)).append("\t").append(decimalFormat.format(oldBalance));
                break;

            default:
                return -2; // 비정상 요청 타입
        }
        lines.set(0, String.valueOf(oldBalance));
        lines.add(transactionLog.toString());
        dbManager.writeAccountFile(accountNumber, lines);

        return 0;
    }

    public void executeTransferTransaction(String accountNumber, long money, String counterpartyName, String type, String date) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        long oldBalance = Long.parseLong(lines.get(0));
        DecimalFormat decimalFormat = new DecimalFormat("₩ #,###");
        String formattedDate = parseDate(date);

        boolean dateExists = false;
        StringBuilder transactionLog = new StringBuilder();

        for (int i = 5; i < lines.size(); i++) {
            if (lines.get(i).startsWith(formattedDate)) {
                dateExists = true;
                break;
            }
        }

        if (!dateExists) {
            transactionLog.append(formattedDate).append("\n");
        }

        switch (type) {
            case "sender": // 송금 - 보내는 사람
                if (oldBalance + money < 0) {
                    return; // 송금 금액이 잔액보다 많음
                }
                oldBalance += money;
                transactionLog.append(counterpartyName).append(":\t- ").append(decimalFormat.format(-money)).append("\t").append(decimalFormat.format(oldBalance));
                break;

            case "receiver": // 송금 - 받는 사람
                oldBalance += money;
                transactionLog.append(counterpartyName).append(":\t+ ").append(decimalFormat.format(money)).append("\t").append(decimalFormat.format(oldBalance));
                break;

            default:
                return; // 비정상 요청 타입
        }

        lines.set(0, String.valueOf(oldBalance));

        if (!transactionLog.toString().isEmpty()) {
            lines.add(transactionLog.toString());
        }

        dbManager.writeAccountFile(accountNumber, lines);
    }

    public String showSavings(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(Long.parseLong(lines.get(0)));
    }

    public boolean hasSavings(String accountNumber, int index) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        return !lines.get(index).trim().isEmpty();
    }

    public void removeSavings(String accountNumber, int productNum) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        lines.set(productNum, "");
        dbManager.writeAccountFile(accountNumber, lines);
    }

    public long getSavings(String accountNumber, int index) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        return Long.parseLong(lines.get(index));
    }

    public String getSavingsAmount(String accountNumber, int productIndex) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String productLine = lines.get(productIndex + 1);
        String[] productInfo = productLine.split("\t");
        return productInfo.length > 0 ? productInfo[0] : "0";
    }

    public void addSavings(String accountNumber, long money, int productIndex) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String line = lines.get(productIndex);
        String[] parts = line.split("\t");
        String startDate = getStartDate(accountNumber, productIndex - 1);
        long oldSavings = Long.parseLong(parts[0]);
        long newSavings = oldSavings + money;

        lines.set(productIndex, newSavings + "\t" + startDate);
        dbManager.writeAccountFile(accountNumber, lines);
    }

    private String parseDate(String rawDate) {
        int month = Integer.parseInt(rawDate.substring(4, 6));
        int day = Integer.parseInt(rawDate.substring(6, 8));
        return month + "월 " + day + "일";
    }

    public String getAmount(String accountNumber, int index) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        List<String> subList = lines.subList(1, lines.size());

        String str = subList.get(index);
        if (!str.isEmpty()) {
            String[] parts = str.split("\t");
            return parts[0];
        }
        return null;
    }
    

    // 복리 계산
    public void calculateAndDepositInterest(String accountNumber, DateDao dateDao) throws IOException {
        // 같은 달이 아닐 경우에만 복리를 계산하고 입금합니다.
        if (!dateDao.isSameMonth(accountNumber)) {
            // 계좌 파일을 읽어옵니다.
            List<String> lines = dbManager.readAccountFile(accountNumber);
            // 복리의 합을 저장할 변수를 초기화합니다.
            long interestSum = 0;
            // 마지막으로 업데이트된 날짜와 잔액을 저장할 변수를 초기화합니다.
            String lastDate = null;
            long lastBalance = 0;

            // 계좌 파일의 각 줄을 순회하면서 복리를 계산합니다.
            for (String line : lines) {
                // 각 줄에서 날짜와 잔액을 추출합니다.
                Matcher matcher = Pattern.compile("(\\d+월 \\d+일).*₩ (\\d+)").matcher(line);
                if (matcher.find()) {
                    // 현재 날짜와 잔액을 가져옵니다.
                    String currentDate = matcher.group(1).replace("월 ", "").replace("일", "");
                    long currentBalance = Long.parseLong(matcher.group(2));

                    // 이전 날짜가 있을 경우, 이전 날짜와 현재 날짜 사이의 복리를 계산합니다.
                    if (lastDate != null) {
                        long daysBetween = dateDao.calculateDaysBetween(lastDate, currentDate);
                        double interest = calculateInterest(lastBalance, daysBetween);
                        // 계산된 복리를 복리의 합에 더합니다.
                        interestSum += interest;
                    }

                    // 마지막으로 업데이트된 날짜와 잔액을 업데이트합니다.
                    lastDate = currentDate;
                    lastBalance = currentBalance;
                }
            }

            // 복리 입금 진행
            // 매달 1일로 설정하여 입금 날짜를 설정합니다.
            String depositDate = dateDao.getDate().substring(0, 6) + "01";
            // 계산된 복리의 합을 입금합니다.
            executeTransaction(accountNumber, interestSum, "deposit", dateDao.getDate());
        }
    }

    // 복리 계산 공식
    private double calculateInterest(double balance, long days) {
        // 연간 이자율을 2%로 가정합니다.
        double annualInterestRate = 0.02;
        // 복리 공식을 사용하여 복리를 계산합니다.
        return balance * Math.pow(1 + annualInterestRate, days / 365.0) - balance;
    }

    
    
}
