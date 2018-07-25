package pl.ssitarek.carpark;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface Parking {

    Map<AcceptedCurrency, BigDecimal> getDailyIncomeForSingleDate(String date);

    BigDecimal calculateFee(int ticketNumber, LocalDateTime currentDateTime);

    Ticket stopPark(int ticketNumber, LocalDateTime currentDateTime, AcceptedCurrency acceptedCurrency);

    boolean checkIfVehicleStartedParking(String carRegistryNumber);

    Ticket startParkAndGetTicket(String carRegistrationNumber, ParkPlaceType parkPlaceType, LocalDateTime localDateTime);

}
