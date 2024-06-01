package database;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        List<List<String>> dateSections = new ArrayList<>();
        List<String> currentSection = new ArrayList<>();

        if (lines.size() > 5) {
            for (int i = 5; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.matches("\\d+월 \\d+일")) {
                    if (!currentSection.isEmpty()) {
                        dateSections.add(new ArrayList<>(currentSection));
                        currentSection.clear();
                    }
                }
                currentSection.add(line);
            }

            if (!currentSection.isEmpty()) {
                dateSections.add(currentSection);
            }

            Collections.reverse(dateSections);
            for (List<String> section : dateSections) {
                for (String s : section) {
                    System.out.println(s);
                }
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
}
