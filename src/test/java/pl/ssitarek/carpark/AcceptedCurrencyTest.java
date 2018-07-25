package pl.ssitarek.carpark;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class AcceptedCurrencyTest {

    @Test
    public void getChangeCurrentValueOfOneChf() {

        //check exchange
        AcceptedCurrency acceptedCurrency = AcceptedCurrency.CHF;
        assertEquals(new BigDecimal(400), acceptedCurrency.getValue());

        //set new exchange and check
        acceptedCurrency.setValue(new BigDecimal(555));
        assertEquals(new BigDecimal(555), acceptedCurrency.getValue());

        //set exchange back
        acceptedCurrency.setValue(new BigDecimal(400));
    }
}