package auth;
import bank.MainMenu;

import database.DatabaseManager;
import database.DateDao;
import database.UserDao;

import java.time.YearMonth;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Scanner;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;



public class AuthUI {
    private RegistrationService registrationService;
    private LoginService loginService;
    private UserDao userDao;
    private final Scanner scanner;
    private DateDao dateDao;

    public AuthUI() {
        this.scanner = new Scanner(System.in);
        initializeServices();
    }
    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        userDao = new UserDao(dbManager);
        this.registrationService = new RegistrationService(userDao);
        this.loginService = new LoginService(userDao);
        dateDao = new DateDao(dbManager);
    }
    public void showMenu() throws IOException {
        String loggedInUserId = null;
        while (true) {
            System.out.println("\n\n[회원가입·로그인 서비스]");
            System.out.println("============================================");
            System.out.println("회원가입 또는 로그인을 해주세요");
            System.out.println("============================================");
            System.out.println("[1] 회원가입 서비스");
            System.out.println("[2] 로그인 서비스");
            System.out.println("[0] 종료하기");
            System.out.println("============================================");

            boolean validInput = false;
            while (!validInput) {
                System.out.print("선택하실 메뉴 번호를 입력하세요 (0-2): ");
                String selected = scanner.nextLine();
                switch (selected) {
                    case "1":
                        registerUser();
                        validInput = true;
                        break;
                    case "2":
                        loggedInUserId = loginUser();
                        if (loggedInUserId != null) {
                            MainMenu mainMenu = new MainMenu(loggedInUserId);
                            mainMenu.show();
                        }
                        validInput = true;
                        break;
                    case "0":
                        System.out.println("서비스를 종료합니다.");
                        return;
                    default:
                        System.out.println("잘못된 입력입니다. 다시 번호를 입력해주세요 (0-2)");
                        break;
                }
            }
        }
    }

    private void registerUser() {

        String userId = "";
        String password = "";
        String name = "";
        String rrn = "";

        System.out.println("\n\n============================================");
        System.out.println("[회원가입 서비스]");
        System.out.println("============================================");
        System.out.println("회원가입을 시작합니다.");


        // 아이디
        while (true) {
            System.out.print("아이디를 입력하세요: ");
            userId = scanner.nextLine();

            if (userId == null || userId.isEmpty()) {
                System.out.println("아이디를 입력해주세요");
                System.out.println("");
                continue;
            }

            else if (!userId.matches("[a-z0-9]+")) {
                System.out.println("소문자 영어와 숫자의 조합으로 이루어져야 합니다.");
                System.out.println("");
                continue;
            }

            else if (userId.length() < 6 || userId.length() > 10) {
                System.out.println("6자 이상 10자 이하로 입력해주세요.");
                System.out.println("");
                continue;
            } else
                try {
                    if(userDao.checkDuplicateUserId(userId)) {
                        System.out.println("아이디가 이미 존재합니다.");
                        System.out.println("");
                        continue;
                    }

                    else {

                        break;
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }

        // 비밀번호
        while (true) {
            System.out.print("비밀번호를 입력하세요: ");
            password = scanner.nextLine();

            if (password == null || password.isEmpty()) {
                System.out.println("비밀번호를 입력해주세요.");
                System.out.println("");
                continue;
            }

            else if (!password.matches("\\d+")) {
                System.out.println("비밀번호는 숫자만 포함해야 합니다.");
                System.out.println("");
                continue;
            }

            else if (password.length() != 6) {
                System.out.println("비밀번호는 6자리 숫자로 이루어져야 합니다.");
                System.out.println("");
                continue;
            }

            break;
        }

        // 이름
        while (true) {
            System.out.print("이름을 입력하세요: ");
            name = scanner.nextLine();

            if (name == null || name.isEmpty()) {
                System.out.println("이름을 입력해주세요.");
                System.out.println("");
                continue;
            }

            else if (!name.matches("[ㄱ-ㅎㅏ-ㅣ가-힣]+")) {
                System.out.println("이름은 한글로만 구성되어야 합니다.");
                System.out.println("");
                continue;
            }
            if (!name.matches("^[가-힣]+$")) {
                System.out.println("이름은 조합된 한글 글자로만 입력해주세요.");
                System.out.println("");
                continue;
            }
            else if (name.length() < 2 || name.length() > 5) {
                System.out.println("2자 이상 5자 이하로 입력해주세요.");
                System.out.println("");
                continue;
            }

            break;
        }

        // 주민등록번호
        while (true) {
            System.out.print("주민등록번호를 입력하세요: (6자리 숫자 - 7자리 숫자) ");
            rrn = scanner.nextLine();

            if (rrn == null || rrn.isEmpty()) {
                System.out.println("주민등록번호를 입력해주세요.");
                System.out.println("");
                continue;
            }

            else if (!rrn.matches("\\d{6}-\\d{7}")) {
                System.out.println("주민등록번호는 (6자리 숫자)-(7자리 숫자) 형식이어야 합니다.");
                System.out.println("");
                continue;
            } else
                try {
                    if (userDao.checkDuplicateRRN(rrn)) {
                        System.out.println("주민등록번호가 이미 존재합니다.");
                        System.out.println("");
                        continue;
                    }

                    else {
                        int[] daysInMonth = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

                        String birthDate = rrn.substring(0, 6);

                        try {
                            int month = Integer.parseInt(birthDate.substring(2, 4));
                            int day = Integer.parseInt(birthDate.substring(4, 6));

                            if (month < 1 || month > 12 || day < 1 || day > daysInMonth[month]) {
                                throw new DateTimeParseException("입력된 월이나 일이 유효 범위를 초과함", birthDate, 0);
                            }

                        } catch (DateTimeParseException e) {
                            System.out.println("유효하지 않은 생년월일입니다.");
                            System.out.println("");
                            continue;
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            break;
        }
        try {
            registrationService.registerUser(userId, password, name, rrn);

        } catch (Exception e) {
            System.out.println("회원가입 중 오류가 발생했습니다: " + e.getMessage());
            System.out.println("");
        }

    }

    private String loginUser() {
        System.out.println("\n\n============================================");
        System.out.println("[로그인 서비스]");
        System.out.println("============================================");
        System.out.println("로그인을 시작합니다.");

        String userId;
        String password;

        // 아이디
        while (true) {
            System.out.print("아이디를 입력하세요: ");
            userId = scanner.nextLine();

            if (userId.isEmpty()) {
                System.out.println("아이디를 입력해주세요");
                System.out.println("");
                continue;
            } else if (!userId.matches("[a-z0-9]+")) {
                System.out.println("소문자 영어와 숫자의 조합으로 이루어져야 합니다.");
                System.out.println("");
                continue;
            } else if (userId.length() < 6 || userId.length() > 10) {
                System.out.println("6자 이상 10자 이하로 입력해주세요.");
                System.out.println("");
                continue;
            } else {
                try {
                    if (!loginService.checkUserId(userId)) {
                        System.out.println("존재하지 않는 아이디입니다.\n");
                        System.out.println("");
                        continue;
                    }
                } catch (IOException e) {
                    System.out.println("에러가 발생했습니다: " + e.getMessage());
                    System.out.println("");
                    continue;
                }
            }
            break;
        }

        // 비밀번호
        while (true) {
            System.out.print("비밀번호를 입력하세요: ");
            password = scanner.nextLine();

            if (password.isEmpty()) {
                System.out.println("비밀번호를 입력해주세요.");
                System.out.println("");
                continue;
            } else if (!password.matches("\\d+")) {
                System.out.println("비밀번호는 숫자만 포함해야 합니다.");
                System.out.println("");
                continue;
            } else if (password.length() != 6) {
                System.out.println("비밀번호는 6자리 숫자로 이루어져야 합니다.");
                System.out.println("");
                continue;
            } else {
                try {
                    if (!loginService.verifyUserPassword(userId, password)) {
                        System.out.println("일치하지 않는 비밀번호 입니다.\n");
                        System.out.println("");
                        continue;
                    }
                } catch (IOException e) {
                    System.out.println("에러가 발생했습니다: " + e.getMessage());
                    System.out.println("");
                    continue;
                }
            }
            System.out.println("로그인 성공!");
            break;
        }

        String inputDate = "";

        while (true) {
            try {
                String pastDate = dateDao.getDate();
                System.out.println("이전 날짜 :" + pastDate);
                System.out.println();
                System.out.print("오늘 날짜를 입력하세요: "); // 오늘 날짜 입력받기 추가
                inputDate = scanner.nextLine();
                dateDao.setDate(inputDate);
            } catch (Exception e){
                System.out.println("유효하지 않은 날짜입니다.");
                System.out.println("");
                continue;
            }
            break;
        }


        String loggedInUserId = loginService.login(userId, password);

        if (loggedInUserId != null) {
            System.out.println("로그인 성공!");
            System.out.println("오늘 날짜: " + inputDate); // 입력받은 날짜 확인용
            System.out.println("");
            return userId;
        } else {
//            System.out.println("로그인 실패");
            return null;
        }
    }
}