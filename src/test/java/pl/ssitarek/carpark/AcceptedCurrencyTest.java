package pl.ssitarek.carpark;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class AcceptedCurrencyTest {

    @Test
    public void getChangeCurrentValueOfOneChf() {

        AcceptedCurrency acceptedCurrency = AcceptedCurrency.CHF;
        assertEquals(new BigDecimal(400), acceptedCurrency.getValue());

        acceptedCurrency.setValue(new BigDecimal(555));
        assertEquals(new BigDecimal(555), acceptedCurrency.getValue());
    }
}