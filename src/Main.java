import auth.AuthUI;
import database.DatabaseManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        DatabaseManager databaseManager = new DatabaseManager();
        databaseManager.makeFiles();
        AuthUI authUI = new AuthUI();
        authUI.showMenu();
    }
}