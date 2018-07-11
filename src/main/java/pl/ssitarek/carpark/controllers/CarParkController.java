package pl.ssitarek.carpark.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.ssitarek.carpark.ParkPlaceType;
import pl.ssitarek.carpark.ParkingImpl;
import pl.ssitarek.carpark.Ticket;
import pl.ssitarek.carpark.config.data.ErrorsAndMessages;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/carpark")
public class CarParkController {

    @Autowired
    ParkingImpl parkingImpl;

    @RequestMapping("")
    public String displayInfo() {

        return "Welcome to our CarPark.<pre>Basic information: " + parkingImpl.toString() + "</pre>";
    }

    @RequestMapping("/hello")
    public String hello() {

        return "health check website of: " + parkingImpl.toString();
    }


    /**
     * userStory01:
     * As a driver, I want to start the parking meter,
     * so I don’t have to pay the fine for the invalid parking.
     *
     * @param carRegistrationNumber
     * @param parkPlaceTypeStr
     * @return ticket info
     * query example: http://localhost:8080/carpark/startPark?number=AB 12345&type=Regular
     * query example: http://localhost:8080/carpark/startPark?number=AB 12345&type=Vip
     */
    @RequestMapping("/startPark")
    public String startParkTheCar(
            @RequestParam("number") String carRegistrationNumber,
            @RequestParam(value = "type", required = false, defaultValue = "regular") String parkPlaceTypeStr
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
     * UserStory04:
     * As a driver, I want to stop the parking meter, so that I pay only for the actual parking time
     *
     * @param ticketNumber
     * @return ticket info
     * query example: http://localhost:8080/carpark/stopPark?ticket=0
     */

    @RequestMapping("/stopPark")
    public String stopParkTheCar(
            @RequestParam("ticket") int ticketNumber
    ) {
        Ticket ticket = parkingImpl.stopPark(ticketNumber, LocalDateTime.now());
        return ticket.toString();
    }


    /**
     * UserStory04:
     * I want to know how much I have to pay for parking.
     *
     * @param ticketNumber
     * @return ticket info
     * query example: http://localhost:8080/carpark/getTicketFee?number=0
     * query example: http://localhost:8080/carpark/getTicketFee?number=2
     */
    @RequestMapping("/getTicketFee")
    public String calculateFeeForParticularTicket(
            @RequestParam("number") int ticketNumber
    ) {
        BigDecimal noFee = new BigDecimal(0.0);
        BigDecimal feeValue = Optional.of(parkingImpl.calculateFee(ticketNumber, LocalDateTime.now())).orElse(noFee);
        return new BigDecimal(100).multiply(feeValue).toString();
    }

}
