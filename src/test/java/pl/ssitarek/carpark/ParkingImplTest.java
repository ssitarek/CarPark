package pl.ssitarek.carpark;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import pl.ssitarek.carpark.config.data.CarParkParameter;
import pl.ssitarek.carpark.config.data.ErrorsAndMessages;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.ssitarek.carpark.ParkingImpl.prepareEmptyDailyFeeMap;

@RunWith(MockitoJUnitRunner.class)
public class ParkingImplTest {

    private static final LocalDateTime CURRENT_DATE_TIME = LocalDateTime.of(2018, 7, 7, 12, 0, 0);

    private FakeCarParkForTest fakeCarParkForTest = new FakeCarParkForTest();

    private CarParkParameter carParkParameter = mock(CarParkParameter.class);

    private ParkingImpl parkingImpl;

    @Before
    public void setUp() {
        parkingImpl = new ParkingImpl();
        parkingImpl.setCarParkParameter(carParkParameter);
    }

    @Test
    public void testGetDailyFeeForParticularDate() {

        when(parkingImpl.getCarParkParameter().getDailyIncomeMap())
                .thenReturn(fakeCarParkForTest.fakeCarParkParameter.getDailyIncomeMap());

        String dateString = TimeToStringConversions.doConversion(CURRENT_DATE_TIME);
        Map<AcceptedCurrency, BigDecimal> singleDayFee = parkingImpl.getDailyIncomeForSingleDate(dateString);
        assertEquals(new BigDecimal(400), singleDayFee.get(AcceptedCurrency.PLN));
        assertEquals(new BigDecimal(0), singleDayFee.get(AcceptedCurrency.EUR));
        assertEquals(new BigDecimal(0), singleDayFee.get(AcceptedCurrency.USD));
        assertEquals(new BigDecimal(0), singleDayFee.get(AcceptedCurrency.GPB));
        assertEquals(new BigDecimal(0), singleDayFee.get(AcceptedCurrency.CHF));
    }

    @Test
    public void testGetDailyFeeForParticularDateMinusOneDay() {

        when(parkingImpl.getCarParkParameter().getDailyIncomeMap())
                .thenReturn(fakeCarParkForTest.fakeCarParkParameter.getDailyIncomeMap());

        String dateString = TimeToStringConversions.doConversion(CURRENT_DATE_TIME.minusDays(1));
        Map<AcceptedCurrency, BigDecimal> singleDayFee = parkingImpl.getDailyIncomeForSingleDate(dateString);
        assertEquals(new BigDecimal(0), singleDayFee.get(AcceptedCurrency.PLN));
        assertEquals(new BigDecimal(0), singleDayFee.get(AcceptedCurrency.EUR));
        assertEquals(new BigDecimal(0), singleDayFee.get(AcceptedCurrency.USD));
        assertEquals(new BigDecimal(0), singleDayFee.get(AcceptedCurrency.GPB));
        assertEquals(new BigDecimal(0), singleDayFee.get(AcceptedCurrency.CHF));
    }

    @Test
    public void testGetDailyFeeForParticularDateNotExists() {

        when(parkingImpl.getCarParkParameter().getDailyIncomeMap())
                .thenReturn(fakeCarParkForTest.fakeCarParkParameter.getDailyIncomeMap());

        String dateString = TimeToStringConversions.doConversion(CURRENT_DATE_TIME.minusDays(2));
        assertNull(parkingImpl.getDailyIncomeForSingleDate(dateString));
    }

    @Test
    public void testCalculateFee() {

        when(carParkParameter.getCurrentTicketsMap())
                .thenReturn(fakeCarParkForTest.fakeCarParkParameter.getCurrentTicketsMap());

        assertEquals(new BigDecimal(300), parkingImpl.calculateFee(0, CURRENT_DATE_TIME));
        assertNull(parkingImpl.calculateFee(1, CURRENT_DATE_TIME));
        assertEquals(new BigDecimal(100), parkingImpl.calculateFee(2, CURRENT_DATE_TIME));
        assertNull(parkingImpl.calculateFee(3, CURRENT_DATE_TIME));
        assertEquals(new BigDecimal(100), parkingImpl.calculateFee(4, CURRENT_DATE_TIME));
        assertNull(parkingImpl.calculateFee(5, CURRENT_DATE_TIME));
        assertNull(parkingImpl.calculateFee(6, CURRENT_DATE_TIME));
        assertNull(parkingImpl.calculateFee(-1, CURRENT_DATE_TIME));
        assertNull(parkingImpl.calculateFee(-1, null));
    }

    @Test
    public void testStopPark() {

        when(carParkParameter.getCurrentTicketsMap())
                .thenReturn(fakeCarParkForTest.fakeCarParkParameter.getCurrentTicketsMap());
        when(carParkParameter.getPayment())
                .thenReturn(fakeCarParkForTest.fakeCarParkParameter.getPayment());
        when(carParkParameter.getDailyIncomeMap())
                .thenReturn(fakeCarParkForTest.fakeCarParkParameter.getDailyIncomeMap());

        AcceptedCurrency acceptedCurrency = AcceptedCurrency.PLN;
        Ticket ticket = parkingImpl.stopPark(0, CURRENT_DATE_TIME, acceptedCurrency);
        assertEquals(ErrorsAndMessages.MESSAGE_FAREWELL, ticket.getTicketMessage());
    }

