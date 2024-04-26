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
    // 추가
    private SavingProduct product1;
    private SavingProduct product2;
    private SavingProduct product3;
    private SavingProduct product4;
    //private boolean backToPreviousMenu = false; //추가
    private SavingServiceManager savingServiceManager;

    public CloseSavingService(UserDao userDao, AccountDao accountDao, SavingProduct product1, SavingProduct product2, SavingProduct product3, SavingProduct product4, SavingServiceManager savingServiceManager) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.scanner = new Scanner(System.in);
        this.product1 = product1;
        this.product2 = product2;
        this.product3 = product3;
        this.product4 = product4;
        this.decimalFormat = new DecimalFormat("#,###");
        this.savingServiceManager = savingServiceManager; //추가
        initializeServices();
    }

    private void initializeServices() {
        DatabaseManager dbManager = new DatabaseManager();
        UserDao userDao = new UserDao(dbManager);
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
                    inputName = inputName.replaceAll(" ", "");
                    if ("q".equals(inputName)) {
                        savingServiceManager.printSavingMenu(loggedInUserId);
                        return;
                    } //추가

                    if (!inputName.matches("^[가-힣]*$")) {
                        System.out.println("이름의 형식이 잘못되었습니다.");     //메세지 뭐라 쓸지
                        continue;
                    }

                    if (inputName.length() < 2 || inputName.length() > 3) {
                        System.out.println("이름의 길이가 잘못되었습니다.");     //메세지 뭐라 쓸지
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
                        System.out.println("주민등록번호 형식에 어긋납니다.");    //메세지 뭐라 쓸지
                        continue;
                    }

                    if (!inputRRN.equals(userDao.findUserToRRN(loggedInUserId))) {
                        System.out.println("존재하지 않는 주민등록번호입니다.");
                        return;
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
                    System.out.println("[4] 24개월 저금");
                }

                System.out.println("[0] 뒤로가기");
                System.out.println("============================================");

                while (flag) {
                    System.out.print("해지하실 예ㆍ적금 번호를 입력하세요 : ");
                    //추가
                    String input = scanner.nextLine();
                    if (!input.matches("[0-4]")) {
                        System.out.println("상품의 숫자를 입력해주세요.");
                        continue;
                    }

                    int inputNum = Integer.parseInt(input);
                    String accountNumber = userDao.findUserToAccount(loggedInUserId);
                    String amountStr = accountDao.getSavingsAmount(accountNumber, inputNum - 1).replaceAll(",", ""); // , 제거
                    int amount = Integer.parseInt(amountStr);

                    switch (inputNum) {
                        case 1: //예금
                            if (!result1) {
                                System.out.println("올바르지 않은 메뉴입니다.");
                                continue;
                            }
                            product1.adjustInterestRateBasedOnAmount(amount);
                            int currentMonths = product1.getCurrentMonths();
                            int totalReturnAmount = product1.calculateTotalAmount(amount, currentMonths);
                            // 현재 계좌 잔액 조회
                            int currentBalance = accountDao.getBalance(accountNumber);
                            // 적금 해지 금액을 현재 계좌에 합치기
                            int newBalance = currentBalance + totalReturnAmount;
                            // 계좌 잔액 업데이트
                            accountDao.updateBalance(accountNumber, newBalance);
                            System.out.println(currentMonths);
                            System.out.println("1번 상품 해지 결과");
                            System.out.println("원금 : " + decimalFormat.format(amount));
                            System.out.println("이자 : " + decimalFormat.format(product1.calculateTotalInterest(amount, currentMonths)));
                            System.out.println("합게 : " + decimalFormat.format(product1.calculateTotalAmount(amount, currentMonths)));
                            accountDao.removeSavings(accountNumber, 1);
                            flag = false;
                            break;
                        case 2:
                            if (!result2) {
                                System.out.println("올바르지 않은 메뉴입니다.");
                                continue;
                            }
                            product2.adjustInterestRateBasedOnAmount(amount);
                            currentMonths = product2.getCurrentMonths();
                            totalReturnAmount = product2.calculateTotalAmount(amount, currentMonths);
                            // 현재 계좌 잔액 조회
                            currentBalance = accountDao.getBalance(accountNumber);
                            // 적금 해지 금액을 현재 계좌에 합치기
                            newBalance = currentBalance + totalReturnAmount;
                            // 계좌 잔액 업데이트
                            accountDao.updateBalance(accountNumber, newBalance);
                            System.out.println("2번 상품 해지 결과");
                            System.out.println("원금 : " + decimalFormat.format(amount));
                            System.out.println("이자 : " + decimalFormat.format(product2.calculateTotalInterest(amount, currentMonths)));
                            System.out.println("합게 : " + decimalFormat.format(product2.calculateTotalAmount(amount, currentMonths)));
                            accountDao.removeSavings(accountNumber, 2);
                            flag = false;
                            break;
                        case 3:
                            if (!result3) {
                                System.out.println("올바르지 않은 메뉴입니다.");
                                continue;
                            }
                            product3.adjustInterestRateBasedOnAmount(amount);
                            currentMonths = product3.getCurrentMonths();
                            totalReturnAmount = product3.calculateTotalAmount(amount, currentMonths);
                            // 현재 계좌 잔액 조회
                            currentBalance = accountDao.getBalance(accountNumber);
                            // 적금 해지 금액을 현재 계좌에 합치기
                            newBalance = currentBalance + totalReturnAmount;
                            // 계좌 잔액 업데이트
                            accountDao.updateBalance(accountNumber, newBalance);
                            System.out.println("3번 상품 해지 결과");
                            System.out.println("원금 : " + decimalFormat.format(amount));
                            System.out.println("이자 : " + decimalFormat.format(product3.calculateTotalInterest(amount, currentMonths)));
                            System.out.println("합게 : " + decimalFormat.format(product3.calculateTotalAmount(amount, currentMonths)));
                            accountDao.removeSavings(accountNumber, 3);
                            flag = false;
                            break;
                        case 4:
                            if (!result4) {
                                System.out.println("올바르지 않은 메뉴입니다.");
                                continue;
                            }
                            product4.adjustInterestRateBasedOnAmount(amount);
                            currentMonths = product4.getCurrentMonths();
                            totalReturnAmount = product4.calculateTotalAmount(amount, currentMonths);
                            // 현재 계좌 잔액 조회
                            currentBalance = accountDao.getBalance(accountNumber);
                            // 적금 해지 금액을 현재 계좌에 합치기
                            newBalance = currentBalance + totalReturnAmount;
                            // 계좌 잔액 업데이트
                            accountDao.updateBalance(accountNumber, newBalance);
                            System.out.println("4번 상품 해지 결과");
                            System.out.println("원금 : " + decimalFormat.format(amount));
                            System.out.println("이자 : " + decimalFormat.format(product4.calculateTotalInterest(amount, currentMonths)));
                            System.out.println("합게 : " + decimalFormat.format(product4.calculateTotalAmount(amount, currentMonths)));
                            accountDao.removeSavings(accountNumber, 4);
                            flag = false;
                            break;
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
                } break;
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}

