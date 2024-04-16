package auth;

import bank.MainMenu;
import database.DatabaseManager;
import database.UserDao;

import java.util.Scanner;

public class AuthUI {
    private RegistrationService registrationService;
    private LoginService loginService;
    private final Scanner scanner;

    public AuthUI() {
        this.scanner = new Scanner(System.in);
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
        this.registrationService = new RegistrationService(userDao);
        this.loginService = new LoginService(userDao);
    }


    public void showMenu() {
        String loggedInUserId = null;

        while (true) {
            System.out.println("[회원가입·로그인 서비스]");
            System.out.println("============================================");
            System.out.println("회원가입 또는 로그인을 해주세요");
            System.out.println("============================================");
            System.out.println("[1] 회원가입 서비스");
            System.out.println("[2] 로그인 서비스");
            System.out.println("[0] 종료하기");
            System.out.println("============================================");
            System.out.print("선택하실 메뉴 번호를 입력하세요 (0-2): ");

            String selected = scanner.nextLine();
            switch (selected) {
                case "1":
                    registerUser();
                    break;
                case "2":
                    loggedInUserId = loginUser();
                    if (loggedInUserId != null) {
                        MainMenu mainMenu = new MainMenu(loggedInUserId);
                        mainMenu.show();
                    }
                case "0":
                    System.out.println("서비스를 종료합니다.");
                    return;
                default:
                    System.out.println("잘못된 입력입니다. 다시 시도해주세요.");
                    break;
            }
        }
    }

    private void registerUser() {
        System.out.println("============================================");
        System.out.println("[회원가입 서비스]");
        System.out.println("============================================");
        System.out.println("회원가입을 시작합니다.");

        // 아이디
        while (true) {
            System.out.print("아이디를 입력하세요: ");
            String userId = scanner.nextLine();

            if (userId == null || userId.isEmpty()) {
                System.out.println("아이디를 입력해주세요");
                continue;
            }

            else if (userId.length() < 6 || userId.length() > 10) {
                System.out.println("6자 이상 10자 이하로 입력해주세요.");
                continue;
            }

            else if (!userId.matches("[a-z0-9]+")) {
                System.out.println("소문자 영어와 숫자의 조합으로 이루어져야 합니다.");
                continue;
            }
            
            else {
            	break;
            }
        }
        
     // Password input
        while (true) {
            System.out.print("비밀번호를 입력하세요: ");
            String password = scanner.nextLine();

            if (password == null || password.isEmpty()) {
                System.out.println("비밀번호를 입력해주세요.");
                continue;
            }

            else if (!password.matches("\\d+")) {
                System.out.println("비밀번호는 숫자만 포함해야 합니다.");
                continue;
            }

            else if (password.length() != 6) {
                System.out.println("비밀번호는 6자리 숫자로 이루어져야 합니다.");
                continue;
            }

            break;
        }

        // 이름
        while (true) {
            System.out.print("이름을 입력하세요: ");
            String name = scanner.nextLine();

            if (name == null || name.isEmpty()) {
                System.out.println("이름을 입력해주세요.");
                continue;
            }

            else if (name.length() < 2 || name.length() > 5) {
                System.out.println("2자 이상 5자 이하로 입력해주세요.");
                continue;
            }

            else if (!name.matches("[가-힣]+")) {
                System.out.println("이름은 한글로만 구성되어야 합니다.");
                continue;
            }

            else if (!name.matches("^[ㄱ-ㅎㅏ-ㅣ가-힣]*$")) {
                System.out.println("이름은 자음과 모음의 조합으로 이루어져야합니다.");
                continue;
            }

            break;
        }

        // 주민등록번호
        while (true) {
            System.out.print("주민등록번호를 입력하세요: ");
            String rrn = scanner.nextLine();

            if (rrn == null || rrn.isEmpty()) {
                System.out.println("주민등록번호를 입력해주세요.");
                continue;
            }

            else if (!rrn.matches("\\d+")) {
                System.out.println("주민등록번호는 숫자만 포함해야 합니다.");
                continue;
            }

            else if (rrn.length() != 13) {
                System.out.println("주민등록번호는 13자리 숫자로 이루어져야 합니다.");
                continue;
            }

            break;
        }
        
        System.out.println("회원가입이 완료되었습니다!");
        
    }

    private String loginUser() {
        System.out.println("============================================");
        System.out.println("[로그인 서비스]");
        System.out.println("============================================");
        System.out.println("로그인을 시작합니다.");

        // 아이디
        while (true) {
            System.out.print("아이디를 입력하세요: ");
            String userId = scanner.nextLine();

            if (userId == null || userId.isEmpty()) {
                System.out.println("아이디를 입력해주세요");
                continue;
            }

            else if (userId.length() < 6 || userId.length() > 10) {
                System.out.println("6자 이상 10자 이하로 입력해주세요.");
                continue;
            }

            else if (!userId.matches("[a-z0-9]+")) {
                System.out.println("소문자 영어와 숫자의 조합으로 이루어져야 합니다.");
                continue;
            }

            break;
        }

        // 비밀번호
        while (true) {
            System.out.print("비밀번호를 입력하세요: ");
            String password = scanner.nextLine();

            if (password == null || password.isEmpty()) {
                System.out.println("비밀번호를 입력해주세요.");
                continue;
            }

            else if (!password.matches("\\d+")) {
                System.out.println("비밀번호는 숫자만 포함해야 합니다.");
                continue;
            }

            else if (password.length() != 6) {
                System.out.println("비밀번호는 6자리 숫자로 이루어져야 합니다.");
                continue;
            }

            break;
        }

        String loggedInUserId = loginService.login(userId, password);

        if (loggedInUserId != null) {
            System.out.println("로그인 성공!");
            return userId;
        } else {
            System.out.println("로그인 실패");
            return null;
        }
    }
}
