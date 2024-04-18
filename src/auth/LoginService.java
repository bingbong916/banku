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

    public String login(String userId, String password, String inputDateStr) { // 날짜 추가
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
