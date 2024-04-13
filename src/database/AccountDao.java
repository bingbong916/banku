package database;

import java.io.IOException;
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

    public void depositSavings(String accountNumber, int money) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        int savings = money + Integer.parseInt(lines.get(0));
        lines.set(0, Integer.toString(savings)); // 잔액 index = 0
        dbManager.writeAccountFile(accountNumber, lines);
    }

    public void withdrawalSavings (String accountNumber, int money) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        int savings = Integer.parseInt(lines.get(0)) - money;
        lines.set(0, Integer.toString(savings)); // 잔액 index = 0
        dbManager.writeAccountFile(accountNumber, lines);
    }

    public String showSavings (String accountNumber) throws IOException{
        List<String> lines = dbManager.readAccountFile(accountNumber);
        return lines.get(0);
    }
}
