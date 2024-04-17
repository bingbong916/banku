package auth;

import database.UserDao;

import java.io.IOException;

public class RegistrationService {
    private final UserDao userDao;

    public RegistrationService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void registerUser(String userId, String password, String name, String rrn) {
        try {
                userDao.createUser(userId, password, name, rrn); 
                System.out.println("회원가입이 완료되었습니다!");
            
        } catch (IOException e) {
            System.out.println("회원가입 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
