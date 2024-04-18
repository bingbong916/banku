package database;

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
    
    public int getBalance(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        // Assuming the balance is stored as the first line in the account file
        String balanceStr = lines.get(0);
        return Integer.parseInt(balanceStr);
    }

    public String getStartDate(String accountNumber, int index) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        List<String> subList = lines.subList(1, lines.size());
        for (index=0;index<4;index++) {
            String str = subList.get(index);
            if (!str.isEmpty()) {
                String[] parts = str.split("\t");

                return parts[1];
            }
        }
        return accountNumber;
    }

    public String getCarryBack(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);

        String carryBackStr = lines.get(1);
        if(!carryBackStr.isEmpty()) {
            String[] parts = carryBackStr.split("\t");

            return parts[0];
        }
        return accountNumber;
    }

    public void updateBalance(String accountNumber, int newBalance) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        // Update balance in the first line
        lines.set(0, String.valueOf(newBalance));
        dbManager.writeAccountFile(accountNumber, lines);
    }

    public void depositSavings(String accountNumber, int money) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        int savings = money + Integer.parseInt(lines.get(0));
        lines.set(0, Integer.toString(savings)); // 잔액 index = 0
        dbManager.writeAccountFile(accountNumber, lines);
    }

    public int withdrawalSavings (String accountNumber, int money) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        int oldSavings = Integer.parseInt(lines.get(0));
        if(Integer.parseInt(lines.get(0)) - money < 0){
            return 0;
        }
        int newSavings = oldSavings - money;

        lines.set(0, Integer.toString(newSavings)); // 잔액 index = 0
        dbManager.writeAccountFile(accountNumber, lines);
        return 1;
    }

    public String showSavings (String accountNumber) throws IOException{
        List<String> lines = dbManager.readAccountFile(accountNumber);
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(Integer.parseInt(lines.get(0)));
    }

    public boolean hasSavings (String accountNumber, int index) throws IOException{
        List<String> lines = dbManager.readAccountFile(accountNumber);
        if (lines.get(index).isEmpty()) {
            return false;
        }
        return true;
    }

    public void removeSavings (String accountNumber, int productNum) throws IOException{
        List<String> lines = dbManager.readAccountFile(accountNumber);
        lines.set(productNum, "");
        dbManager.writeAccountFile(accountNumber,lines);
    }

    //추가
        public String getSavingsAmount(String accountNumber, int productIndex) throws IOException {
            List<String> lines = dbManager.readAccountFile(accountNumber);
            String productLine = lines.get(productIndex + 1); // 인덱스 1부터 적금 상품 정보 시작
            String[] productInfo = productLine.split("\t");
            return productInfo.length > 0 ? productInfo[0] : "0";
        }

}
