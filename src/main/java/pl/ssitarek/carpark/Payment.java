package pl.ssitarek.carpark;

import java.math.BigDecimal;

public interface Payment {

    boolean checkPaymentStatus();

    void doPayment(BigDecimal fee, AcceptedCurrency acceptedCurrency, boolean paymentOk);

    AcceptedCurrency getPaymentCurrency();

    BigDecimal getPaymentValue();
}
