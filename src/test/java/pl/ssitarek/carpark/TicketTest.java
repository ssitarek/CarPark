package pl.ssitarek.carpark;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TicketTest {

    private ParkPlace parkPlaceRegular;
    private ParkPlace parkPlaceVip;

    @Before
    public void setUp() {

        parkPlaceRegular = new ParkPlace(0, ParkPlaceType.REGULAR);
        parkPlaceVip = new ParkPlace(0, ParkPlaceType.VIP);
    }

    @Test
    public void regular5Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(5));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        BigDecimal expectedValue = new BigDecimal(100.0);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    @Test
    public void regular55Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(55));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        BigDecimal expectedValue = new BigDecimal(100.0);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    //60 minutes means that the client has just started second hour due to the invoke time
    @Test
    public void regular59Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(59).minusSeconds(50));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        BigDecimal expectedValue = new BigDecimal(100);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    //60 minutes means that the client has just started second hour due to the invoke time
    @Test
    public void regular60Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(60));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        BigDecimal expectedValue = new BigDecimal(300);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    @Test
    public void regular65Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(65));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        BigDecimal expectedValue = new BigDecimal(300);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    @Test
    public void regular125Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(125));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        BigDecimal expectedValue = new BigDecimal(600);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    @Test
    public void regular185Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(185));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        BigDecimal expectedValue = new BigDecimal(1050);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    @Test
    public void vip5Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(5));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        BigDecimal expectedValue = new BigDecimal(0);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    @Test
    public void vip55Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(55));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        BigDecimal expectedValue = new BigDecimal(0);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    //60 minutes means that the client has just started second hour due to the invoke time
    @Test
    public void vip59Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(59).minusSeconds(50));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        BigDecimal expectedValue = new BigDecimal(0);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    //60 minutes means that the client has just started second hour due to the invoke time
    @Test
    public void vip60Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(60));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        BigDecimal expectedValue = new BigDecimal(200);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    @Test
    public void vip65Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(65));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        BigDecimal expectedValue = new BigDecimal(200);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    @Test
    public void vip125Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(125));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        BigDecimal expectedValue = new BigDecimal(440);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    @Test
    public void vip185Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(185));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        BigDecimal expectedValue = new BigDecimal(728);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    @Test
    public void updateAfterPayment() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(90));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        assertNull(ticket.getReservedTo());
        BigDecimal expectedValue = new BigDecimal(300);
        BigDecimal result = ticket.calculateTicketFee(LocalDateTime.now());
        assertTrue(expectedValue.doubleValue() == result.doubleValue());

        LocalDateTime currentDateTime = LocalDateTime.now();
        ticket.updateTicketData(currentDateTime, new BigDecimal(3223), "test payment");
        assertEquals(currentDateTime, ticket.getReservedTo());
        expectedValue = new BigDecimal(3223);
        result = ticket.getTicketFee();
        assertTrue(expectedValue.doubleValue() == result.doubleValue());
    }

    @Test
    public void emptyTicket() {

        Ticket ticket = new Ticket(10, parkPlaceRegular);
        String message = "test message";
        ticket.generateEmptyTicketWithMessage(message);

        assertEquals(-1, ticket.getTicketNumber());
        assertNull(ticket.getParkPlace());
        assertNull(ticket.getReservedTo());
        assertNull(ticket.getTicketFee());

        assertNull(ticket.calculateTicketFee(LocalDateTime.now()));
        assertEquals(message, ticket.getTicketMessage());
    }

}