package database;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

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
}
