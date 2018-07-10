package pl.ssitarek.carpark;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface Parking {

    BigDecimal getDailyFeeForSingleDate(String date);

    BigDecimal calculateFee(int ticketNumber, LocalDateTime currentDateTime);

    Ticket stopPark(int ticketNumber);

    boolean checkIfVehicleStartedParking(String carRegistryNumber);

    Ticket startParkAndGetTicket(String carRegistrationNumber, ParkPlaceType parkPlaceType, LocalDateTime localDateTime);

}
