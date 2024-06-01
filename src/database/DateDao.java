package database;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateDao {
    private final DatabaseManager dbManager;

    public DateDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setDate(String inputDate) throws Exception {
        validateDate(inputDate);

        List<String> lines = dbManager.readDateFile();
        String pastDate = getDate();

        // 첫 번째 줄에 현재 날짜를, 두 번째 줄에 이전 날짜를 유지
        if (lines.size() < 2) {
            lines.add(pastDate);
        } else {
            lines.set(1, pastDate);
        }
        lines.set(0, inputDate);

        dbManager.writeDateFile(lines);
    }

    public String getDate() throws IOException {
        List<String> lines = dbManager.readDateFile();
        return lines.get(0);
    }

    public String getLastSavedDate() throws IOException {
        List<String> lines = dbManager.readDateFile();
        return lines.size() > 1 ? lines.get(1) : null;
    }

    public void validateDate(String inputDate) throws Exception {
        if (inputDate.length() != 8) {
            throw new DateTimeParseException("입력된 날짜는 8자리여야 합니다.", inputDate, 0);
        }

        try {
            LocalDate.parse(inputDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("유효하지 않은 날짜 형식입니다.", inputDate, 0);
        }
    }

    public int parseYear(String date) {
        return Integer.parseInt(date.substring(0, 4));
    }

    public int parseMonth(String date) {
        return Integer.parseInt(date.substring(4, 6));
    }

    public int parseDay(String date) {
        return Integer.parseInt(date.substring(6, 8));
    }

    public int calculateMonth(String pastDate, String presentDate) {
        int pastYear = parseYear(pastDate);
        int pastMonth = parseMonth(pastDate);

        int presentYear = parseYear(presentDate);
        int presentMonth = parseMonth(presentDate);

        // 연도와 월을 이용하여 개월 수 계산
        int yearDifference = presentYear - pastYear;
        return yearDifference * 12 + (presentMonth - pastMonth);
    }
    
    
    // 날짜 차이 계산 메서드
    public long calculateDaysBetween(String date1, String date2) throws DateTimeParseException {
        // 현재 연도를 기준으로 두 날짜를 파싱
        LocalDate localDate1 = parseDateWithCurrentYear(date1);
        LocalDate localDate2 = parseDateWithCurrentYear(date2);

        // 두 날짜가 연도를 넘어갈 경우를 고려
        if (localDate1.isAfter(localDate2)) {
            localDate2 = localDate2.plusYears(1);
        }

        return java.time.temporal.ChronoUnit.DAYS.between(localDate1, localDate2);
    }

    // 날짜 차이 계산을 위해 임시로 yyyyMMdd date 생성 메서드
    private LocalDate parseDateWithCurrentYear(String date) {
        int currentYear = LocalDate.now().getYear();
        return LocalDate.parse(currentYear + date, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }
    
    
    // 마지막으로 업데이트된 날짜와 오늘이 같은 달인지 확인하는 메서드(맞으면 true를, 아니면 false return)
    public boolean isSameMonth(String accountNumber) throws IOException {
        // 가장 마지막에 업데이트된 날짜를 가져옵니다.
        String lastUpdatedDate = getLastUpdatedDate(accountNumber);
        // 오늘 날짜를 가져옵니다.
        String today = getDate();

        // 두 날짜가 같은 달인지 확인합니다.
        return getMonthFromAccountFileDate(lastUpdatedDate) == getMonthFromDateString(today);
    }

    // account/계좌번호.txt에서 마지막으로 업데이트 된 날짜를 가져오는 메서드
    private String getLastUpdatedDate(String accountNumber) throws IOException {
        List<String> lines = dbManager.readAccountFile(accountNumber);
        String lastLine = lines.get(lines.size() - 1);
        Pattern pattern = Pattern.compile("(\\d+월 \\d+일)");
        Matcher matcher = pattern.matcher(lastLine);
        if (matcher.find()) {
            return matcher.group(1).replace("월 ", "").replace("일", "");
        } else {
            throw new IOException("마지막 업데이트된 날짜를 찾을 수 없습니다.");
        }
    }

    // account/계좌번호.txt에서 몇 월인지 가져오는 메서드
    private int getMonthFromAccountFileDate(String date) {
        return Integer.parseInt(date.split(" ")[0]);
    }

    // getDate에서 month 정보만 가져오는 메서드
    private int getMonthFromDateString(String date) {
        return Integer.parseInt(date.substring(4, 6));
    }


}
