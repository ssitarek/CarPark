package pl.ssitarek.carpark;

import java.time.Duration;
import java.time.LocalDateTime;

public class Ticket {

    private int ticketNumber;
    private ParkPlace parkPlace;
    private LocalDateTime reservedTo;
    private double ticketFee;
    private String ticketMessage;

    public Ticket() {
        //empty constructor
    }

    public Ticket(int ticketNumber, ParkPlace parkPlace) {

        this.ticketNumber = ticketNumber;
        this.parkPlace = parkPlace;
        reservedTo = null;
        ticketFee = 0.0;
        ticketMessage = "";
    }

    public void generateEmptyTicketWithMessage(String message) {

        ticketNumber = 0;
        parkPlace = null;
        reservedTo = null;
        ticketFee = 0.0;
        ticketMessage = message;
    }

    public void updateTicketData(LocalDateTime localDateTime, double fee, String message) {

        reservedTo = localDateTime;
        ticketFee = fee;
        ticketMessage = message;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketNumber=" + ticketNumber +
                ", carRegistryNumber=" + parkPlace.getCarRegistryNumber() +
                ", parkPlaceType=" + parkPlace.getPlaceType() +
                ", parkPlaceNumber=" + parkPlace.getPlaceNumber() +
                ", startDateTime=" + parkPlace.getReservedFrom() +
                ", stopDateTime=" + reservedTo +
                ", ticketFee=" + ticketFee +
                ", ticketMessage=" + ticketMessage +
                '}';
    }

    public double calculateTicketFee(LocalDateTime localDateTime) {

        Duration duration = Duration.between(localDateTime, parkPlace.getReservedFrom());
        long occupancyTime = Math.abs(duration.toHours());
        double[] priceTable = generatePriceListTable((int)occupancyTime);

        double fee = 0;
        for (double price : priceTable) {
            fee += price;
        }
        return Math.round(fee * 100.0) / 100.0;
    }

    private double[] generatePriceListTable(int numberOfHours) {

        double[] priceTable = new double[numberOfHours + 1];
        priceTable[0] = parkPlace.getParkPlaceFeeData().getFirsHour();

        double baseFee = parkPlace.getParkPlaceFeeData().getSecondHour();
        for (int i = 1; i < priceTable.length; i++) {
            priceTable[i] = baseFee * Math.pow(parkPlace.getParkPlaceFeeData().getNextHourMultiplicationTerm(), i - 1);
        }
        return priceTable;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public double getTicketFee() {
        return ticketFee;
    }

    public ParkPlace getParkPlace() {
        return parkPlace;
    }

    public LocalDateTime getReservedTo() {
        return reservedTo;
    }

    public String getTicketMessage() {
        return ticketMessage;
    }
}

