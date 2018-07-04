package pl.ssitarek.carpark;

public class ParkPlaceFee {

    private double firsHour;
    private double secondHour;
    private double nextHourMultiplicationTerm;


    public ParkPlaceFee(double firsHour, double secondHour, double nextHourMultiplicationTerm) {
        this.firsHour = firsHour;
        this.secondHour = secondHour;
        this.nextHourMultiplicationTerm = nextHourMultiplicationTerm;
    }

    public double getFirsHour() {
        return firsHour;
    }

    public double getSecondHour() {
        return secondHour;
    }

    public double getNextHourMultiplicationTerm() {
        return nextHourMultiplicationTerm;
    }
}
