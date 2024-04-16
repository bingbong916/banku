package database;

import java.io.IOException;
import java.util.List;

public class UserDao {
    private final DatabaseManager dbManager;

    public UserDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public List<String> readUserFile() throws IOException {
        return dbManager.readUserFile();
    }

    public void createUser(String userId, String password, String name, String rrn) throws IOException {
        List<String> lines = readUserFile();
        String userLine = userId + "\t" + password + "\t" + name + "\t" + rrn;
        lines.add(userLine);
        dbManager.writeUserFile(lines);
    }

    public void addAccountToUser(String userId, String accountNumber) throws IOException {
        List<String> lines = readUserFile();
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).startsWith(userId + "\t")) {
                lines.set(i, lines.get(i) + "\t" + accountNumber);
                break;
            }
        }
        dbManager.writeUserFile(lines);
    }

    public boolean hasAccount(String userId) throws IOException {
        List<String> lines = dbManager.readUserFile();
        for (String line : lines) {
            if (line.startsWith(userId + "\t")) {
                // 계좌 번호가 이미 있는지 확인
                String[] parts = line.split("\t");
                if (parts.length > 4) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public String getAccountNumber(String userId) throws IOException {
        List<String> lines = dbManager.readUserFile();
        for (String line : lines) {
            if (line.startsWith(userId + "\t")) {
                String[] parts = line.split("\t");
                if (parts.length > 4) {
                    return parts[4];
                }
                break;
            }
        }
        return userId;
    }

    public String findUserToName(String userId) throws IOException {
        List<String> lines = dbManager.readUserFile();
        for (String line : lines) {
            String[] textId = line.split("\t");
            if (textId[0].equals(userId)) {
                return textId[2];
            }
        }
        return userId;
    }

    public String findUserToAccount(String userId) throws IOException {
        List<String> lines = dbManager.readUserFile();
        for (String line : lines) {
            String[] textId = line.split("\t");
            if (textId[0].equals(userId)) {
                return textId[4];
            }
        }
        return userId;
    }

    public String findUserToRRN(String userId) throws IOException {
        List<String> lines = dbManager.readUserFile();
        for (String line : lines) {
            String[] textId = line.split("\t");
            if (textId[0].equals(userId)) {
                return textId[3];
            }
        }
        return userId;
<<<<<<< HEAD
=======
    }

    //추가
    public String findUserNameById(String userId) throws IOException {
        List<String> lines = readUserFile();
        for (String line : lines) {
            String[] parts = line.split("\t");
            if (parts[0].equals(userId)) {
                return parts[2];
            }
        }
        return null;
>>>>>>> upstream/main
    }
}
