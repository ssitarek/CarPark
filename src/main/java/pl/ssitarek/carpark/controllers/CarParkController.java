package pl.ssitarek.carpark.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.ssitarek.carpark.ParkPlace;
import pl.ssitarek.carpark.ParkPlaceType;
import pl.ssitarek.carpark.Parking;
import pl.ssitarek.carpark.Ticket;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/carpark")
public class CarParkController {

    @Autowired
    Parking parking;

    @RequestMapping("")
    public String displayInfo() {

        return "Welcome to our CarPark.<pre>Basic information: " + parking.toString() + "</pre>";
    }

    @RequestMapping("/hello")
    public String hello() {

        return "health check website of: " + parking.toString();
    }


    /**
     * userStory01:
     * As a driver, I want to start the parking meter,
     * so I don’t have to pay the fine for the invalid parking.
     *
     * @param carRegistrationNumber
     * @param parkPlaceTypeStr
     * @return
     *
     * query example: http://localhost:8080/carpark/startPark?number=AB 12345
     * query example: http://localhost:8080/carpark/startPark?number=AB 12345&type=Vip
     */
    @RequestMapping("/startPark")
    public String startParkTheCar(
            @RequestParam("number") String carRegistrationNumber,
            @RequestParam(value = "type", required = false, defaultValue = "reg") String parkPlaceTypeStr
    ) {

        //it is necessary to change this part of code in cas of new parkPlace type
        ParkPlaceType parkPlaceType = ParkPlaceType.REGULAR;
        if ("vip".equals(parkPlaceTypeStr.toLowerCase())) {
            parkPlaceType = ParkPlaceType.VIP;
        }
        Ticket ticket = parking.startParkAndGetTicket(carRegistrationNumber.toUpperCase(), parkPlaceType, LocalDateTime.now());
        return ticket.toString();
    }


    /**
     * userStory02:
     * As a parking operator, I want to check if the vehicle has started the parking meter.
     *
     * @param carRegistrationNumber
     * @return
     *
     * query example: http://localhost:8080/carpark/checkIfStarted?number=AB 12345
     */

    @RequestMapping("/checkIfStarted")
    public String checkIfStarted(
            @RequestParam("number") String carRegistrationNumber
    ) {
        Boolean result = parking.checkIfVehicleStartedParking(carRegistrationNumber.toUpperCase());
        return Boolean.toString(result);
    }

    /**
     * UserStory04:
     * As a driver, I want to stop the parking meter, so that I pay only for the actual parking time
     *
     * @param ticketNumber
     * @return
     *
     * query example: http://localhost:8080/carpark/stopPark?ticket=0
     */

    @RequestMapping("/stopPark")
    public String stopParkTheCar(
            @RequestParam("ticket") int ticketNumber
    ) {
        Ticket ticket = parking.stopPark(ticketNumber);
        return ticket.toString();
    }


    /**
     * UserStory04:
     * I want to know how much I have to pay for parking.
     *
     * @param ticketNumber
     * @return
     *
     * query example: http://localhost:8080/carpark/getTicketFee?number=0
     * query example: http://localhost:8080/carpark/getTicketFee?number=2
     */
    @RequestMapping("/getTicketFee")
    public String calculateFeeForParticularTicket(
            @RequestParam("number") int ticketNumber
    ) {
        double feeValue = parking.calculateFee(ticketNumber, LocalDateTime.now());
        return Double.toString(feeValue);
    }

}
