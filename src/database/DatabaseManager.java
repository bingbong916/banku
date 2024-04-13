package database;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class DatabaseManager {
    private static final String USER_FILE = "user.txt";

    public List<String> readUserFile() throws IOException {
        Path path = Paths.get(USER_FILE);
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public void writeUserFile(List<String> lines) throws IOException {
        Path path = Paths.get(USER_FILE);
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    public List<String> readAccountFile(String accountNumber) throws IOException {
        Path path = Paths.get(accountNumber + ".txt");
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }

    public void writeAccountFile(String accountNumber, List<String> lines) throws IOException {
        Path path = Paths.get(accountNumber + ".txt");
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

}
