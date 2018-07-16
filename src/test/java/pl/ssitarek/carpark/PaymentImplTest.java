package pl.ssitarek.carpark;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class PaymentImplTest {

    private final int DIFF_IN_CENT = 1;
    private PaymentImpl payment;
    private BigDecimal toPay;

    @Before
    public void setUp() {
        payment = new PaymentImpl();
        toPay = new BigDecimal(100);
    }

    // names should be camelCase but PLN, USD is a name of currency
    @Test
    public void testPaymentPLN() {

        AcceptedCurrency acceptedCurrency = AcceptedCurrency.PLN;
        payment.doPayment(toPay, acceptedCurrency, true);
        assertEquals(new BigDecimal(100).doubleValue(), payment.getPaymentValue().doubleValue(), DIFF_IN_CENT);
        assertEquals(AcceptedCurrency.PLN, payment.getPaymentCurrency());
        assertEquals(true, payment.checkPaymentStatus());
    }

    @Test
    public void testPaymentUSD() {

        AcceptedCurrency acceptedCurrency = AcceptedCurrency.EUR;
        payment.doPayment(toPay, acceptedCurrency, true);
        assertEquals(new BigDecimal(23).doubleValue(), payment.getPaymentValue().doubleValue(), DIFF_IN_CENT);
        assertEquals(AcceptedCurrency.EUR, payment.getPaymentCurrency());
        assertEquals(true, payment.checkPaymentStatus());
    }

    @Test
    public void testPaymentGPB() {

        AcceptedCurrency acceptedCurrency = AcceptedCurrency.GPB;
        payment.doPayment(toPay, acceptedCurrency, true);
        assertEquals(new BigDecimal(18).doubleValue(), payment.getPaymentValue().doubleValue(), DIFF_IN_CENT);
        assertEquals(AcceptedCurrency.GPB, payment.getPaymentCurrency());
        assertEquals(true, payment.checkPaymentStatus());
    }

    @Ignore("some strange behaviour, OK if you run this test class only, errors in case of maven build")
    @Test
    public void testPaymentCHF() {

        AcceptedCurrency acceptedCurrency = AcceptedCurrency.CHF;
        payment.doPayment(toPay, acceptedCurrency, true);
        assertEquals(new BigDecimal(25).doubleValue(), payment.getPaymentValue().doubleValue(), DIFF_IN_CENT);
        assertEquals(AcceptedCurrency.CHF, payment.getPaymentCurrency());
        assertEquals(true, payment.checkPaymentStatus());
    }
}