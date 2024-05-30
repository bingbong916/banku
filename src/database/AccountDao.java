package database;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
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
        lines.set(productIndex + 1, productLine); // 예금 금액은 인덱스 0, 적금 상품은 인덱스 1부터 시작
        dbManager.writeAccountFile(accountNumber, lines);
    }
    
    public long getBalance(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        // Assuming the balance is stored as the first line in the account file
        String balanceStr = lines.get(0);

        return Long.parseLong(balanceStr);
    }
    
    public String getBalanceToString(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        // Assuming the balance is stored as the first line in the account file
        String balanceStr = lines.get(0);
        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        return decimalFormat.format(Long.parseLong(balanceStr));
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
        if(!carryBackStr.isEmpty()) {
            String[] parts = carryBackStr.split("\t");
            DecimalFormat decimalFormat = new DecimalFormat("###,###");

            String input = parts[0];
            Long carryBack = (Long) Math.round(Double.parseDouble(input) + Double.parseDouble(input)*0.03);

            return decimalFormat.format(carryBack);
        }
        return accountNumber;
    }

////    // TODO: executeTransaction method 에 통합
//    public void updateBalance(String accountNumber, long newBalance) throws IOException {
//        List<String> lines = dbManager.readAccountFile(accountNumber);
//        // Update balance in the first line
//        lines.set(0, String.valueOf(newBalance));
//        dbManager.writeAccountFile(accountNumber, lines);
//    }

    // TODO: 모든 입출금 로직을 하나의 method 로 통합
    // TODO: 모든 입출금 로직에 대해 입출금 내역 로그 추가
    public int executeTransaction(String accountNumber, long money, String type) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        long oldBalance = Long.parseLong(lines.get(0));
        switch (type){
            case "deposit": // 입금
                try {
                    money = Math.addExact(money, oldBalance);
                } catch (ArithmeticException e) {
                    return -1; // 입금 long 형 범위 초과
                }
                lines.set(0, Long.toString(money));
                dbManager.writeAccountFile(accountNumber, lines);
                return 0;

            case "withdrawal": // 출금
                if (oldBalance - money < 0) {
                    return -1; // 잔고보다 더 많은 돈을 출금할 때
                }
                money = oldBalance - money;
                lines.set(0, Long.toString(money));
                dbManager.writeAccountFile(accountNumber, lines);
                return 0;

            case "savings": // 적금
                if (oldBalance - money < 0) {
                    return -1; // 적금 금액보다 더 많은 돈을 출금할 때
                }
                money = oldBalance - money;
                lines.set(0, Long.toString(money));
                dbManager.writeAccountFile(accountNumber, lines);
                return 0;

            case "sender": // 송금 - 보내는 사람
                // 이중 체크
                if (oldBalance - money < 0) {
                    return -1; // 잔고보다 더 많은 돈을 송금할 때
                }
                money = oldBalance - money;
                lines.set(0, Long.toString(money));
                dbManager.writeAccountFile(accountNumber, lines);
                return 0;

            case "receiver": // 송금 - 받는 사람
                try {
                    money = Math.addExact(money, oldBalance);
                } catch (ArithmeticException e) {
                    return -1; // 입금 long 형 범위 초과
                }
                lines.set(0, Long.toString(money));
                dbManager.writeAccountFile(accountNumber, lines);
                return 0;

            case "canceled": // 적금 해지
                try {
                    money = Math.addExact(money, oldBalance);
                } catch (ArithmeticException e) {
                    return -1; // 입금 long 형 범위 초과
                }
                lines.set(0, Long.toString(money));
                dbManager.writeAccountFile(accountNumber, lines);
                return 0;

            default:
                return -2; // 비정상 요청 타입
        }
    }

    public String showSavings (String accountNumber) throws IOException{
        List<String> lines = dbManager.readAccountFile(accountNumber);
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(Long.parseLong(lines.get(0)));
    }

    public boolean hasSavings (String accountNumber, int index) throws IOException{
        System.out.println();
        List<String> lines = dbManager.readAccountFile(accountNumber);
        return !lines.get(index).trim().isEmpty();
    }

    public void removeSavings (String accountNumber, int productNum) throws IOException{
        List<String> lines = dbManager.readAccountFile(accountNumber);
        lines.set(productNum, "");
        dbManager.writeAccountFile(accountNumber,lines);
    }

    public long getSavings (String accountNumber, int index) throws  IOException{
        List<String> lines = dbManager.readAccountFile(accountNumber);
        return Long.parseLong(lines.get(index));
    }

    //추가
    public String getSavingsAmount(String accountNumber, int productIndex) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String productLine = lines.get(productIndex + 1); // 인덱스 1부터 적금 상품 정보 시작
        String[] productInfo = productLine.split("\t");
        return productInfo.length > 0 ? productInfo[0] : "0";
    }

    public void addSavings (String accountNumber, long money, int productIndex) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String line = lines.get(productIndex);
        String[] parts = line.split("\t");
        String startDate = getStartDate(accountNumber, productIndex - 1);
        long oldSavings = Long.parseLong(parts[0]);
        long newSavings = oldSavings + money;

        lines.set(productIndex, newSavings + "\t" + startDate); // 잔액 index = 0
        dbManager.writeAccountFile(accountNumber, lines);
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
