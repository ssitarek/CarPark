package pl.ssitarek.carpark;

import org.springframework.stereotype.Service;
import pl.ssitarek.carpark.config.CarParkParameter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParkingImpl implements Parking {

    private CarParkParameter carParkParameter;

    public ParkingImpl() {

    }

    @Override
    public String toString() {
        return "Parking{" +
                "name='" + carParkParameter.getCarParkName() + '\'' +
                ", address='" + carParkParameter.getCarParkAddress() + '\'' +
                ", numberOfPlaces=" + carParkParameter.getNumberOfPlaces() +
                ", numberOfRegular=" + carParkParameter.getParkPlaceTypeListMap().get(ParkPlaceType.REGULAR).size() +
                ", numberOfVip=" + carParkParameter.getParkPlaceTypeListMap().get(ParkPlaceType.VIP).size() +
                '}';
    }


    /**
     * 5. As a parking owner, I want to know how much money was earned during a given day
     *
     * @param dateString
     * @return
     */
    @Override
    public BigDecimal getDailyFeeForSingleDate(String dateString) {

        return carParkParameter.getDailyFeeMap().get(dateString);//Optional.ofNullable(dailyFeeMap.get(date)).orElse(new BigDecimal(0.0));

    }

    /**
     * 4. As a driver, I want to know how much I have to pay for parking.
     *
     * @param ticketNumber
     * @return
     */
    @Override
    public BigDecimal calculateFee(int ticketNumber, LocalDateTime currentDateTime) {

        Ticket ticket = carParkParameter.getCurrentTicketsMap().get(ticketNumber);
        if (ticket == null) {
            return null;
        }
        return ticket.calculateTicketFee(currentDateTime);
    }


    /**
     * 3. As a driver, I want to stop the parking meter, so that I pay only for the actual parking time
     *
     * @param ticketNumber
     * @return
     */
    @Override
    public Ticket stopPark(int ticketNumber) {

        Ticket ticket = carParkParameter.getCurrentTicketsMap().get(ticketNumber);
        if (ticket == null) {
            ticket = new Ticket();
            ticket.updateTicketData(null, null, ErrorsAndMessages.ERROR_TICKET_NOT_FOUND);
            return ticket;
        }

        LocalDateTime currentDateTime = getCurrentDateTime();
        BigDecimal fee = calculateFee(ticketNumber, currentDateTime);
        if (fee == null) {
            ticket = new Ticket();
            ticket.updateTicketData(null, null, ErrorsAndMessages.ERROR_FEE);
            return ticket;
        }

        carParkParameter.getPayment().doPayment(fee, true);
        boolean isPaymentOk = carParkParameter.getPayment().checkPaymentStatus();
        if (isPaymentOk == false) {
            ticket.updateTicketData(currentDateTime, fee, ErrorsAndMessages.ERROR_PAYMENT);
        }
        ticket.updateTicketData(currentDateTime, fee, ErrorsAndMessages.MESSAGE_PAYMENT_OK);

        //prepare to calculate dailyFee
        String workingDay = TimeToStringConversions.doConversion(ticket.getReservedTo());
        if (carParkParameter.getDailyFeeMap().get(workingDay) == null) {
            carParkParameter.getDailyFeeMap().put(workingDay, new BigDecimal(0.0));
        }
        BigDecimal val = carParkParameter.getDailyFeeMap().get(workingDay);
        val = val.add(ticket.getTicketFee());
        carParkParameter.getDailyFeeMap().put(workingDay, val);

        if (!unDoReserveParkPlace(ticket.getParkPlace())) {
            ticket.updateTicketData(currentDateTime, fee, ErrorsAndMessages.ERROR_UNDO_RESERVATION);
        }

        ticket.updateTicketData(currentDateTime, fee, ErrorsAndMessages.MESSAGE_FAREWELL);
        carParkParameter.getCurrentTicketsMap().remove(ticket.getTicketNumber());
        return ticket;
    }

    private LocalDateTime getCurrentDateTime() {

        return LocalDateTime.now();
    }


    /**
     * 2 As a parking operator, I want to check if the vehicle has started the parking meter.
     *
     * @param carRegistryNumber
     * @return
     */
    @Override
    public boolean checkIfVehicleStartedParking(String carRegistryNumber) {

        ParkPlaceType[] parkPlaceTypes = ParkPlaceType.values();
        for (int i = 0; i < carParkParameter.getParkPlaceTypeListMap().size(); i++) {
            List<ParkPlace> tmpList = carParkParameter.getParkPlaceTypeListMap().get(parkPlaceTypes[i]);
            for (int j = 0; j < tmpList.size(); j++) {
                if (carRegistryNumber.equals(tmpList.get(j).getCarRegistryNumber())) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 1. As a driver, I want to start the parking meter, so I don’t have to pay the fine for the invalid parking.
     *
     * @param carRegistrationNumber
     * @param parkPlaceType
     * @return message dedicated to the ticket machine
     */
    @Override
    public Ticket startParkAndGetTicket(String carRegistrationNumber, ParkPlaceType parkPlaceType, LocalDateTime localDateTime) {

        int emptyPlaceNumber = getEmptyPlaceNumber(parkPlaceType);
        if (emptyPlaceNumber == -1) {
            Ticket ticket = new Ticket();
            ticket.generateEmptyTicketWithMessage(ErrorsAndMessages.ERROR_NO_EMPTY_PLACES);
            return ticket;
        }

        boolean isReservationOk = doReserveParkPlace(parkPlaceType, emptyPlaceNumber, carRegistrationNumber, localDateTime);
        if (!isReservationOk) {
            Ticket ticket = new Ticket();
            ticket.generateEmptyTicketWithMessage(ErrorsAndMessages.ERROR_RESERVATION);
            return ticket;
        }

        int ticketNumber = generateTicketNumber();
        Ticket ticket = generateTicket(ticketNumber, carParkParameter.getParkPlaceTypeListMap().get(parkPlaceType).get(emptyPlaceNumber));
        carParkParameter.getCurrentTicketsMap().put(ticketNumber, ticket);
        carParkParameter.setLastTicketNumber(ticketNumber);
        return ticket;
    }


    private Ticket generateTicket(int ticketNumber, ParkPlace parkPlace) {

        Ticket ticket = new Ticket(ticketNumber, parkPlace);
        return ticket;
    }


    private boolean doReserveParkPlace(ParkPlaceType parkPlaceType, int emptyPlaceNumber, String carRegistryNumber, LocalDateTime localDateTime) {

        if (checkIfVehicleStartedParking(carRegistryNumber)) {
            return false;
        }
        List<ParkPlace> parkPlaceList = carParkParameter.getParkPlaceTypeListMap().get(parkPlaceType);
        ParkPlace singlePlace = parkPlaceList.get(emptyPlaceNumber);

        singlePlace.doReservation(carRegistryNumber, localDateTime);
        return true;
    }

    private boolean unDoReserveParkPlace(ParkPlace parkPlace) {

        parkPlace.unDoReservation();
        return true;
    }

    private int generateTicketNumber() {

        int ticketNumber = carParkParameter.getLastTicketNumber() + 1;
        return ticketNumber;
    }

    private int getEmptyPlaceNumber(ParkPlaceType parkPlaceType) {

        List<ParkPlace> parkPlaceList = carParkParameter.getParkPlaceTypeListMap().get(parkPlaceType);
        for (int i = 0; i < parkPlaceList.size(); i++) {
            if (parkPlaceList.get(i).getIsEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public CarParkParameter getCarParkParameter() {
        return carParkParameter;
    }

    public void setCarParkParameter(CarParkParameter carParkParameter) {
        this.carParkParameter = carParkParameter;
    }

    public static class ErrorsAndMessages {

        public static final String ERROR_PAYMENT = "!!! PAYMENT ISSUE, PLEASE CONTACT PARKING ADMINISTRATOR !!!";
        public static final String ERROR_FEE = "!!! ERROR DURING THE FEE CALCULATION !!!";
        public static final String ERROR_NO_EMPTY_PLACES = "!!! NO EMPTY PLACES !!!";
        public static final String ERROR_RESERVATION = "!!! PROBLEMS DURING RESERVATION PROCESS, WRONG PARK PLACE TYPE OR THE CAR IS ALREDY PARKED !!!";
        public static final String ERROR_TICKET_NOT_FOUND = "!!! TICKET NOT FOUND IN OUR DATABASE !!!";
        public static final String ERROR_UNDO_RESERVATION = "!!! PAYMENT HAS BEEN REGISTERED BUT RESERVATIONT HAS NOT BEEN REMOVED , PLEASE CONTACT PARKING ADMINISTRATOR !!!";
        public static final String MESSAGE_FAREWELL = "!!! HAVE A NICE DAY !!!";
        public static final String MESSAGE_PAYMENT_OK = "!!! PAYMENT OK !!!";
    }
}

