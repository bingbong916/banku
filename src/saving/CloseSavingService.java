package saving;

import database.AccountDao;
import database.DatabaseManager;
import database.UserDao;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;

public class CloseSavingService {
    private final Scanner scanner;
    private final UserDao userDao;
    private final AccountDao accountDao;
    private final DecimalFormat decimalFormat;
    private SavingServiceManager savingServiceManager;

    public CloseSavingService(SavingServiceManager savingServiceManager) {
        this.accountDao = new AccountDao(new DatabaseManager());
        this.userDao = new UserDao(new DatabaseManager());
        this.scanner = new Scanner(System.in);
        this.decimalFormat = new DecimalFormat("#,###");
        this.savingServiceManager = savingServiceManager; //추가
    }


    public void doCloseService(String loggedInUserId) throws IOException {
        try {
            System.out.println();
            System.out.println("\n\n[예ㆍ적금 해지 서비스]");
            System.out.println("============================================");
            System.out.println("'q'를 입력할 시 이전 화면으로 돌아갑니다.");
            System.out.println("예ㆍ적금 해지 서비스를 시작합니다. 개인정보를 입력해주세요");
            System.out.println("============================================");

            boolean flag = true;
            while (true) {
                String inputName;
                String actualName;

                while (true) {
                    System.out.print("이름을 입력하세요 : ");
                    inputName = scanner.nextLine();

                    if ("q".equals(inputName)) {
                        return;
                    } //추가

                    if (!inputName.matches("^[가-힣]+$")) {
                        System.out.println("올바른 양식이 아닙니다.");     //메세지 뭐라 쓸지
                        continue;
                    }

                    if (inputName.length() < 2 || inputName.length() > 5) {
                        System.out.println("올바른 양식이 아닙니다.");     //메세지 뭐라 쓸지
                        continue;
                    }

                    //추가
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
                    } //추가
                    if (!inputRRN.matches("\\d{6}-\\d{7}")) {
                        System.out.println("올바른 양식이 아닙니다.");    //메세지 뭐라 쓸지
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
                    //추가
                    String input = scanner.nextLine();
                    if (!input.matches("[0-4]")) {
                        System.out.println("상품의 숫자를 입력해주세요.");
                        continue;
                    }


                    int inputNum = Integer.parseInt(input);

                    if(inputNum == 1 && !result1){
                        System.out.println("올바르지 않은 메뉴입니다.");
                        continue;
                    }
                    if(inputNum == 2 && !result2){
                        System.out.println("올바르지 않은 메뉴입니다.");
                        continue;
                    }
                    if(inputNum == 3 && !result3){
                        System.out.println("올바르지 않은 메뉴입니다.");
                        continue;
                    }
                    if(inputNum == 4 && !result4){
                        System.out.println("올바르지 않은 메뉴입니다.");
                        continue;
                    }
                    if(inputNum == 0){
                        return;
                    }

                    String accountNumber = userDao.findUserToAccount(loggedInUserId);

                    //현재 예적금 금액
                    long amount = Long.parseLong(accountDao.getAmount(accountNumber, inputNum - 1));
                    long currentMonths;
                    long totalReturnAmount;
                    long currentBalance;
                    long newBalance;

                switch (inputNum){
                    case 1: //예금
                        SavingProduct product1 = new SavingProduct(12, 3.0, amount);
                        product1.adjustRate(amount);
                        currentMonths = product1.getCurrentMonths(amount);
                        totalReturnAmount = product1.calculateAmount(amount, currentMonths);
                        // 현재 계좌 잔액 조회
                        currentBalance = accountDao.getBalance(accountNumber);
                        // 적금 해지 금액을 현재 계좌에 합치기
                        newBalance = currentBalance + totalReturnAmount;
                        // 계좌 잔액 업데이트
                        accountDao.updateBalance(accountNumber, newBalance);
                        System.out.println("1번 상품 해지 결과");
                        System.out.println("원금 : " + decimalFormat.format(amount));
                        System.out.println("이자 : " + decimalFormat.format(product1.calculateInterest(amount, currentMonths)));
                        System.out.println("합계 : " + decimalFormat.format(product1.calculateAmount(amount, currentMonths)));
                        accountDao.removeSavings(accountNumber,1);
                        break;
                    case 2:
                        SavingProduct product2 = new SavingProduct(6, 2.0, 200000);
                        product2.adjustRate(amount);
                        currentMonths = product2.getCurrentMonths(amount);
                        totalReturnAmount = product2.calculateAmount(amount, currentMonths);
                        // 현재 계좌 잔액 조회
                        currentBalance = accountDao.getBalance(accountNumber);
                        // 적금 해지 금액을 현재 계좌에 합치기
                        newBalance = currentBalance + totalReturnAmount;
                        // 계좌 잔액 업데이트
                        accountDao.updateBalance(accountNumber, newBalance);
                        System.out.println("2번 상품 해지 결과");
                        System.out.println("원금 : " + decimalFormat.format(amount));
                        System.out.println("이자 : " + decimalFormat.format(product2.calculateInterest(amount, currentMonths)));
                        System.out.println("합계 : " + decimalFormat.format(product2.calculateAmount(amount, currentMonths)));
                        accountDao.removeSavings(accountNumber,2);
                        break;
                    case 3:
                        SavingProduct product3 = new SavingProduct(12, 3.0, 500000);
                        product3.adjustRate(amount);
                        currentMonths = product3.getCurrentMonths(amount);
                        totalReturnAmount = product3.calculateAmount(amount, currentMonths);
                        // 현재 계좌 잔액 조회
                        currentBalance = accountDao.getBalance(accountNumber);
                        // 적금 해지 금액을 현재 계좌에 합치기
                        newBalance = currentBalance + totalReturnAmount;
                        // 계좌 잔액 업데이트
                        accountDao.updateBalance(accountNumber, newBalance);
                        System.out.println("3번 상품 해지 결과");
                        System.out.println("원금 : " + decimalFormat.format(amount));
                        System.out.println("이자 : " + decimalFormat.format(product3.calculateInterest(amount, currentMonths)));
                        System.out.println("합계 : " + decimalFormat.format(product3.calculateAmount(amount, currentMonths)));
                        accountDao.removeSavings(accountNumber,3);
                        break;
                    case 4:
                        SavingProduct product4 = new SavingProduct(24, 5.0, 1000000);
                        product4.adjustRate(amount);
                        currentMonths = product4.getCurrentMonths(amount);
                        totalReturnAmount = product4.calculateAmount(amount, currentMonths);
                        // 현재 계좌 잔액 조회
                        currentBalance = accountDao.getBalance(accountNumber);
                        // 적금 해지 금액을 현재 계좌에 합치기
                        newBalance = currentBalance + totalReturnAmount;
                        // 계좌 잔액 업데이트
                        accountDao.updateBalance(accountNumber, newBalance);
                        System.out.println("4번 상품 해지 결과");
                        System.out.println("원금 : " + decimalFormat.format(amount));
                        System.out.println("이자 : " + decimalFormat.format(product4.calculateInterest(amount, currentMonths)));
                        System.out.println("합계 : " + decimalFormat.format(product4.calculateAmount(amount, currentMonths)));
                        accountDao.removeSavings(accountNumber,4);
                        break;
                    default:
                        String num = Integer.toString(inputNum);
                        if(!num.matches("[0-9]")){
                            System.out.println("상품의 숫자를 입력해주세요.");
                        }
                        else{
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
}

