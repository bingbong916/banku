package auth;

import bank.MainMenu;
import database.UserDao;
import java.io.IOException;
import java.util.List;

public class LoginService {
    private final UserDao userDao;

    public LoginService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean checkUserId(String userId) throws IOException {
        List<String> lines = userDao.readUserFile();
        for (String line : lines) {
            String[] userDetails = line.split("\t");
            if (userDetails[0].equals(userId)) {
                return true; // 아이디가 존재하면 true 반환
            }
        }
        return false; // 아이디가 존재하지 않으면 false 반환
    }

    public boolean verifyUserPassword(String userId, String password) throws IOException {
        List<String> lines = userDao.readUserFile();
        for (String line : lines) {
            String[] userDetails = line.split("\t");
            if (userDetails[0].equals(userId) && userDetails[1].equals(password)) {
                return true; // 아이디와 비밀번호가 일치하면 true 반환
            }
        }
        return false; // 일치하지 않으면 false 반환
    }
    public String login(String userId, String password) {
        try {
            List<String> lines = userDao.readUserFile();
            for (String line : lines) {
                String[] userDetails = line.split("\t");
                if (userDetails[0].equals(userId) && userDetails[1].equals(password)) {
                    return userId;
                }
            }
            System.out.println("아이디 또는 비밀번호가 잘못되었습니다.");
        } catch (IOException e) {
            System.out.println("로그인 중 오류가 발생했습니다: " + e.getMessage());
        }
        return null;
    }
}