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

@RestController
@RequestMapping("/carpark")
public class CarParkController {

    public static BigDecimal CONVERT_FROM_CENT = new BigDecimal(100);

    @Autowired
    ParkingImpl parkingImpl;

    /**
     * welcome information
     * @return string with welcome and car park data
     * query example: http://localhost:8080/carpark
     */
    @RequestMapping("")
    public String displayInfo() {

        return "Welcome to our CarPark.<pre>Basic information: " + parkingImpl.toString() + "</pre>";
    }

    /**
     * simple health check with car park data
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
    @RequestMapping("/startPark")
    public String startParkTheCar(
            @RequestParam("number") String carRegistrationNumber,
            @RequestParam(value = "type", defaultValue = "regular") String parkPlaceTypeStr
    ) {

        ParkPlaceType parkPlaceType;
        try {
            parkPlaceType = ParkPlaceType.valueOf(parkPlaceTypeStr.toUpperCase());
        } catch (IllegalArgumentException ex) {
            Ticket ticket = new Ticket();
            ticket.updateTicketData(null, null, ErrorsAndMessages.ERROR_RESERVATION);
            return ticket.toString();
        }
        Ticket ticket = parkingImpl.startParkAndGetTicket(carRegistrationNumber.toUpperCase(), parkPlaceType, LocalDateTime.now());
        return ticket.toString();
    }


    /**
     * userStory02:
     * As a parking operator, I want to check if the vehicle has started the parking meter.
     *
     * @param carRegistrationNumber
     * @return true in case of started and false otherwise
     * query example: http://localhost:8080/carpark/checkIfStarted?number=AB 12345
     */

    @RequestMapping("/checkIfStarted")
    public String checkIfStarted(
            @RequestParam("number") String carRegistrationNumber
    ) {
        Boolean result = parkingImpl.checkIfVehicleStartedParking(carRegistrationNumber.toUpperCase());
        return Boolean.toString(result);
    }

    /**
     * UserStory03:
     * As a driver, I want to stop the parking meter, so that I pay only for the actual parking time
     *
     * @param ticketNumber
     * @return ticket info
     * query example: http://localhost:8080/carpark/stopPark?ticket=0&currency=PLN
     */

    @RequestMapping("/stopPark")
    public String stopParkTheCar(
            @RequestParam("ticket") int ticketNumber,
            @RequestParam("currency") String acceptedCurrencyString
    ) {
        AcceptedCurrency acceptedCurrency = AcceptedCurrency.valueOf(acceptedCurrencyString);
        Ticket ticket = parkingImpl.stopPark(ticketNumber, LocalDateTime.now(), acceptedCurrency);
        return ticket.toString();
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
    @RequestMapping("/getTicketFee")
    public String calculateFeeForParticularTicket(
            @RequestParam("number") int ticketNumber
    ) {

        BigDecimal noFee = new BigDecimal(0.0);
        BigDecimal feeValue = Optional.ofNullable(parkingImpl.calculateFee(ticketNumber, LocalDateTime.now()))
                                      .orElse(noFee);
        //divide by 100 to obtain x.xx PLN
        return feeValue.divide(CONVERT_FROM_CENT).toString();
    }


    /**
     * UserStory05:
     * I want to know how much I have to pay for parking.
     * @param dayString yyyyMMdd e.g. 20180725
     * @return map of all acceptedCurrency income
     * <p>
     * query example: http://localhost:8080/carpark/getDailyIncome?day=20180712
     */
    @RequestMapping("/getDailyIncome")
    public String getDailyIncomeFromCarPark(
            @RequestParam("day") String dayString
    ) {

        Map<AcceptedCurrency, BigDecimal> result = Optional.ofNullable(parkingImpl.getDailyIncomeForSingleDate(dayString))
                                                           .orElse(parkingImpl.prepareEmptyDailyFeeMap());
        //divide by 100 to obtain x.xx PLN, y.yy EUR ...
        for (Map.Entry<AcceptedCurrency, BigDecimal> entry : result.entrySet()) {
            BigDecimal value = entry.getValue();
            entry.setValue(value.divide(CONVERT_FROM_CENT));
        }
        return result.toString();
    }
}
