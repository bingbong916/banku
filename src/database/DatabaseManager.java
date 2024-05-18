package database;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class DatabaseManager {
    private static final String USER_FILE = "user.txt";
    private static final String DATE_FILE = "date.txt";
    private static final String ACCOUNT_DIR = "account";

    public void makeFiles() throws IOException {
        Path user_path = Paths.get(USER_FILE);
        if (!Files.exists(user_path)) {
            Files.createFile(user_path);
        }
        Path date_path = Paths.get(DATE_FILE);
        if (!Files.exists(date_path)) {
            Files.write(date_path, Collections.singletonList("00010101"), StandardCharsets.UTF_8);
        }
        Path accountDirPath = Paths.get(ACCOUNT_DIR);
        if (!Files.exists(accountDirPath)) {
            Files.createDirectory(accountDirPath);
        }
    }
    public List<String> readUserFile() throws IOException {
        Path path = Paths.get(USER_FILE);
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public void writeUserFile(List<String> lines) throws IOException {
        Path path = Paths.get(USER_FILE);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public List<String> readAccountFile(String accountNumber) throws IOException {
        Path path = Paths.get(ACCOUNT_DIR, accountNumber + ".txt");
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public void writeAccountFile(String accountNumber, List<String> lines) throws IOException {
        Path path = Paths.get(ACCOUNT_DIR, accountNumber + ".txt");
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public List<String> readDateFile() throws IOException {
        Path path = Paths.get(DATE_FILE);
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public void writeDateFile(List<String> lines) throws IOException {
        Path path = Paths.get(DATE_FILE);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }
}
