package saving;

public class SavingProduct {

    private int months;
    private double rate;
    private int monthlyPayment;
    private int currentMonths;

    public SavingProduct(int months, double rate, int monthlyPayment) {
        this.months = months;
        this.rate = rate;
        this.monthlyPayment = monthlyPayment;
    }

    // amount를 기반으로 현재까지 납입한 개월 수 계산 후 이율 조정
    public void adjustRate(int amount) {
        this.currentMonths = amount / monthlyPayment;
      // 현재까지 납입한 개월 수에 따라 이율 조정
         if (currentMonths < (months * 0.2)) {
            rate = rate * 0.1;
        }
         else if ((this.months * 0.2) <= currentMonths && currentMonths < (months * 0.5)) {
         rate = rate * 0.5;
      }
        else if ((this.months * 0.5) <= currentMonths && currentMonths < (months)) {
         rate = rate * 0.8;
       }
//       else {
//         rate = this.rate;
//        }
    }

    public int calculateInterest(int amount, int currentMonths) {
        return (int) Math.round(amount * (rate / 100) / 12 * this.currentMonths);
    }

    public int calculateAmount(int amount, int currentMonths) {
        return (int) Math.round(amount + calculateInterest(amount, this.currentMonths));
    }

    public int getCurrentMonths() {
        return this.currentMonths;
    }
}