package database;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountDao {
    private final DatabaseManager dbManager;

    public AccountDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void createAccount(String accountNumber, long balance, String date) throws IOException {
        List<String> lines = Arrays.asList("0", "", "", "", "");
        dbManager.writeAccountFile(accountNumber, lines);
        executeTransaction(accountNumber, balance, "deposit", date);
    }

    public void updateSavings(String accountNumber, int productIndex, String amount, String startDate) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String productLine = amount + "\t" + startDate;
        lines.set(productIndex + 1, productLine);
        dbManager.writeAccountFile(accountNumber, lines);
    }

    public long getBalance(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String balanceStr = lines.get(0);
        return Long.parseLong(balanceStr);
    }

    public String getBalanceToString(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String balanceStr = lines.get(0);
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(Long.parseLong(balanceStr));
    }

    public void showTransactionLog(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        System.out.println("============================================");

        List<List<String>> dateSections = new ArrayList<>();
        List<String> currentSection = new ArrayList<>();

        if (lines.size() > 5) {
            for (int i = 5; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.matches("\\d+년 \\d+월 \\d+일")) {
                    if (!currentSection.isEmpty()) {
                        dateSections.add(new ArrayList<>(currentSection));
                        currentSection.clear();
                    }
                }
                currentSection.add(line);
            }

            if (!currentSection.isEmpty()) {
                dateSections.add(currentSection);
            }

            Collections.reverse(dateSections);
            for (List<String> section : dateSections) {
                for (String s : section) {
                    System.out.println(s);
                }
            }
        }
    }


    public String getStartDate(String accountNumber, int index) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        List<String> subList = lines.subList(1, lines.size());

        String str = subList.get(index);
        if (!str.isEmpty()) {
            String[] parts = str.split("\t");
            return parts[1];
        }
        return accountNumber;
    }

    public String getCarryBack(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String carryBackStr = lines.get(1);
        if (!carryBackStr.isEmpty()) {
            String[] parts = carryBackStr.split("\t");
            DecimalFormat decimalFormat = new DecimalFormat("###,###");
            String input = parts[0];
            Long carryBack = (Long) Math.round(Double.parseDouble(input) + Double.parseDouble(input) * 0.03);
            return decimalFormat.format(carryBack);
        }
        return accountNumber;
    }

    public int executeTransaction(String accountNumber, long money, String type, String date) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        long oldBalance = Long.parseLong(lines.get(0));
        DecimalFormat decimalFormat = new DecimalFormat("₩ #,###");
        String formattedDate = parseDate(date);

        boolean dateExists = false;
        StringBuilder transactionLog = new StringBuilder();

        for (int i = 5; i < lines.size(); i++) {
            if (lines.get(i).startsWith(formattedDate)) {
                dateExists = true;
                break;
            }
        }

        if (!dateExists) {
            transactionLog.append(formattedDate).append("\n");
        }
        switch (type) {
            case "deposit": // 입금
                try {
                    oldBalance = Math.addExact(money, oldBalance);
                } catch (ArithmeticException e) {
                    return -1; // 입금 long 형 범위 초과
                }
                transactionLog.append("입금:\t+ ").append(decimalFormat.format(money)).append("\t").append(decimalFormat.format(oldBalance));
                break;
                
            case "compound": // 복리 추가
                try {
                    oldBalance = Math.addExact(money, oldBalance);
                } catch (ArithmeticException e) {
                    return -1; // 입금 long 형 범위 초과
                }
                transactionLog.append("복리 입금:\t+ ").append(decimalFormat.format(money)).append("\t").append(decimalFormat.format(oldBalance));
                break;

            case "withdrawal": // 출금
                if (oldBalance - money < 0) {
                    return -1; // 잔고보다 더 많은 돈을 출금할 때
                }
                oldBalance -= money;
                transactionLog.append("출금:\t- ").append(decimalFormat.format(money)).append("\t").append(decimalFormat.format(oldBalance));
                break;

            case "savings": // 적금
                if (oldBalance - money < 0) {
                    return -1; // 적금 금액보다 더 많은 돈을 출금할 때
                }
                oldBalance -= money;
                transactionLog.append("적금:\t- ").append(decimalFormat.format(money)).append("\t").append(decimalFormat.format(oldBalance));
                break;

            case "canceled": // 적금 해지
                try {
                    oldBalance = Math.addExact(money, oldBalance);
                } catch (ArithmeticException e) {
                    return -1; // 입금 long 형 범위 초과
                }
                transactionLog.append("적금 해지:\t+ ").append(decimalFormat.format(money)).append("\t").append(decimalFormat.format(oldBalance));
                break;

            default:
                return -2; // 비정상 요청 타입
        }
        lines.set(0, String.valueOf(oldBalance));
        lines.add(transactionLog.toString());
        dbManager.writeAccountFile(accountNumber, lines);

        return 0;
    }

    public void executeTransferTransaction(String accountNumber, long money, String counterpartyName, String type, String date) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        long oldBalance = Long.parseLong(lines.get(0));
        DecimalFormat decimalFormat = new DecimalFormat("₩ #,###");
        String formattedDate = parseDate(date);

        boolean dateExists = false;
        StringBuilder transactionLog = new StringBuilder();

        for (int i = 5; i < lines.size(); i++) {
            if (lines.get(i).startsWith(formattedDate)) {
                dateExists = true;
                break;
            }
        }

        if (!dateExists) {
            transactionLog.append(formattedDate).append("\n");
        }

        switch (type) {
            case "sender": // 송금 - 보내는 사람
                if (oldBalance + money < 0) {
                    return; // 송금 금액이 잔액보다 많음
                }
                oldBalance += money;
                transactionLog.append(counterpartyName).append(":\t- ").append(decimalFormat.format(-money)).append("\t").append(decimalFormat.format(oldBalance));
                break;

            case "receiver": // 송금 - 받는 사람
                oldBalance += money;
                transactionLog.append(counterpartyName).append(":\t+ ").append(decimalFormat.format(money)).append("\t").append(decimalFormat.format(oldBalance));
                break;

            default:
                return; // 비정상 요청 타입
        }

        lines.set(0, String.valueOf(oldBalance));

        if (!transactionLog.toString().isEmpty()) {
            lines.add(transactionLog.toString());
        }

        dbManager.writeAccountFile(accountNumber, lines);
    }

    public String showSavings(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(Long.parseLong(lines.get(0)));
    }

    public boolean hasSavings(String accountNumber, int index) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        return !lines.get(index).trim().isEmpty();
    }

    public void removeSavings(String accountNumber, int productNum) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        lines.set(productNum, "");
        dbManager.writeAccountFile(accountNumber, lines);
    }

    public void addSavings(String accountNumber, long money, int productIndex) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String line = lines.get(productIndex);
        String[] parts = line.split("\t");
        String startDate = getStartDate(accountNumber, productIndex - 1);
        long oldSavings = Long.parseLong(parts[0]);
        long newSavings = oldSavings + money;

        lines.set(productIndex, newSavings + "\t" + startDate);
        dbManager.writeAccountFile(accountNumber, lines);
    }

    private String parseDate(String rawDate) {
        int yearInt = Integer.parseInt(rawDate.substring(0, 4));
        int month = Integer.parseInt(rawDate.substring(4, 6));
        int day = Integer.parseInt(rawDate.substring(6, 8));
        
        // 년도를 문자열로 변환합니다.
        String year = String.valueOf(yearInt);
        
        // 년도가 4자리가 아닌 경우 앞에 0을 추가합니다.
        while (year.length() < 4) {
            year = "0" + year;
        }
        
        return year + "년 " + month + "월 " + day + "일";
    }


    public String getAmount(String accountNumber, int index) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        List<String> subList = lines.subList(1, lines.size());

        String str = subList.get(index);
        if (!str.isEmpty()) {
            String[] parts = str.split("\t");
            return parts[0];
        }
        return null;
    }
    
    
 // 복리 계산
    public void calculateAndDepositInterest(String accountNumber, DateDao dateDao) throws IOException {
        System.out.println("복리 계산이 실행되었습니다. -1");
        
        // 입출금 내역이 없을 때 오류 방지를 위한 early return
        if(dateDao.getLastUpdatedDate(accountNumber) == null) {
        	return;
        }
        
        // 같은 달이 아닐 경우에만 복리를 계산하고 입금합니다.
        if (!dateDao.isSameYearAndMonth(accountNumber)) {
            System.out.println("복리 계산이 실행되었습니다 -2");
            // 계좌 파일을 읽어옵니다.
            List<String> lines = dbManager.readAccountFile(accountNumber);
            System.out.println("복리 계산이 실행되었습니다 -3");
            // 복리의 합을 저장할 변수를 초기화합니다.
            long interestSum = 0;
            // 마지막으로 업데이트된 날짜와 잔액을 저장할 변수를 초기화합니다.
            String lastDate = null;
            long lastBalance = 0;
            String currentDate = null;
            long currentBalance = 0;

            // 계좌 파일의 각 줄을 순회하면서 복리를 계산합니다.
            for (int i = 5; i < lines.size(); i++) {
                String line = lines.get(i);
//                System.out.println("line : " + line);
                if (line.trim().isEmpty()) {
                    continue;
                }

                // 각 줄에서 날짜를 추출합니다.
                Matcher matcher = Pattern.compile("(\\d+년 \\d+월 \\d+일)").matcher(line);
                if (matcher.find()) {
                    // 현재 날짜를 가져옵니다.
                	String year = matcher.group(1).split("년")[0].trim();
                	String month = matcher.group(1).split("년")[1].split("월")[0].trim();
                    String day = matcher.group(1).split("월")[1].replace("일", "").trim();
                    
                 
                    // 년도가 4자리가 아닌 경우 앞에 0을 추가합니다.
                    while (year.length() < 4) {
                        year = "0" + year;
                    }
                    
                    // 월과 일이 한 자리수인 경우 앞에 0을 추가합니다.

                    if (month.length() == 1) {
                        month = "0" + month;
                    }
                    if (day.length() == 1) {
                        day = "0" + day;
                    }
                    
                    currentDate = year +  month + day;  // 현재 연도를 추가합니다.
                    
                    System.out.println("currentDate : " + currentDate);
                    
                 // 현재 날짜가 마지막으로 업데이트된 날짜의 달에 속하지 않으면, 해당 줄을 건너뛴다.
                    if (!dateDao.isSameYearAndMonthInLine(accountNumber, currentDate)) {
                    	System.out.println("건너뜀");
                    	currentDate = null;
                        continue;
                        
                    }
                }

                else {
                	// 해당 줄에서 금액을 추출합니다.
                	if(currentDate == null) {
                		continue;
                	}
                	else {
                		String[] parts = line.split("\\s+");
                        if (parts.length > 0) {
                            String balancePart = parts[parts.length - 1];
                            balancePart = balancePart.replace("₩", "").replace(",", "");
                            currentBalance = Long.parseLong(balancePart);
                            System.out.println("currentBalance : " + currentBalance);
                        }
                	}
                    
                }
                

             // 다음 줄의 날짜를 가져옵니다.
                if (i < lines.size() - 1) {
                	Matcher nextMatcher1 = Pattern.compile("(\\d+년 \\d+월 \\d+일)").matcher(lines.get(i + 1));
                    boolean isFound = nextMatcher1.find();
                    System.out.println("isFound 진입 전");
                    System.out.println(isFound);
                    if (isFound) {
                        System.out.println("isFound 진입 후");

                        // 다음 날짜를 가져옵니다.
                        String nextYear = nextMatcher1.group(1).split("년")[0].trim();
                        String nextMonth = nextMatcher1.group(1).split("년")[1].split("월")[0].trim();
                        String nextDay = nextMatcher1.group(1).split("월")[1].replace("일", "").trim();
                        
                        // 년도가 4자리가 아닌 경우 앞에 0을 추가합니다.
                        while (nextYear.length() < 4) {
                            nextYear = "0" + nextYear;
                        }
                        

                        // 월과 일이 한 자리수인 경우 앞에 0을 추가합니다.
                        if (nextMonth.length() == 1) {
                            nextMonth = "0" + nextMonth;
                        }
                        if (nextDay.length() == 1) {
                            nextDay = "0" + nextDay;
                        }
                        
                        String nextDate = nextYear + nextMonth + nextDay;

                        // 현재 날짜와 다음 날짜 사이의 복리를 계산합니다.
                        long daysBetween = dateDao.calculateDaysBetween(currentDate, nextDate);
                        double interest = calculateInterest(currentBalance, daysBetween);
                        // 계산된 복리를 복리의 합에 더합니다.
                        interestSum += interest;
                    }
                }
                
                System.out.println("isFound는 false");
                // 마지막으로 업데이트된 날짜와 잔액을 업데이트합니다.
                lastDate = currentDate;
                lastBalance = currentBalance;
                System.out.println("currentDate" + currentDate);
                System.out.println("lastDate" + lastDate);
                System.out.println("currentBalance" + currentBalance);
                System.out.println("lastBalance" + lastBalance);
            }

            // 복리 입금 진행
         // 마지막으로 입금된 날짜를 가져옵니다.
            String lastDepositDate = lastDate;
            System.out.println("lastDepositDate : " + lastDepositDate);

            // depositDate를 마지막으로 입금된 날짜의 다음 월의 첫날로 설정합니다.
            String depositDate = LocalDate.parse(lastDepositDate, DateTimeFormatter.ofPattern("yyyyMMdd")).plusMonths(1).withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            System.out.println("depositDate : " + depositDate);
            System.out.println("복리 계산이 실행되었습니다 -4");
            System.out.println("depositDate : " + depositDate);
            System.out.println("dateDao.getDate().substring(0, 6) + \"01\" : " + dateDao.getDate().substring(0, 6) + "01");
            
            while (LocalDate.parse(depositDate, DateTimeFormatter.ofPattern("yyyyMMdd")).isBefore(LocalDate.parse(dateDao.getDate(), DateTimeFormatter.ofPattern("yyyyMMdd")))) {
                // 마지막으로 업데이트된 날짜와 입금 날짜 사이의 복리를 계산합니다.
            	System.out.println("복리 계산이 실행되었습니다 -5");
                long daysBetween = dateDao.calculateDaysBetween(lastDate, depositDate);
                double interest = calculateInterest(lastBalance, daysBetween);
                
                System.out.println("Before interestsum : " + interestSum);

                // 계산된 복리를 복리의 합에 더합니다.
                interestSum += interest;
                // 계산된 복리의 합을 입금합니다.
                executeTransaction(accountNumber, interestSum, "compound", depositDate);

                // 복리를 계산하고 입금한 후에 lastDate와 lastBalance를 업데이트합니다.
                lastDate = depositDate;
                lastBalance += interestSum;
                
             // 복리를 계산하고 입금한 후에 interestSum을 0으로 초기화합니다.
                interestSum = 0;


                // 다음 달의 첫날로 입금 날짜를 업데이트합니다.
                depositDate = LocalDate.parse(depositDate, DateTimeFormatter.ofPattern("yyyyMMdd")).plusMonths(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
            
        }
    }



    // 복리 계산 공식
    private double calculateInterest(double balance, long days) {
        // 연간 이자율을 2%로 가정합니다.
        double annualInterestRate = 0.02;
        // 복리 공식을 사용하여 복리를 계산합니다.
        return balance * Math.pow(1 + annualInterestRate, days / 365.0) - balance;
    }

    
    
}
