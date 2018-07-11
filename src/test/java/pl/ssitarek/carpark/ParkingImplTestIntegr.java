package pl.ssitarek.carpark;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import pl.ssitarek.carpark.config.CarParkParameter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ParkingImpl.class)
@TestPropertySource(locations = "classpath:test.properties")
public class ParkingImplTestIntegr {

    @Autowired
    CarParkParameter carParkParameter;

    @Autowired
    ParkingImpl parkingImplForTests;

    @Before
    public void SetUp() {
        parkingImplForTests.setCarParkParameter(carParkParameter);
    }

    @Test
    public void startParkRegular() {

        Ticket ticket = parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());

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
    public void startParkRegularTwice() {

        Ticket ticket1 = parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());
        Ticket ticket2 = parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());

        Assert.assertEquals("", ticket1.getTicketMessage());
        Assert.assertEquals(ParkingImpl.ErrorsAndMessages.ERROR_RESERVATION, ticket2.getTicketMessage());
    }

    @Test
    public void startParkVip() {

        Ticket ticket = parkingImplForTests.startParkAndGetTicket("carVip", ParkPlaceType.VIP, LocalDateTime.now());

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
            ticket = parkingImplForTests.startParkAndGetTicket("carRegular" + i, ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(10));
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
            ticket = parkingImplForTests.startParkAndGetTicket("carRegular" + i, ParkPlaceType.REGULAR, LocalDateTime.now());
        }

        Assert.assertEquals(0, ticket.getTicketNumber());
        Assert.assertNull(ticket.getParkPlace());
        Assert.assertNull(ticket.getReservedTo());
        Assert.assertEquals(ParkingImpl.ErrorsAndMessages.ERROR_NO_EMPTY_PLACES, ticket.getTicketMessage());
    }

    @Test
    public void checkIfVehicleStartedParkingMeter() {

        for (int i = 0; i < 2; i++) {
            parkingImplForTests.startParkAndGetTicket("carVip" + i, ParkPlaceType.VIP, LocalDateTime.now());
        }
        Assert.assertTrue(parkingImplForTests.checkIfVehicleStartedParking("carVip0"));
        Assert.assertTrue(!parkingImplForTests.checkIfVehicleStartedParking("carReg0"));
    }

    @Test
    public void payAndUndoReservationTicketFound() {

        parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());
        Ticket ticket = parkingImplForTests.stopPark(0);
        Assert.assertTrue(ticket.getTicketMessage().equals(ParkingImpl.ErrorsAndMessages.MESSAGE_FAREWELL));
    }

    @Test
    public void payAndUndoReservationTicketNotFound() {

        parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());
        Ticket ticket = parkingImplForTests.stopPark(10);
        Assert.assertTrue(ticket.getTicketMessage().equals(ParkingImpl.ErrorsAndMessages.ERROR_TICKET_NOT_FOUND));
    }

    @Test
    public void calculateFeeForExistingTicket() {

        parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(65));
        BigDecimal expectedValue = new BigDecimal(300);
        Assert.assertEquals(expectedValue, parkingImplForTests.calculateFee(0, LocalDateTime.now()));
    }

    @Test
    public void calculateFeeForNonExistingTicket() {

        parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(65));
        Assert.assertNull(parkingImplForTests.calculateFee(100, LocalDateTime.now()));
    }

    @Test
    public void testGetDailyFee() {

        for (int i = 0; i < 4; i++) {
            Ticket ticket = parkingImplForTests.startParkAndGetTicket("carRegular" + i, ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(125 - 30 * i));
            parkingImplForTests.stopPark(ticket.getTicketNumber());
        }

        String date = convertTimeToString(LocalDateTime.now());
        BigDecimal expectedValue = new BigDecimal(1300);
        BigDecimal result = parkingImplForTests.getDailyFeeForSingleDate(date);
        Assert.assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    @Test
    public void testGetDailyFeeSingleCarParked() {

        for (int i = 0; i < 4; i++) {
            Ticket ticket = parkingImplForTests.startParkAndGetTicket("carRegular" + i, ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(125 - 30 * i));
            parkingImplForTests.stopPark(ticket.getTicketNumber());
        }
        Ticket ticket = parkingImplForTests.startParkAndGetTicket("carRegularNext", ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(150));

        String date = convertTimeToString(LocalDateTime.now());
        BigDecimal expectedValue = new BigDecimal(1300);
        BigDecimal result = parkingImplForTests.getDailyFeeForSingleDate(date);
        Assert.assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    private static String convertTimeToString(LocalDateTime localDateTime) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(localDateTime.getYear());
        stringBuilder.append(String.format("%02d", localDateTime.getMonthValue()));
        stringBuilder.append(String.format("%02d", localDateTime.getDayOfMonth()));
        return stringBuilder.toString();
    }
}