package pl.ssitarek.carpark;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ParkPlaceTest {

    private ParkPlace parkPlaceRegular;
    private ParkPlace parkPlaceVip;

    @Before
    public void setUp() {

        parkPlaceRegular = new ParkPlace(0, ParkPlaceType.REGULAR);
        parkPlaceVip = new ParkPlace(0, ParkPlaceType.VIP);
    }

    @Test
    public void isParkPlaceRegularEmpty() {

        assertTrue(parkPlaceRegular.getIsEmpty());
        assertTrue("".equals(parkPlaceRegular.getCarRegistryNumber()));
        assertNull(parkPlaceRegular.getReservedFrom());
    }

    @Test
    public void isParkPlaceVipEmpty() {

        assertTrue(parkPlaceVip.getIsEmpty());
        assertTrue("".equals(parkPlaceVip.getCarRegistryNumber()));
        assertNull(parkPlaceVip.getReservedFrom());
    }

    @Test
    public void regularReservation() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(65));
        assertTrue(!parkPlaceRegular.getIsEmpty());
        assertTrue("testCarRegular".equals(parkPlaceRegular.getCarRegistryNumber()));

        Duration duration = Duration.between(LocalDateTime.now(), parkPlaceRegular.getReservedFrom());
        long occupancyTime = Math.abs(duration.toMinutes());
        assertEquals(65, occupancyTime);
    }

    @Test
    public void vipReservation() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(65));
        assertTrue(!parkPlaceVip.getIsEmpty());
        assertTrue("testCarVip".equals(parkPlaceVip.getCarRegistryNumber()));

        Duration duration = Duration.between(LocalDateTime.now(), parkPlaceVip.getReservedFrom());
        long occupancyTime = Math.abs(duration.toMinutes());
        assertEquals(65, occupancyTime);
    }

    @Test
    public void regularUnDoReservation() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(65));
        parkPlaceRegular.unDoReservation();
        assertTrue(parkPlaceRegular.getIsEmpty());
        assertTrue("".equals(parkPlaceRegular.getCarRegistryNumber()));
        assertNull(parkPlaceRegular.getReservedFrom());
    }

    @Test
    public void vipUnDoReservation() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(65));
        parkPlaceVip.unDoReservation();
        assertTrue(parkPlaceVip.getIsEmpty());
        assertTrue("".equals(parkPlaceVip.getCarRegistryNumber()));
        assertNull(parkPlaceVip.getReservedFrom());
    }

}