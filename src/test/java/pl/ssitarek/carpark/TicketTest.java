package pl.ssitarek.carpark;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class TicketTest {

    public static final double DELTA_FEE = 0.01;

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
        Assert.assertEquals(1.0, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    @Test
    public void regular55Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(55));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        Assert.assertEquals(1.0, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

     //60 minutes means that the client has just started second hour due to the invoke time
    @Test
    public void regular59Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(59).minusSeconds(50));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        Assert.assertEquals(1.0, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    //60 minutes means that the client has just started second hour due to the invoke time
    @Test
    public void regular60Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(60));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        Assert.assertEquals(3.0, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    @Test
    public void regular65Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(65));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        Assert.assertEquals(3.0, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    @Test
    public void regular125Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(125));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        Assert.assertEquals(6.0, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    @Test
    public void regular185Minutes() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(185));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        Assert.assertEquals(10.5, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    @Test
    public void vip5Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(5));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        Assert.assertEquals(0.0, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    @Test
    public void vip55Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(55));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        Assert.assertEquals(0.0, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    //60 minutes means that the client has just started second hour due to the invoke time
    @Test
    public void vip59Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(59).minusSeconds(50));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        Assert.assertEquals(0.0, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    //60 minutes means that the client has just started second hour due to the invoke time
    @Test
    public void vip60Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(60));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        Assert.assertEquals(2.0, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    @Test
    public void vip65Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(65));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        Assert.assertEquals(2.0, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    @Test
    public void vip125Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(125));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        Assert.assertEquals(4.4, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    @Test
    public void vip185Minutes() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(185));
        Ticket ticket = new Ticket(0, parkPlaceVip);
        Assert.assertEquals(7.28, ticket.calculateTicketFee(LocalDateTime.now()), DELTA_FEE);
    }

    @Test
    public void updateAfterPayment(){
        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(90));
        Ticket ticket = new Ticket(0, parkPlaceRegular);
        Assert.assertNull(ticket.getReservedTo());
        Assert.assertEquals(0.0, ticket.getTicketFee(), DELTA_FEE);

        LocalDateTime currentDateTime = LocalDateTime.now();
        ticket.updateTicketData(currentDateTime, 32.23, "payment OK");
        Assert.assertEquals(currentDateTime, ticket.getReservedTo());
        Assert.assertEquals(32.23, ticket.getTicketFee(), DELTA_FEE);
    }

    @Test
    public void emptyTicket(){

        Ticket ticket = new Ticket(10,parkPlaceRegular);
        String message = "test message";
        ticket.generateEmptyTicketWithMessage(message);
        Assert.assertEquals(0, ticket.getTicketNumber());
        Assert.assertNull(ticket.getParkPlace());
        Assert.assertNull(ticket.getReservedTo());
        Assert.assertEquals(0.0, ticket.getTicketFee(), DELTA_FEE);
        Assert.assertEquals(message, ticket.getTicketMessage());
    }

}