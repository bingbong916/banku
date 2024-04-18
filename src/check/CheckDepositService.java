package check;

import database.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.IOException;

public class CheckDepositService {
    private final UserDao userDao;
    private static AccountDao accountDao;
    private final Scanner scanner;

    public CheckDepositService(UserDao userDao, AccountDao accountDao){
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
        AccountDao accountDao = new AccountDao(dbManager);
    }

    public static String addMonths(String startDate, int months) {
        LocalDate date = LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE);

        LocalDate expDate = date.plusMonths(months);
        
        return expDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber.matches("\\d{6}-\\d{7}");
    }


    public static void checkDeposit(String loggedInUserId) {
        Scanner scan = new Scanner(System.in);

        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
        AccountDao accountDao = new AccountDao(dbManager);

        
        try {
            String storedRRN = userDao.findUserToRRN(loggedInUserId);
            String storedName = userDao.findUserToName(loggedInUserId);    
            
            System.out.println("\n\n[예·적금 조회 서비스]");
            System.out.println("============================================");
            System.out.println("('q를 입력할 시 이전 화면으로 돌아갑니다.')");

            String userName;
            String userRRN;
            while(true) {
                System.out.print("이름을 입력하세요: ");
                userName = scan.nextLine();
                if(userName.equals("q")) {
                    System.out.println("이전으로 돌아갑니다.");
                    return;
                }
                if(!userName.matches("^[가-힣]*$")){
                    System.out.println("이름의 형식이 잘못되었습니다.");
                    continue;
                }
                if(userName.length() < 2 || userName.length() > 3){
                    System.out.println("이름의 길이가 잘못되었습니다.");
                    continue;
                }
                if(!(userName.equals(storedName))) {
                    System.out.println("존재하지 않는 이름입니다.");
                    return;
                }
                else break;
            }

            while(true) {
                System.out.print("주민번호를 입력하세요(입력 예시: 991106-010101): ");
                userRRN = scan.nextLine();
                if(userRRN.equals("q")) {
                    // checkManager.printCheckingMenu(loggedInUserId);
                    System.out.println("이전으로 돌아갑니다.");
                    return;
                }
                if(!isValidAccountNumber(userRRN)) {
                    System.out.println("올바른 주민번호를 입력하세요!");
                    continue;
                }
                if(!(userRRN.equals(storedRRN))) {
                    System.out.println("존재하지 않는 주민번호입니다.");
                    return;
                }
                else break;
            }

            //이름&주민번호 일치
            System.out.println("\n\n[예·적금 조회 서비스]");
            System.out.println("============================================");
            System.out.println("예·적금 조회 결과:");


            String account = userDao.findUserToAccount(loggedInUserId);
            boolean result1 = accountDao.hasSavings(account, 1);
            boolean result2 = accountDao.hasSavings(account, 2);
            boolean result3 = accountDao.hasSavings(account, 3);
            boolean result4 = accountDao.hasSavings(account, 4);

            String startDate1 = accountDao.getStartDate(account, 1);
            String startDate2 = accountDao.getStartDate(account, 2);
            String startDate3 = accountDao.getStartDate(account,3);
            String startDate4 = accountDao.getStartDate(account, 4);

            String carryBack = accountDao.getCarryBack(account);

            int resultNumber = 0;

            if (result1) {
                resultNumber++;
                System.out.println(resultNumber + ")");
                System.out.println("상품명: 정기 예금");
                System.out.println("만기일: " + addMonths(startDate1, 12));
                System.out.println("예상 환급액: ₩ " + carryBack);
                System.out.println(" ");

            }
            if (result2) {
                resultNumber++;
                System.out.println(resultNumber + ")");
                System.out.println("6개월 적금");
                System.out.println("만기일: " + addMonths(startDate2, 6));
                System.out.println("예상 환급액: ₩ 1,200,000");
                System.out.println(" ");

            }
            if (result3) {
                resultNumber++;
                System.out.println(resultNumber + ")");
                System.out.println("12개월 적금");
                System.out.println("만기일: " + addMonths(startDate3, 12));
                System.out.println("예상 환급액: ₩ 6,180,000");
                System.out.println(" ");
            }
            if (result4) {
                resultNumber++;
                System.out.println(resultNumber + ")");
                System.out.println("24개월 적금");
                System.out.println("만기일: " + addMonths(startDate4, 24));
                System.out.println("예상 환급액: ₩ 25,830,000");
            }
            if (!result1 & !result2 & !result3 & !result4) {
                System.out.println("가입된 예·적금이 없습니다.");
            }

            System.out.println("============================================");
        }
        catch (Exception e){
            e.getMessage();
        }
    }
}