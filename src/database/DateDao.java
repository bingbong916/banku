package database;

import java.io.IOException;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DateDao {
    private final DatabaseManager dbManager;

    public DateDao(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setDate(String inputDate) throws Exception {
        validateDate(inputDate);

        int year = parseYear(inputDate);
        int month = parseMonth(inputDate);
        int date = parseDate(inputDate);


        List<String> lines = dbManager.readDateFile();
        String past = getDate();

        int pastYear = parseYear(past);
        int pastMonth = parseMonth(past);
        int pastDate = parseDate(past);


        if (year > pastYear) {
            lines.set(0, inputDate);
            dbManager.writeDateFile(lines);
            return;
        }

        if (year == pastYear) {
            if (month > pastMonth) {
                lines.set(0, inputDate);
                dbManager.writeDateFile(lines);
                return;
            } else if (month == pastMonth) {
                if (date >= pastDate) {
                    lines.set(0, inputDate);
                    dbManager.writeDateFile(lines);
                    return;
                }
            }
        }

        throw new IOException("에러");
    }

    public String getDate() throws IOException {
        List<String> lines = dbManager.readDateFile();
        return lines.get(0);
    }

    public void validateDate(String inputDate) throws Exception {

        if (inputDate.length() != 8)
            throw new DateTimeParseException("입력된 월이나 일이 유효 범위를 초과함", inputDate, 0);

        int[] daysInMonth = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        int year = Integer.parseInt(inputDate.substring(0, 4));
        int month = Integer.parseInt(inputDate.substring(4, 6));
        int day = Integer.parseInt(inputDate.substring(6, 8));


        if (month < 1 || month > 12 || day < 1 || day > daysInMonth[month]) {
            throw new Exception();
        }
    }

    public int parseYear(String date){
        return Integer.parseInt(date.substring(0,4));
    }

    public int parseMonth(String date){
        return Integer.parseInt(date.substring(4,6));
    }

    public int parseDate(String date){
        return Integer.parseInt(date.substring(6,8));
    }
}
