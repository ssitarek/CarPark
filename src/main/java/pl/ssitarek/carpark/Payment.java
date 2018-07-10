package pl.ssitarek.carpark;

import java.math.BigDecimal;

public interface Payment {

    boolean checkPaymentStatus();

    void doPayment(BigDecimal fee, boolean paymentOk);

}
