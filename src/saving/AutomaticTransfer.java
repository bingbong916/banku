package saving;

import database.AccountDao;
import database.DatabaseManager;
import database.DateDao;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class AutomaticTransfer {
    private final AccountDao accountDao;
    private final DateDao dateDao;
    private static final String ACCOUNT_DIR = "account";

    public AutomaticTransfer() {
        this.accountDao = new AccountDao(new DatabaseManager());
        this.dateDao = new DateDao(new DatabaseManager());
    }

    public void doService() throws IOException {
        List<String> accountList = listFilesInDirectory();
        for(String account : accountList){
            autoSaving(account);
            matureSaving(account);
        }
    }

    public static List<String> listFilesInDirectory() {
        List<String> fileNames = new ArrayList<>();
        Path path = Paths.get(ACCOUNT_DIR);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    String fileName = entry.getFileName().toString();
                    if (fileName.endsWith(".txt")) {
                        fileName = fileName.substring(0, fileName.lastIndexOf(".txt"));
                    }
                    fileNames.add(fileName);
                }
            }
        } catch (IOException | DirectoryIteratorException e) {
            System.err.println("Error reading directory: " + e.getMessage());
        }

        return fileNames;
    }


    //적금 자동이체 로직
    public void autoSaving(String account) throws IOException {
        //적금 1번 상품 - 6개월 , 이율 2.0, 월 200000
        if(accountDao.hasSavings(account, 2)) {
            if (accountDao.getBalance(account) >= 200000) {
                accountDao.withdrawalSavings(account, 200000);
                accountDao.addSavings(account, 200000, 2);
            }
        }

        //적금 2번 상품 - 12개월 , 이율 3.0, 월 500000
        if(accountDao.hasSavings(account, 3)) {
            if (accountDao.getBalance(account) >= 500000) {
                accountDao.withdrawalSavings(account, 500000);
                accountDao.addSavings(account, 500000, 3);
            }
        }

        //적금 3번 상품 - 24개월 , 이율 5.0, 월 1000000
        if(accountDao.hasSavings(account, 4)) {
            if (accountDao.getBalance(account) >= 1000000) {
                accountDao.withdrawalSavings(account, 1000000);
                accountDao.addSavings(account, 1000000, 4);
            }
        }
    }


    //예적금 만기 해지 로직
    public void matureSaving(String account) throws IOException {
        //에금 만기 - 12개월, 이율 3.0
        if(accountDao.hasSavings(account, 1)){
            String start = accountDao.getStartDate(account, 0);
            String present = dateDao.getDate();


            int startYear = dateDao.parseYear(start);
            int presentYear = dateDao.parseYear(present);

            if(startYear + 1 <= presentYear){
                //돌려줄 돈 계산
                //잔고에 추가
                //예금 라인 삭제(개행 처리)
                long savings = accountDao.getSavings(account, 0);
                // 이자 계산 (단리)
                long interest = (long) (savings * 0.03);
                accountDao.depositSavings(account, interest);
                accountDao.removeSavings(account, 1);

            }
        }

        //적금 1번 상품 만기
        if(accountDao.hasSavings(account, 2)){
            //적금 만기 시 납부 금액과 같을 경우 만기 해지
            long presentAmount = Long.parseLong(accountDao.getAmount(account, 1));
            if(presentAmount == 1200000){
                long interest = (long) (1200000 * 0.02);
                accountDao.depositSavings(account, interest);
                accountDao.removeSavings(account, 2);
            }

        }

        //적금 2번 상품 만기
        if(accountDao.hasSavings(account, 3)){
            long presentAmount = Long.parseLong(accountDao.getAmount(account, 2));
            if(presentAmount == 6500000){
                long interest = (long) (6500000 * 0.03);
                accountDao.depositSavings(account, interest);
                accountDao.removeSavings(account, 3);
            }
        }

        //적금 3번 상품 만기
        if(accountDao.hasSavings(account, 4)){
            long presentAmount = Long.parseLong(accountDao.getAmount(account, 3));
            if(presentAmount == 24000000){
                long interest = (long) (24000000 * 0.05);
                accountDao.depositSavings(account, interest);
                accountDao.removeSavings(account, 4);
            }
        }

    }
}
