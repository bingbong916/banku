package database;

import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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


    public void updateBalance(String accountNumber, long newBalance) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        // Update balance in the first line
        lines.set(0, String.valueOf(newBalance));
        dbManager.writeAccountFile(accountNumber, lines);
    }

    public void depositSavings(String accountNumber, long money) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        long savings = money + Long.parseLong(lines.get(0));
        lines.set(0, Long.toString(savings)); // 잔액 index = 0
        dbManager.writeAccountFile(accountNumber, lines);
    }

    public int withdrawalSavings (String accountNumber, long money) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        long oldSavings = Long.parseLong(lines.get(0));
        if(Long.parseLong(lines.get(0)) - money < 0){
            return 0;
        }
        long newSavings = oldSavings - money;

        lines.set(0, Long.toString(newSavings)); // 잔액 index = 0
        dbManager.writeAccountFile(accountNumber, lines);
        return 1;
    }

    public String showSavings (String accountNumber) throws IOException{
        List<String> lines = dbManager.readAccountFile(accountNumber);
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(Long.parseLong(lines.get(0)));
    }

    public boolean hasSavings (String accountNumber, int index) throws IOException{
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
        return accountNumber;
    }
}
