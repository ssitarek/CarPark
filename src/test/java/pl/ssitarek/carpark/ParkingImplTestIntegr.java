package pl.ssitarek.carpark;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.ssitarek.carpark.config.data.CarParkParameter;
import pl.ssitarek.carpark.config.data.ErrorsAndMessages;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ParkingImpl.class)
public class ParkingImplTestIntegr {

    CarParkParameter carParkParameter = new CarParkParameter(5, 3);

    @Autowired
    ParkingImpl parkingImplForTests;

    @Before
    public void SetUp() {
        parkingImplForTests.setCarParkParameter(carParkParameter);
    }

    @Test
    public void startParkRegular() {

        Ticket ticket = parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());

        assertEquals(0, ticket.getTicketNumber());
        assertEquals("carRegular", ticket.getParkPlace().getCarRegistryNumber());
        assertEquals(ParkPlaceType.REGULAR, ticket.getParkPlace().getPlaceType());
        assertEquals(1000, ticket.getParkPlace().getPlaceNumber());

        Duration duration = Duration.between(LocalDateTime.now(), ticket.getParkPlace().getReservedFrom());
        long occupancyTime = Math.abs(duration.toMinutes());
        assertTrue(occupancyTime < 1);

        assertNull(ticket.getReservedTo());
        assertEquals("", ticket.getTicketMessage());
    }

    @Test
    public void startParkRegularTwice() {

        Ticket ticket1 = parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());
        Ticket ticket2 = parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());

        assertEquals("", ticket1.getTicketMessage());
        assertEquals(ErrorsAndMessages.ERROR_RESERVATION, ticket2.getTicketMessage());
    }

    @Test
    public void startParkVip() {

        Ticket ticket = parkingImplForTests.startParkAndGetTicket("carVip", ParkPlaceType.VIP, LocalDateTime.now());

        assertEquals(0, ticket.getTicketNumber());
        assertEquals("carVip", ticket.getParkPlace().getCarRegistryNumber());
        assertEquals(ParkPlaceType.VIP, ticket.getParkPlace().getPlaceType());
        assertEquals(2000, ticket.getParkPlace().getPlaceNumber());

        Duration duration = Duration.between(LocalDateTime.now(), ticket.getParkPlace().getReservedFrom());
        long occupancyTime = Math.abs(duration.toMinutes());
        assertTrue(occupancyTime < 1);

        assertNull(ticket.getReservedTo());
        assertEquals("", ticket.getTicketMessage());
    }

    @Test
    public void startParkMultipleCars() {

        Ticket ticket = new Ticket("");
        for (int i = 0; i < 3; i++) {
            ticket = parkingImplForTests.startParkAndGetTicket("carRegular" + i, ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(10));
        }

        assertEquals(2, ticket.getTicketNumber());
        assertEquals("carRegular2", ticket.getParkPlace().getCarRegistryNumber());
        assertEquals(ParkPlaceType.REGULAR, ticket.getParkPlace().getPlaceType());
        assertEquals(1002, ticket.getParkPlace().getPlaceNumber());

        Duration duration = Duration.between(LocalDateTime.now(), ticket.getParkPlace().getReservedFrom());
        long occupancyTime = Math.abs(duration.toMinutes());
        assertTrue(occupancyTime >= 10);

        assertNull(ticket.getReservedTo());
        assertEquals("", ticket.getTicketMessage());
    }

    @Test
    public void noEmptyParkPlaces() {

        Ticket ticket = new Ticket("");
        for (int i = 0; i < 6; i++) {
            ticket = parkingImplForTests.startParkAndGetTicket("carRegular" + i, ParkPlaceType.REGULAR, LocalDateTime.now());
        }

        assertEquals(-1, ticket.getTicketNumber());
        assertNull(ticket.getParkPlace());
        assertNull(ticket.getReservedTo());
        assertEquals(ErrorsAndMessages.ERROR_NO_EMPTY_PLACES, ticket.getTicketMessage());
    }

    @Test
    public void checkIfVehicleStartedParkingMeter() {

        for (int i = 0; i < 2; i++) {
            parkingImplForTests.startParkAndGetTicket("carVip" + i, ParkPlaceType.VIP, LocalDateTime.now());
        }
        assertTrue(parkingImplForTests.checkIfVehicleStartedParking("carVip0"));
        assertTrue(!parkingImplForTests.checkIfVehicleStartedParking("carReg0"));
    }

    @Test
    public void payAndUndoReservationTicketFound() {

        parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());
        AcceptedCurrency acceptedCurrency = AcceptedCurrency.PLN;
        Ticket ticket = parkingImplForTests.stopPark(0, LocalDateTime.now(), acceptedCurrency);
        assertTrue(ticket.getTicketMessage().equals(ErrorsAndMessages.MESSAGE_FAREWELL));
    }

    @Test
    public void payAndUndoReservationTicketNotFound() {

        parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now());
        AcceptedCurrency acceptedCurrency = AcceptedCurrency.PLN;
        Ticket ticket = parkingImplForTests.stopPark(10, LocalDateTime.now(), acceptedCurrency);
        assertTrue(ticket.getTicketMessage().equals(ErrorsAndMessages.ERROR_TICKET_NOT_FOUND));
    }

    @Test
    public void calculateFeeForExistingTicket() {

        parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(65));
        BigDecimal expectedValue = new BigDecimal(300);
        assertEquals(expectedValue, parkingImplForTests.calculateFee(0, LocalDateTime.now()));
    }

    @Test
    public void calculateFeeForNonExistingTicket() {

        parkingImplForTests.startParkAndGetTicket("carRegular", ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(65));
        assertNull(parkingImplForTests.calculateFee(100, LocalDateTime.now()));
    }

    @Test
    public void testGetDailyIncomeNoCarsOnTheCarPark() {

        AcceptedCurrency acceptedCurrency = AcceptedCurrency.PLN;
        for (int i = 0; i < 4; i++) {
            Ticket ticket = parkingImplForTests.startParkAndGetTicket("carRegular" + i, ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(125 - 30 * i));
            parkingImplForTests.stopPark(ticket.getTicketNumber(), LocalDateTime.now(), acceptedCurrency);
        }

        String date = TimeToStringConversions.doConversion(LocalDateTime.now());
        BigDecimal expectedValue = new BigDecimal(1300);
        Map<AcceptedCurrency, BigDecimal> result = parkingImplForTests.getDailyIncomeForSingleDate(date);
        assertTrue(expectedValue.doubleValue() == result.get(acceptedCurrency).doubleValue());
    }

    @Test
    public void testGetDailyIncomeSingleCarParked() {

        AcceptedCurrency acceptedCurrency = AcceptedCurrency.PLN;
        for (int i = 0; i < 4; i++) {
            Ticket ticket = parkingImplForTests.startParkAndGetTicket("carRegular" + i, ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(125 - 30 * i));
            parkingImplForTests.stopPark(ticket.getTicketNumber(), LocalDateTime.now(), acceptedCurrency);
        }
        Ticket ticket = parkingImplForTests.startParkAndGetTicket("carRegularNext", ParkPlaceType.REGULAR, LocalDateTime.now().minusMinutes(150));

        String date = TimeToStringConversions.doConversion(LocalDateTime.now());
        BigDecimal expectedValue = new BigDecimal(1300);
        Map<AcceptedCurrency, BigDecimal> result = parkingImplForTests.getDailyIncomeForSingleDate(date);
        assertTrue(expectedValue.doubleValue() == result.get(acceptedCurrency).doubleValue());
    }
}