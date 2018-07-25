package pl.ssitarek.carpark;

import java.math.BigDecimal;

public class ParkPlaceFee {

    private BigDecimal firsHour;
    private BigDecimal secondHour;
    private double nextHourMultiplicationTerm;


    public ParkPlaceFee(BigDecimal firsHour, BigDecimal secondHour, double nextHourMultiplicationTerm) {
        this.firsHour = firsHour;
        this.secondHour = secondHour;
        this.nextHourMultiplicationTerm = nextHourMultiplicationTerm;
    }

    public BigDecimal getFirsHour() {
        return firsHour;
    }

    public BigDecimal getSecondHour() {
        return secondHour;
    }

    public double getNextHourMultiplicationTerm() {
        return nextHourMultiplicationTerm;
    }
}
