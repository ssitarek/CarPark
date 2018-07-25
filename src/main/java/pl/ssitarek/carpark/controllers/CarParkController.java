package pl.ssitarek.carpark.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.ssitarek.carpark.AcceptedCurrency;
import pl.ssitarek.carpark.ParkPlaceType;
import pl.ssitarek.carpark.ParkingImpl;
import pl.ssitarek.carpark.Ticket;
import pl.ssitarek.carpark.config.data.ErrorsAndMessages;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/carpark")
public class CarParkController {

    public static BigDecimal CONVERT_FROM_CENT = new BigDecimal(100);

    @Autowired
    ParkingImpl parkingImpl;

    /**
     * welcome information
     *
     * @return string with welcome and car park data
     * query example: http://localhost:8080/carpark
     */
    @RequestMapping("")
    public String displayInfo() {

        return "Welcome to our CarPark.<pre>Basic information: " + parkingImpl.toString() + "</pre>";
    }

    /**
     * simple health check with car park data
     *
     * @return string with car park data
     * query example: http://localhost:8080/carpark/healthcheck
     */
    @RequestMapping("/healthcheck")
    public String hello() {

        return "Health check of: " + parkingImpl.toString();
    }


    /**
     * userStory01:
     * As a driver, I want to start the parking meter,
     * so I don’t have to pay the fine for the invalid parking.
     *
     * @param carRegistrationNumber
     * @param parkPlaceTypeStr      it can be "regular" or "vip"
     * @return ticket info
     * query example: http://localhost:8080/carpark/startPark?number=AB 12345&type=regular
     * query example: http://localhost:8080/carpark/startPark?number=AB 12345&type=vip
     */
    @RequestMapping(value = "/startPark", method = GET)
    public Ticket startParkTheCar(
            @RequestParam("number") String carRegistrationNumber,
            @RequestParam(value = "type", defaultValue = "regular") String parkPlaceTypeStr
    ) {

        ParkPlaceType parkPlaceType;
        try {
            parkPlaceType = ParkPlaceType.valueOf(parkPlaceTypeStr.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return new Ticket(ErrorsAndMessages.ERROR_RESERVATION);
        }
        return parkingImpl.startParkAndGetTicket(carRegistrationNumber.toUpperCase(), parkPlaceType, LocalDateTime.now());
    }


    /**
     * userStory02:
     * As a parking operator, I want to check if the vehicle has started the parking meter.
     *
     * @param carRegistrationNumber
     * @return true in case of started and false otherwise
     * query example: http://localhost:8080/carpark/checkIfStarted?number=AB 12345
     */

    @RequestMapping(value = "/checkIfStarted", method = GET)
    public Boolean checkIfStarted(
            @RequestParam("number") String carRegistrationNumber
    ) {
        return parkingImpl.checkIfVehicleStartedParking(carRegistrationNumber.toUpperCase());
    }

    /**
     * UserStory03:
     * As a driver, I want to stop the parking meter, so that I pay only for the actual parking time
     *
     * @param ticketNumber
     * @return ticket info
     * query example: http://localhost:8080/carpark/stopPark?ticket=0&currency=PLN
     */

    @RequestMapping(value = "/stopPark", method = GET)
    public Ticket stopParkTheCar(
            @RequestParam("ticket") int ticketNumber,
            @RequestParam("currency") String acceptedCurrencyString
    ) {
        AcceptedCurrency acceptedCurrency = AcceptedCurrency.valueOf(acceptedCurrencyString);
        return parkingImpl.stopPark(ticketNumber, LocalDateTime.now(), acceptedCurrency);
    }


    /**
     * UserStory04:
     * I want to know how much I have to pay for parking.
     *
     * @param ticketNumber number of the registered ticket
     * @return ticket info (the fee is always calculated in PLN)
     * query example: http://localhost:8080/carpark/getTicketFee?number=0
     * query example: http://localhost:8080/carpark/getTicketFee?number=2
     */
    @RequestMapping(value = "/getTicketFee", method = GET)
    public BigDecimal calculateFeeForParticularTicket(
            @RequestParam("number") int ticketNumber
    ) {

        BigDecimal noFee = BigDecimal.ZERO;
        BigDecimal feeValue = Optional.ofNullable(parkingImpl.calculateFee(ticketNumber, LocalDateTime.now()))
                                      .orElse(noFee);
        return feeValue.divide(CONVERT_FROM_CENT, 2, BigDecimal.ROUND_UP);
    }


    /**
     * UserStory05:
     * I want to know how much I have to pay for parking.
     *
     * @param dayString yyyyMMdd e.g. 20180725
     * @return map of all acceptedCurrency income
     * <p>
     * query example: http://localhost:8080/carpark/getDailyIncome?day=20180720
     * !!!! IMPORTANT !!!! change the day string to current before use this query example
     */
    @RequestMapping(value = "/getDailyIncome", method = GET)
    public Map<AcceptedCurrency, BigDecimal> getDailyIncomeFromCarPark(
            @RequestParam("day") String dayString
    ) {

        Map<AcceptedCurrency, BigDecimal> result = Optional.ofNullable(parkingImpl.getDailyIncomeForSingleDate(dayString))
                                                           .orElse(parkingImpl.prepareEmptyDailyFeeMap());
        return divideAllDailyIncomesToGetCentPrecision(result);
    }

    private Map<AcceptedCurrency, BigDecimal> divideAllDailyIncomesToGetCentPrecision(Map<AcceptedCurrency, BigDecimal> bigDecimalMap) {

        Map<AcceptedCurrency, BigDecimal> result = parkingImpl.prepareEmptyDailyFeeMap();

        for (Map.Entry<AcceptedCurrency, BigDecimal> entry : bigDecimalMap.entrySet()) {
            AcceptedCurrency key = entry.getKey();
            BigDecimal value = entry.getValue();
            result.put(key, value.divide(CONVERT_FROM_CENT, 2, BigDecimal.ROUND_UP));
        }
        return result;
    }
}
