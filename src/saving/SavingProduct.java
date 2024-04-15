package saving;

public class SavingProduct {

    private int months;
    private double originalAnnualInterestRate;
    private double annualInterestRate;
    private int monthlyPayment;
    private int amount;
    private int currentMonths;


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
}