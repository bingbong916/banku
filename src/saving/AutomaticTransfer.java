package saving;

import database.AccountDao;
import database.DatabaseManager;
import database.DateDao;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AutomaticTransfer {
    private final AccountDao accountDao;
    private final DateDao dateDao;
    private static final String ACCOUNT_DIR = "account";

    public AutomaticTransfer() {
        this.accountDao = new AccountDao(new DatabaseManager());
        this.dateDao = new DateDao(new DatabaseManager());
    }

    public void doService(String pastDate, String presentDate) throws IOException, Exception {
        int monthsBetween = dateDao.calculateMonth(pastDate, presentDate);
        ExecutorService executorService = Executors.newFixedThreadPool(10); // 적절한 스레드 풀 크기를 설정하세요.

        for (int i = 0; i < monthsBetween; i++) {
            List<String> accountList = listFilesInDirectory();
            String currentProcessingDate = incrementMonth(pastDate);

            List<CompletableFuture<Void>> futures = accountList.stream()
                    .map(account -> CompletableFuture.runAsync(() -> {
                        try {
                            accountDao.calculateAndDepositInterest(account, dateDao);
                            matureSaving(account, currentProcessingDate);
                            autoSaving(account, currentProcessingDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, executorService))
                    .collect(Collectors.toList());

            // 모든 작업이 완료될 때까지 기다림
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            pastDate = currentProcessingDate;
        }

        executorService.shutdown();
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

    public void autoSaving(String account, String date) throws IOException {
        if (accountDao.hasSavings(account, 2)) {
            if (accountDao.getBalance(account) >= 200000) {
                accountDao.executeTransaction(account, 200000, "savings", date);
                accountDao.addSavings(account, 200000, 2);
            }
        }

        if (accountDao.hasSavings(account, 3)) {
            if (accountDao.getBalance(account) >= 500000) {
                accountDao.executeTransaction(account, 500000, "savings", date);
                accountDao.addSavings(account, 500000, 3);
            }
        }

        if (accountDao.hasSavings(account, 4)) {
            if (accountDao.getBalance(account) >= 1000000) {
                accountDao.executeTransaction(account, 1000000, "savings", date);
                accountDao.addSavings(account, 1000000, 4);
            }
        }
    }

    private String incrementMonth(String date) {
        int year = dateDao.parseYear(date);
        int month = dateDao.parseMonth(date);

        if (month == 12) {
            year++;
            month = 1;
        } else {
            month++;
        }

        return String.format("%04d%02d01", year, month);
    }

    public void matureSaving(String account, String date) throws IOException {
        //적금 만기 - 12개월, 이율 3.0
        if (accountDao.hasSavings(account, 1)) {
            String start = accountDao.getStartDate(account, 0);
            String present = date;

            int startYear = dateDao.parseYear(start);
            int startMonth = dateDao.parseMonth(start);
            int presentYear = dateDao.parseYear(present);
            int presentMonth = dateDao.parseMonth(present);

            if (startYear * 12 + startMonth + 12 <= presentYear * 12 + presentMonth) {
                long savings = Long.parseLong(accountDao.getAmount(account, 0));
                long interest = (long) (savings * 0.03);
                long total = savings + interest;
                accountDao.executeTransaction(account, total, "canceledYegeum", date);
                accountDao.removeSavings(account, 1);
            }
        }

        if (accountDao.hasSavings(account, 2)) {
            long presentAmount = Long.parseLong(accountDao.getAmount(account, 1));
            if (presentAmount == 1200000) {
                long interest = (long) (1200000 * 0.02);
                long total = presentAmount + interest;
                accountDao.executeTransaction(account, total, "canceled", date);
                accountDao.removeSavings(account, 2);
            }
        }

        if (accountDao.hasSavings(account, 3)) {
            long presentAmount = Long.parseLong(accountDao.getAmount(account, 2));
            if (presentAmount == 6500000) {
                long interest = (long) (6500000 * 0.03);
                long total = presentAmount + interest;
                accountDao.executeTransaction(account, total, "canceled", date);
                accountDao.removeSavings(account, 3);
            }
        }

        if (accountDao.hasSavings(account, 4)) {
            long presentAmount = Long.parseLong(accountDao.getAmount(account, 3));
            if (presentAmount == 24000000) {
                long interest = (long) (24000000 * 0.05);
                long total = presentAmount + interest;
                accountDao.executeTransaction(account, total, "canceled", date);
                accountDao.removeSavings(account, 4);
            }
        }
    }
}
