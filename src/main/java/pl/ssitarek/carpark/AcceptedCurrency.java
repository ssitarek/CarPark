package pl.ssitarek.carpark;

import java.math.BigDecimal;

public enum AcceptedCurrency {

    /**
     * the BigDecimal value is a exchange rate and
     * can be simply modified in case of the price of e.g. USD change
     * from 3.50PLN to 3.60PLN
     */
    PLN(new BigDecimal(100)),
    EUR(new BigDecimal(450)),
    USD(new BigDecimal(350)),
    GPB(new BigDecimal(550)),
    CHF(new BigDecimal(400));

    /**
     * accepted currency with its value in centPLN, centEUR etc
     * on the website it has to be divided by 100 to obtain monetary x.xx format
     */

    private BigDecimal value;

    AcceptedCurrency(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    /**
     * method to change the exchange rate
     *
     * @param value new value of the enum
     */

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
