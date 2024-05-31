package saving;

import database.AccountDao;
import database.DatabaseManager;
import database.DateDao;
import database.UserDao;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

public class CloseSavingService {
    private final Scanner scanner;
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final DecimalFormat decimalFormat;
    private final DateDao dateDao;
    private SavingServiceManager savingServiceManager;

    public CloseSavingService(SavingServiceManager savingServiceManager) {
        this.accountDao = new AccountDao(new DatabaseManager());
        this.userDao = new UserDao(new DatabaseManager());
        this.scanner = new Scanner(System.in);
        this.decimalFormat = new DecimalFormat("#,###");
        this.savingServiceManager = savingServiceManager;
        this.dateDao = new DateDao(new DatabaseManager());
    }

    public void doCloseService(String loggedInUserId) throws IOException {
        try {
            System.out.println();
            System.out.println("\n\n[예ㆍ적금 해지 서비스]");
            System.out.println("============================================");
            System.out.println("'q'를 입력할 시 이전 화면으로 돌아갑니다.");
            System.out.println("예ㆍ적금 해지 서비스를 시작합니다. 개인정보를 입력해주세요");
            System.out.println("============================================");

            while (true) {
                String inputName;
                String actualName;

                while (true) {
                    System.out.print("이름을 입력하세요 : ");
                    inputName = scanner.nextLine();

                    if ("q".equals(inputName)) {
                        return;
                    }

                    if (!inputName.matches("^[가-힣]+$")) {
                        System.out.println("올바른 양식이 아닙니다.");
                        continue;
                    }

                    if (inputName.length() < 2 || inputName.length() > 5) {
                        System.out.println("올바른 양식이 아닙니다.");
                        continue;
                    }

                    actualName = userDao.findUserNameById(loggedInUserId);
                    if (!inputName.equals(actualName)) {
                        System.out.println("존재하지 않는 이름입니다.");
                        continue;
                    }
                    break;
                }

                String inputRRN;

                while (true) {
                    System.out.print("주민등록번호를 입력하세요: ");
                    inputRRN = scanner.nextLine();
                    if ("q".equals(inputRRN)) {
                        savingServiceManager.printSavingMenu(loggedInUserId);
                        return;
                    }
                    if (!inputRRN.matches("\\d{6}-\\d{7}")) {
                        System.out.println("올바른 양식이 아닙니다.");
                        continue;
                    }

                    if (!inputRRN.equals(userDao.findUserToRRN(loggedInUserId))) {
                        System.out.println("존재하지 않는 주민등록번호입니다.");
                        continue;
                    }
                    break;
                }

                System.out.println();
                System.out.println("\n\n[예ㆍ적금 해지 서비스]");
                System.out.println("============================================");
                System.out.println("예ㆍ적금 조회 결과:");

                String account = userDao.findUserToAccount(loggedInUserId);
                boolean result1 = accountDao.hasSavings(account, 1);
                boolean result2 = accountDao.hasSavings(account, 2);
                boolean result3 = accountDao.hasSavings(account, 3);
                boolean result4 = accountDao.hasSavings(account, 4);

                if (result1) {
                    System.out.println("[1] 정기 예금");
                }
                if (result2) {
                    System.out.println("[2] 6개월 적금");
                }
                if (result3) {
                    System.out.println("[3] 12개월 적금");
                }
                if (result4) {
                    System.out.println("[4] 24개월 적금");
                }

                System.out.println("[0] 뒤로가기");
                System.out.println("============================================");

                while (true) {
                    System.out.print("해지하실 예ㆍ적금 번호를 입력하세요 : ");
                    String input = scanner.nextLine();
                    if (!input.matches("[0-4]")) {
                        System.out.println("상품의 숫자를 입력해주세요.");
                        continue;
                    }

                    int inputNum = Integer.parseInt(input);

                    if (inputNum == 1 && !result1) {
                        System.out.println("올바르지 않은 메뉴입니다.");
                        continue;
                    }
                    if (inputNum == 2 && !result2) {
                        System.out.println("올바르지 않은 메뉴입니다.");
                        continue;
                    }
                    if (inputNum == 3 && !result3) {
                        System.out.println("올바르지 않은 메뉴입니다.");
                        continue;
                    }
                    if (inputNum == 4 && !result4) {
                        System.out.println("올바르지 않은 메뉴입니다.");
                        continue;
                    }

                    long amount = Long.parseLong(accountDao.getAmount(account, inputNum - 1));
                    long totalReturnAmount;
                    double rate;
                    long interest;
                    String currentDate = dateDao.getDate();

                    switch (inputNum) {
                        case 1:
                            String saveDate = accountDao.getStartDate(account, 0);
                            String presentDate = dateDao.getDate();
                            int month = dateDao.calculateMonth(saveDate, presentDate);
                            rate = adjustRateSaving(month);
                            interest = (long) (amount * rate);
                            totalReturnAmount = amount + interest;
                            accountDao.executeTransaction(account, totalReturnAmount, "canceled", currentDate);
                            System.out.println("1번 상품 해지 결과");
                            System.out.println("원금 : " + decimalFormat.format(amount));
                            System.out.println("이자 : " + decimalFormat.format(interest));
                            System.out.println("합계 : " + decimalFormat.format(totalReturnAmount));
                            accountDao.removeSavings(account, 1);
                            break;
                        case 2:
                            rate = adjustRate(amount, 200000, 6, 0.02);
                            interest = (long) (amount * rate);
                            totalReturnAmount = amount + interest;
                            accountDao.executeTransaction(account, totalReturnAmount, "canceled", currentDate);
                            System.out.println("2번 상품 해지 결과");
                            System.out.println("원금 : " + decimalFormat.format(amount));
                            System.out.println("이자 : " + decimalFormat.format(interest));
                            System.out.println("합계 : " + decimalFormat.format(totalReturnAmount));
                            accountDao.removeSavings(account, 2);
                            break;
                        case 3:
                            rate = adjustRate(amount, 500000, 12, 0.03);
                            interest = (long) (amount * rate);
                            totalReturnAmount = amount + interest;
                            accountDao.executeTransaction(account, totalReturnAmount, "canceled", currentDate);
                            System.out.println("3번 상품 해지 결과");
                            System.out.println("원금 : " + decimalFormat.format(amount));
                            System.out.println("이자 : " + decimalFormat.format(interest));
                            System.out.println("합계 : " + decimalFormat.format(totalReturnAmount));
                            accountDao.removeSavings(account, 3);
                            break;
                        case 4:
                            rate = adjustRate(amount, 1000000, 24, 0.05);
                            interest = (long) (amount * rate);
                            totalReturnAmount = amount + interest;
                            accountDao.executeTransaction(account, totalReturnAmount, "canceled", currentDate);
                            System.out.println("4번 상품 해지 결과");
                            System.out.println("원금 : " + decimalFormat.format(amount));
                            System.out.println("이자 : " + decimalFormat.format(interest));
                            System.out.println("합계 : " + decimalFormat.format(totalReturnAmount));
                            accountDao.removeSavings(account, 4);
                            break;
                        case 0:
                            return;
                        default:
                            String num = Integer.toString(inputNum);
                            if (!num.matches("[0-9]")) {
                                System.out.println("상품의 숫자를 입력해주세요.");
                            } else {
                                System.out.println("올바르지 않은 메뉴입니다.");
                            }
                    }
                    System.out.println();
                    System.out.println();
                    break;
                }
                break;
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    //각각의 상품마다 납부해야 할 금액과 현재 납부된 금액을 비교한 이후 만기날짜 대비 비율을 계산
    public double adjustRate(long amount, long monthlyPayment, int months, double rate) {
        int currentMonths = (int) (amount / monthlyPayment);
        // 현재까지 납입한 개월 수에 따라 이율 조정

        if (currentMonths == 1)
           return 0;

        if (currentMonths < (months * 0.2))
            return rate * 0.1;

        if (currentMonths < (months * 0.5))
            return rate * 0.5;

        if (currentMonths < (months))
            return rate * 0.8;

        return -1;
    }

    public double adjustRateSaving(int month){
        //예금 신청 날짜와 현재 날짜의 개월수 차이 계산
        //개월수 차이에 따라 비율 차등 반환
        //기존 이율은 0.03;
        double rate = 0.03;

        if(month < 12 * 0.2)
            return rate * 0.1;

        if (month < 12 * 0.5)
            return rate * 0.5;

        if(month < 12)
            return rate * 0.8;

        return -1;
    }
}
