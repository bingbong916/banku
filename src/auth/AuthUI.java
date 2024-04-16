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

        System.out.print("아이디를 입력하세요: ");
        String userId = scanner.nextLine();
        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();

        System.out.print("이름을 입력하세요: ");
        String name = scanner.nextLine();

        System.out.print("주민등록번호를 입력하세요 (예: 991106-1234567): ");
        String rrn = scanner.nextLine(); // resident registration number

        registrationService.registerUser(userId, password, name, rrn);
    }

    private String loginUser() {
        System.out.println("============================================");
        System.out.println("[로그인 서비스]");
        System.out.println("============================================");
        System.out.println("로그인을 시작합니다.");

        System.out.print("아이디를 입력하세요: ");
        String userId = scanner.nextLine();
        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();
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