package saving;

public class SavingProduct {

    private long months;
    private double rate;
    private long monthlyPayment;
    private long currentMonths;

    public SavingProduct(int months, double rate, long monthlyPayment) {
        this.months = months;
        this.rate = rate;
        this.monthlyPayment = monthlyPayment;
    }

    // amount를 기반으로 현재까지 납입한 개월 수 계산 후 이율 조정
    public void adjustRate(long amount) {
        currentMonths = amount / monthlyPayment;
      // 현재까지 납입한 개월 수에 따라 이율 조정

        if (currentMonths == 0)
            rate = 0;

        if (currentMonths < (months * 0.2))
            rate = rate * 0.1;

        if ((this.months * 0.2) <= currentMonths && currentMonths < (months * 0.5))
             rate = rate * 0.5;

        if ((this.months * 0.5) <= currentMonths && currentMonths < (months))
            rate = rate * 0.8;
    }

    public long calculateInterest(long amount, long currentMonths) {
        return (int) Math.round(amount * (rate / 100) / 12 * currentMonths);
    }

    public long calculateAmount(long amount, long currentMonths) {
        return (int) Math.round(amount + calculateInterest(amount, currentMonths));
    }

    public long getCurrentMonths(long amount) {
        return amount / monthlyPayment;
    }
}