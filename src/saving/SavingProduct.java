package saving;

import java.time.YearMonth;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Scanner;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class SavingProduct {

    private int months;
    private double originalAnnualInterestRate;
    private double annualInterestRate;
    private int monthlyPayment;
    private int amount;
    private int currentMonths;
    private long currentMonths2;


    public SavingProduct( int months, double annualInterestRate, int monthlyPayment) {
        this.months = months;
        this.originalAnnualInterestRate = annualInterestRate;
        this.annualInterestRate = annualInterestRate;
        this.monthlyPayment = monthlyPayment;
    }

    // amount를 기반으로 현재까지 납입한 개월 수 계산 후 이율 조정
    public void adjustInterestRateBasedOnAmount(int amount) {
    this.amount = amount;
    // 현재까지 납입한 개월 수 계산
    this.currentMonths = amount / monthlyPayment;
    // 현재까지 납입한 개월 수에 따라 이율 조정
    if (currentMonths < (this.months * 0.2)) {
        this.annualInterestRate = this.originalAnnualInterestRate * 0.1;
    }
    else if ((this.months * 0.2) <= currentMonths && currentMonths < (this.months * 0.5)) {
        this.annualInterestRate = this.originalAnnualInterestRate * 0.5;
    }
    else if ((this.months * 0.5) <= currentMonths && currentMonths < (this.months)) {
        this.annualInterestRate = this.originalAnnualInterestRate * 0.8;
    }
    else {
        this.annualInterestRate = this.originalAnnualInterestRate;
    }
}

    public int calculateTotalInterest(int amount, int currentMonths) {
        return (int) Math.round(this.amount * (this.annualInterestRate / 100) / 12 * this.currentMonths);
    }

    public int calculateTotalAmount(int amount, int currentMonths) {
        return (int) Math.round(this.amount + calculateTotalInterest(amount, currentMonths));
    }

    public int getCurrentMonths() {
        return this.currentMonths;
    }

// amount를 기반으로 현재까지 납입한 개월 수 계산 후 이율 조정
public void adjustInterestRateBasedOnAmount2(int amount, String inputDateStr, String startDateStr) {
    this.amount = amount;
    // 현재까지 납입한 개월 수 계산
    this.currentMonths2 = getCurrentMonths2(inputDateStr, startDateStr);
    // 현재까지 납입한 개월 수에 따라 이율 조정
    if (currentMonths2 < (this.months * 0.2)) {
        this.annualInterestRate = this.originalAnnualInterestRate * 0.1;
    }
    else if ((this.months * 0.2) <= currentMonths2 && currentMonths2 < (this.months * 0.5)) {
        this.annualInterestRate = this.originalAnnualInterestRate * 0.5;
    }
    else if ((this.months * 0.5) <= currentMonths2 && currentMonths2 < (this.months)) {
        this.annualInterestRate = this.originalAnnualInterestRate * 0.8;
    }
    else {
        this.annualInterestRate = this.originalAnnualInterestRate;
    }
}

    public int calculateTotalInterest2(int amount, int currentMonths2) {
        return (int) Math.round(this.amount * (this.annualInterestRate / 100) / 12 * this.currentMonths2);
    }

    public int calculateTotalAmount2(int amount, int currentMonths2) {
        return (int) Math.round(this.amount + calculateTotalInterest(amount, currentMonths2));
    }

    public static long getCurrentMonths2(String inputDateStr, String startDateStr) {
        // 날짜 포맷 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // 문자열을 LocalDate 객체로 변환
        LocalDate dateOfLogin = LocalDate.parse(inputDateStr, formatter);
        LocalDate dateOfJoin = LocalDate.parse(startDateStr, formatter);
        
        // 두 날짜 사이의 차이를 개월 수로 계산
        long monthsBetween = ChronoUnit.MONTHS.between(dateOfJoin, dateOfLogin);
    
        return monthsBetween;
    }
}