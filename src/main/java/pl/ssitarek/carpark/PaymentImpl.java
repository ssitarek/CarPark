package pl.ssitarek.carpark;

import java.math.BigDecimal;

public class PaymentImpl implements Payment {

    private final BigDecimal ONE_HUNDERED = new BigDecimal(100);
    private boolean isOk;
    private AcceptedCurrency paymentCurrency;
    private BigDecimal paymentValue;

    @Override
    public boolean checkPaymentStatus() {
        return isOk;
    }

    @Override
    public void doPayment(BigDecimal fee, AcceptedCurrency acceptedCurrency, boolean paymentOk) {

        paymentCurrency = acceptedCurrency;
        paymentValue = fee.multiply(ONE_HUNDERED).divide(acceptedCurrency.getValue(), 2, BigDecimal.ROUND_UP);
        isOk = paymentOk;
    }

    @Override
    public AcceptedCurrency getPaymentCurrency() {
        return paymentCurrency;
    }

    @Override
    public BigDecimal getPaymentValue() {
        return paymentValue;
    }
}
