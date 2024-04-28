import auth.AuthUI;
import bank.MainMenu;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        AuthUI authUI = new AuthUI();
        authUI.showMenu();
    }
}