    @Test
    public void testCheckIfVehicleStartedParking() {
        when(carParkParameter.getParkPlaceTypeListMap())
                .thenReturn(fakeCarParkForTest.fakeCarParkParameter.getParkPlaceTypeListMap());

        assertEquals(false, parkingImpl.checkIfVehicleStartedParking(""));
        assertEquals(true, parkingImpl.checkIfVehicleStartedParking("FakeCar000"));
        assertEquals(false, parkingImpl.checkIfVehicleStartedParking("FakeCar001"));
        assertEquals(false, parkingImpl.checkIfVehicleStartedParking("carWithTheRegistryNumberTooLong"));
    }

    @Test
    public void testStartParkAndGetTicket() {

        when(carParkParameter.getParkPlaceTypeListMap())
                .thenReturn(fakeCarParkForTest.fakeCarParkParameter.getParkPlaceTypeListMap());

        Ticket ticket = parkingImpl.startParkAndGetTicket("TestCar", ParkPlaceType.REGULAR, CURRENT_DATE_TIME);
        assertEquals(1, ticket.getTicketNumber());
        assertEquals(1001, ticket.getParkPlace().getPlaceNumber());
        assertEquals("TestCar", ticket.getParkPlace().getCarRegistryNumber());
    }

    @Test
    public void testStartParkAndGetTicketTwiceTheSame() {

        when(carParkParameter.getParkPlaceTypeListMap())
                .thenReturn(fakeCarParkForTest.fakeCarParkParameter.getParkPlaceTypeListMap());

        Ticket ticket1 = parkingImpl.startParkAndGetTicket("TestCar", ParkPlaceType.REGULAR, CURRENT_DATE_TIME);
        assertEquals(1, ticket1.getTicketNumber());
        assertEquals(1001, ticket1.getParkPlace().getPlaceNumber());
        assertEquals("TestCar", ticket1.getParkPlace().getCarRegistryNumber());
        assertEquals(CURRENT_DATE_TIME, ticket1.getParkPlace().getReservedFrom());

        Ticket ticket2 = parkingImpl.startParkAndGetTicket("TestCar", ParkPlaceType.REGULAR, CURRENT_DATE_TIME);
        assertEquals(-1, ticket2.getTicketNumber());
        assertNull(ticket2.getParkPlace());
        assertEquals(ErrorsAndMessages.ERROR_RESERVATION, ticket2.getTicketMessage());
    }


    /**
     * fake class including data that are necessary for test only
     */
    private static final class FakeCarParkForTest {

        CarParkParameter fakeCarParkParameter;

        public FakeCarParkForTest() {

            fakeCarParkParameter = new CarParkParameter(5, 3);
            fillWithFakeData(fakeCarParkParameter);
        }

        private void fillWithFakeData(CarParkParameter params) {

            List<ParkPlace> parkPlaceList = params.getParkPlaceTypeListMap().get(ParkPlaceType.REGULAR);

            //prepare to dailyFee map
            String workingDay = TimeToStringConversions.doConversion(CURRENT_DATE_TIME.minusDays(1));
            params.getDailyIncomeMap().put(workingDay, prepareEmptyDailyFeeMap());
            workingDay = TimeToStringConversions.doConversion(CURRENT_DATE_TIME);
            params.getDailyIncomeMap().put(workingDay, prepareEmptyDailyFeeMap());

            for (int i = 0; i < parkPlaceList.size(); i++) {

                //park the car
                ParkPlace parkPlace = parkPlaceList.get(i);
                parkPlace.doReservation("FakeCar00" + i, CURRENT_DATE_TIME.minusMinutes(75 - 10 * i));

                //generate ticket
                Ticket ticket = new Ticket(i, parkPlace);
                params.getCurrentTicketsMap().put(i, ticket);
                params.setLastTicketNumber(ticket.getTicketNumber());

                if (i % 2 == 1) {

                    //calculate fee and update ticket
                    BigDecimal fee = ticket.calculateTicketFee(CURRENT_DATE_TIME);
                    ticket.updateTicketData(CURRENT_DATE_TIME, fee, ErrorsAndMessages.MESSAGE_PAYMENT_OK);

                    //update dailyFee map
                    AcceptedCurrency acceptedCurrency = AcceptedCurrency.PLN;
                    //get single day map -> get value
                    Map<AcceptedCurrency, BigDecimal> singleDayMap = params.getDailyIncomeMap().get(workingDay);
                    BigDecimal val = singleDayMap.get(acceptedCurrency);
                    //calculate fee and add it to daily fee
                    val = val.add(ticket.getTicketFee());
                    //put fee to singleDayMap -> put singleDayMap to dailyFeeMap
                    singleDayMap.put(acceptedCurrency, val);
                    params.getDailyIncomeMap().put(workingDay, singleDayMap);

                    //update ticket data of dateTime, fee and message
                    ticket.updateTicketData(CURRENT_DATE_TIME, fee, ErrorsAndMessages.MESSAGE_FAREWELL);
                    //remove ticket from current map
                    params.getCurrentTicketsMap().remove(ticket.getTicketNumber());

                    parkPlace.unDoReservation();
                }
            }
        }
    }
}