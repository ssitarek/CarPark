package pl.ssitarek.carpark;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

public class ParkingTest {

    public static final double DELTA_FEE = 0.01;

    Parking parking;

    @Before
    public void setUp() {
        parking = new Parking("SomeCarParkName", "SomeCarParkAddress");
    }


    @Test
    public void startParkRegular() {

        Ticket ticket = parking.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());

        Assert.assertEquals(0, ticket.getTicketNumber());
        Assert.assertEquals("carRegular", ticket.getParkPlace().getCarRegistryNumber());
        Assert.assertEquals(ParkPlaceType.REGULAR, ticket.getParkPlace().getPlaceType());
        Assert.assertEquals(1000, ticket.getParkPlace().getPlaceNumber());

        Duration duration = Duration.between(LocalDateTime.now(), ticket.getParkPlace().getReservedFrom());
        long occupancyTime = Math.abs(duration.toMinutes());
        Assert.assertTrue(occupancyTime < 1);

        Assert.assertNull(ticket.getReservedTo());
        Assert.assertEquals("", ticket.getTicketMessage());
    }

    @Test
    public void startParkVip() {

        Ticket ticket = parking.startParkAndGetTicket("carVip", ParkPlaceType.VIP, LocalDateTime.now());

        Assert.assertEquals(0, ticket.getTicketNumber());
        Assert.assertEquals("carVip", ticket.getParkPlace().getCarRegistryNumber());
        Assert.assertEquals(ParkPlaceType.VIP, ticket.getParkPlace().getPlaceType());
        Assert.assertEquals(2000, ticket.getParkPlace().getPlaceNumber());

        Duration duration = Duration.between(LocalDateTime.now(), ticket.getParkPlace().getReservedFrom());
        long occupancyTime = Math.abs(duration.toMinutes());
        Assert.assertTrue(occupancyTime < 1);

        Assert.assertNull(ticket.getReservedTo());
        Assert.assertEquals("", ticket.getTicketMessage());
    }

    @Test
    public void startParkMultipleCars() {

        Ticket ticket = new Ticket();
        for (int i = 0; i < 3; i++) {
            ticket = parking.startParkAndGetTicket("carRegular" + i, ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(10));
        }

        Assert.assertEquals(2, ticket.getTicketNumber());
        Assert.assertEquals("carRegular2", ticket.getParkPlace().getCarRegistryNumber());
        Assert.assertEquals(ParkPlaceType.REGULAR, ticket.getParkPlace().getPlaceType());
        Assert.assertEquals(1002, ticket.getParkPlace().getPlaceNumber());

        Duration duration = Duration.between(LocalDateTime.now(), ticket.getParkPlace().getReservedFrom());
        long occupancyTime = Math.abs(duration.toMinutes());
        Assert.assertTrue(occupancyTime >= 10);

        Assert.assertNull(ticket.getReservedTo());
        Assert.assertEquals("", ticket.getTicketMessage());
    }

    @Test
    public void noEmptyParkPlaces() {

        Ticket ticket = new Ticket();
        for (int i = 0; i < 6; i++) {
            ticket = parking.startParkAndGetTicket("carRegular" + i, ParkPlaceType.REGULAR, LocalDateTime.now());
        }

        Assert.assertEquals(0, ticket.getTicketNumber());
        Assert.assertNull(ticket.getParkPlace());
        Assert.assertNull(ticket.getReservedTo());
        Assert.assertEquals("no empty places", ticket.getTicketMessage());
    }

    @Test
    public void checkIfVehicleStartedParkingMeter() {

        for (int i = 0; i < 2; i++) {
            parking.startParkAndGetTicket("carVip" + i, ParkPlaceType.VIP, LocalDateTime.now());
        }
        Assert.assertTrue(parking.checkIfVehicleStartedParking("carVip0"));
        Assert.assertTrue(!parking.checkIfVehicleStartedParking("carReg0"));
    }

    @Test
    public void payAndUnDoReservationTicketFound() throws InterruptedException {

        parking.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());
        Ticket ticket = parking.payAndUnDoReservation(0);
        Assert.assertTrue(ticket.getTicketMessage().equals("have a nice day"));
    }

    @Test
    public void payAndUnDoReservationTicketNotFound() throws InterruptedException {

        parking.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());
        Ticket ticket = parking.payAndUnDoReservation(10);
        Assert.assertTrue(ticket.getTicketMessage().equals("ticket not found in our database"));
    }

    @Test
    public void calculateFeeForExistingTicket() {

        parking.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(65));
        double fee = parking.calculateFee(0, LocalDateTime.now());
        Assert.assertEquals(3.0, fee, DELTA_FEE);
    }

    @Test
    public void calculateFeeForNonExistingTicket() {

        parking.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(65));
        double fee = parking.calculateFee(10, LocalDateTime.now());
        Assert.assertEquals(0.0, fee, DELTA_FEE);
    }

    @Test
    public void testGetDailyFee() {

        for (int i = 0; i < 4; i++) {
            Ticket ticket = parking.startParkAndGetTicket("carRegular" + i, ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(125-30*i));
            parking.payAndUnDoReservation(ticket.getTicketNumber());
        }

        String date = convertTimeToString(LocalDateTime.now());
        double result = parking.getDailyFee(date);
        Assert.assertEquals(13.0, result, DELTA_FEE);
    }

    @Test
    public void testGetDailyFeeSingleCarParked() {

        for (int i = 0; i < 4; i++) {
            Ticket ticket = parking.startParkAndGetTicket("carRegular" + i, ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(125-30*i));
            parking.payAndUnDoReservation(ticket.getTicketNumber());
        }
        Ticket ticket = parking.startParkAndGetTicket("carRegularNext", ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(150));

        String date = convertTimeToString(LocalDateTime.now());
        double result = parking.getDailyFee(date);
        Assert.assertEquals(13.0, result, DELTA_FEE);
    }


    private String convertTimeToString(LocalDateTime localDateTime) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(localDateTime.getYear());
        stringBuilder.append(String.format("%02d", localDateTime.getMonthValue()));
        stringBuilder.append(String.format("%02d", localDateTime.getDayOfMonth()));
        return stringBuilder.toString();
    }
}