package pl.ssitarek.carpark;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

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

        Assert.assertTrue(parkPlaceRegular.getIsEmpty());
        Assert.assertTrue("".equals(parkPlaceRegular.getCarRegistryNumber()));
        Assert.assertNull(parkPlaceRegular.getReservedFrom());
    }

    @Test
    public void isParkPlaceVipEmpty() {

        Assert.assertTrue(parkPlaceVip.getIsEmpty());
        Assert.assertTrue("".equals(parkPlaceVip.getCarRegistryNumber()));
        Assert.assertNull(parkPlaceVip.getReservedFrom());
    }

    @Test
    public void regularReservation() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(65));
        Assert.assertTrue(!parkPlaceRegular.getIsEmpty());
        Assert.assertTrue("testCarRegular".equals(parkPlaceRegular.getCarRegistryNumber()));

        Duration duration = Duration.between(LocalDateTime.now(), parkPlaceRegular.getReservedFrom());
        long occupancyTime = Math.abs(duration.toMinutes());
        Assert.assertEquals(65, occupancyTime);
    }

    @Test
    public void vipReservation() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(65));
        Assert.assertTrue(!parkPlaceVip.getIsEmpty());
        Assert.assertTrue("testCarVip".equals(parkPlaceVip.getCarRegistryNumber()));

        Duration duration = Duration.between(LocalDateTime.now(), parkPlaceVip.getReservedFrom());
        long occupancyTime = Math.abs(duration.toMinutes());
        Assert.assertEquals(65, occupancyTime);
    }

    @Test
    public void regularUnDoReservation() {

        parkPlaceRegular.doReservation("testCarRegular", LocalDateTime.now().minusMinutes(65));
        parkPlaceRegular.unDoReservation();
        Assert.assertTrue(parkPlaceRegular.getIsEmpty());
        Assert.assertTrue("".equals(parkPlaceRegular.getCarRegistryNumber()));
        Assert.assertNull(parkPlaceRegular.getReservedFrom());
    }

    @Test
    public void vipUnDoReservation() {

        parkPlaceVip.doReservation("testCarVip", LocalDateTime.now().minusMinutes(65));
        parkPlaceVip.unDoReservation();
        Assert.assertTrue(parkPlaceVip.getIsEmpty());
        Assert.assertTrue("".equals(parkPlaceVip.getCarRegistryNumber()));
        Assert.assertNull(parkPlaceVip.getReservedFrom());
    }

}