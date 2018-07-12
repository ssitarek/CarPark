package pl.ssitarek.carpark;

import java.math.BigDecimal;

public class PaymentImpl implements Payment {

    private boolean isOk;

    @Override
    public boolean checkPaymentStatus() {
        return isOk;
    }

    @Override
    public void doPayment(BigDecimal fee, AcceptedCurrency acceptedCurrency, boolean paymentOk) {

        //not necessary line to show how do I imagine payment in different currency
        BigDecimal payInCurrency = fee.divide(acceptedCurrency.getValue());

        isOk = paymentOk;
    }
}
