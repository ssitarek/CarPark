package pl.ssitarek.carpark;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class PaymentImplTest {

    public final int DIFF_IN_CENT = 1;

    // names should be camelCase but PLN, USD is a name of currency
    @Test
    public void testPaymentPLN() {

        PaymentImpl payment = new PaymentImpl();
        payment.doPayment(new BigDecimal(100), AcceptedCurrency.PLN, true);
        assertEquals(new BigDecimal(100).doubleValue(), payment.getPaymentValue().doubleValue(), DIFF_IN_CENT);
        assertEquals(AcceptedCurrency.PLN, payment.getPaymentCurrency());
        assertEquals(true, payment.checkPaymentStatus());
    }

    @Test
    public void testPaymentUSD() {

        PaymentImpl payment = new PaymentImpl();
        payment.doPayment(new BigDecimal(100), AcceptedCurrency.EUR, true);
        assertEquals(new BigDecimal(23).doubleValue(), payment.getPaymentValue().doubleValue(), DIFF_IN_CENT);
        assertEquals(AcceptedCurrency.EUR, payment.getPaymentCurrency());
        assertEquals(true, payment.checkPaymentStatus());
    }

    @Test
    public void testPaymentCHF() {

        PaymentImpl payment = new PaymentImpl();
        payment.doPayment(new BigDecimal(100), AcceptedCurrency.CHF, true);
        assertEquals(new BigDecimal(25).doubleValue(), payment.getPaymentValue().doubleValue(), DIFF_IN_CENT);
        assertEquals(AcceptedCurrency.CHF, payment.getPaymentCurrency());
        assertEquals(true, payment.checkPaymentStatus());
    }

}