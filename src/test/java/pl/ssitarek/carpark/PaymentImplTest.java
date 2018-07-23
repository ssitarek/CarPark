package pl.ssitarek.carpark;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class PaymentImplTest {

    private final int DIFF_IN_CENT = 1;
    private Payment payment = new PaymentImpl();
    private BigDecimal toPay= new BigDecimal(100);

    @Test
    public void testPaymentPln() {

        AcceptedCurrency acceptedCurrency = AcceptedCurrency.PLN;
        payment.doPayment(toPay, acceptedCurrency, true);
        assertEquals(new BigDecimal(100).doubleValue(), payment.getPaymentValue().doubleValue(), DIFF_IN_CENT);
        assertEquals(AcceptedCurrency.PLN, payment.getPaymentCurrency());
        assertEquals(true, payment.checkPaymentStatus());
    }

    @Test
    public void testPaymentUsd() {

        AcceptedCurrency acceptedCurrency = AcceptedCurrency.EUR;
        payment.doPayment(toPay, acceptedCurrency, true);
        assertEquals(new BigDecimal(23).doubleValue(), payment.getPaymentValue().doubleValue(), DIFF_IN_CENT);
        assertEquals(AcceptedCurrency.EUR, payment.getPaymentCurrency());
        assertEquals(true, payment.checkPaymentStatus());
    }

    @Test
    public void testPaymentGpb() {

        AcceptedCurrency acceptedCurrency = AcceptedCurrency.GPB;
        payment.doPayment(toPay, acceptedCurrency, true);
        assertEquals(new BigDecimal(18).doubleValue(), payment.getPaymentValue().doubleValue(), DIFF_IN_CENT);
        assertEquals(AcceptedCurrency.GPB, payment.getPaymentCurrency());
        assertEquals(true, payment.checkPaymentStatus());
    }

    @Test
    public void testPaymentChf() {

        AcceptedCurrency acceptedCurrency = AcceptedCurrency.CHF;
        payment.doPayment(toPay, acceptedCurrency, true);

        BigDecimal paymentValue = payment.getPaymentValue();
        BigDecimal expectedValue = new BigDecimal(25);
        System.out.println(expectedValue.doubleValue());
        System.out.println(paymentValue.doubleValue());
        System.out.println(payment.getPaymentCurrency().getValue());
        System.out.println(payment.getPaymentCurrency());

        assertEquals(AcceptedCurrency.CHF, payment.getPaymentCurrency());
        assertEquals(true, payment.checkPaymentStatus());
        assertEquals(new BigDecimal(25).doubleValue(), payment.getPaymentValue().doubleValue(), DIFF_IN_CENT);
    }
}