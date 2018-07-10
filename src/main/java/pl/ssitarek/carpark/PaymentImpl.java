package pl.ssitarek.carpark;

import java.math.BigDecimal;

public class PaymentImpl implements Payment{

    private boolean isOk;

    @Override
    public boolean checkPaymentStatus() {
        return isOk;
    }

    @Override
    public void doPayment(BigDecimal fee, boolean paymentOk) {
        isOk = paymentOk;
    }
}
