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
    public int getBalance(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        // Assuming the balance is stored as the first line in the account file
        String balanceStr = lines.get(0);
        return Integer.parseInt(balanceStr);
    }

    public void updateBalance(String accountNumber, int newBalance) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        // Update balance in the first line
        lines.set(0, String.valueOf(newBalance));
        dbManager.writeAccountFile(accountNumber, lines);
    }
}
