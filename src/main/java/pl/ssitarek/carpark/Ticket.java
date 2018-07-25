package pl.ssitarek.carpark;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

public class Ticket {

    private int ticketNumber;
    private ParkPlace parkPlace;
    private LocalDateTime reservedTo;
    private BigDecimal ticketFee;
    private String ticketMessage;

    public Ticket(String message) {

        ticketNumber = -1;
        parkPlace = null;
        reservedTo = null;
        ticketFee = null;
        ticketMessage = message;
    }

    public Ticket(int ticketNumber, ParkPlace parkPlace, String message) {

        this.ticketNumber = ticketNumber;
        this.parkPlace = parkPlace;
        reservedTo = null;
        ticketFee = null;
        ticketMessage = message;
    }

    public void updateTicketData(LocalDateTime localDateTime, BigDecimal fee, String message) {

        reservedTo = localDateTime;
        ticketFee = fee;
        ticketMessage = message;
    }

    @Override
    public String toString() {

        //to prevent of null
        ParkPlace parkPlaceForString = Optional
                .ofNullable(parkPlace)
                .orElse(new ParkPlace(0, ParkPlaceType.REGULAR));
        BigDecimal ticketFeeForString = Optional
                .ofNullable(ticketFee)
                .orElse(BigDecimal.ZERO);

        //display
        return "Ticket{" +
                "\nticketNumber=" + ticketNumber +
                "\ncarRegistryNumber=" + parkPlaceForString.getCarRegistryNumber() +
                "\nparkPlaceType=" + parkPlaceForString.getPlaceType() +
                "\nparkPlaceNumber=" + parkPlaceForString.getPlaceNumber() +
                "\nstartDateTime=" + parkPlaceForString.getReservedFrom() +
                "\nstopDateTime=" + reservedTo +
                "\nticketFee=" + ticketFeeForString.toString() +
                "\n\nticketMessage=" + ticketMessage +
                '}';
    }

    public BigDecimal calculateTicketFee(LocalDateTime localDateTime) {

        if ((localDateTime == null) || (parkPlace == null)) {
            return null;
        }
        if (parkPlace.getReservedFrom() == null) {
            return null;
        }

        Duration duration = Duration.between(localDateTime, parkPlace.getReservedFrom());
        long occupancyTime = Math.abs(duration.toHours());
        BigDecimal[] priceTable = generatePriceListTable((int)occupancyTime);

        BigDecimal fee = BigDecimal.ZERO;
        for (BigDecimal price : priceTable) {
            fee = fee.add(price);
        }
        return fee;
    }

    private BigDecimal[] generatePriceListTable(int numberOfHours) {

        BigDecimal[] priceTable = new BigDecimal[numberOfHours + 1];
        priceTable[0] = parkPlace.getParkPlaceFeeData().getFirsHour();

        BigDecimal baseFee = parkPlace.getParkPlaceFeeData().getSecondHour();
        for (int i = 1; i < priceTable.length; i++) {
            double coefficient = Math.pow(parkPlace.getParkPlaceFeeData().getNextHourMultiplicationTerm(), i - 1);
            priceTable[i] = baseFee.multiply(new BigDecimal(coefficient));
        }
        return priceTable;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public BigDecimal getTicketFee() {
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

