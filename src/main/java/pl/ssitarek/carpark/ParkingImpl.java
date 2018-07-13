package pl.ssitarek.carpark;

import org.springframework.stereotype.Service;
import pl.ssitarek.carpark.config.data.CarParkParameter;
import pl.ssitarek.carpark.config.data.ErrorsAndMessages;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param dateString the string composed of yyyyMMdd e.g. "20180701"
     * @return map of fee in different currencies in 1 cent (of PLN, of EUR etc.)
     * 5. As a parking owner, I want to know how much money was earned during a given day
     */
    @Override
    public Map<AcceptedCurrency, BigDecimal> getDailyIncomeForSingleDate(String dateString) {

        return carParkParameter.getDailyIncomeMap().get(dateString);//Optional.ofNullable(dailyFeeMap.get(date)).orElse(new BigDecimal(0.0));
    }

    /**
     * @param ticketNumber    number that is equal or higher than 0;
     * @param currentDateTime date and time of calculation
     * @return fee in (1 centPLN)
     * 4. As a driver, I want to know how much I have to pay for parking.
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
     * @param ticketNumber    number that is equal or higher than 0;
     * @param currentDateTime date and time of calculation     *
     * @return ticket with the message that are in const MESSAGE_FAREWELL e.g. "!!! HAVE A NICE DAY !!!"
     * 3. As a driver, I want to stop the parking meter, so that I pay only for the actual parking time
     */
    @Override
    public Ticket stopPark(int ticketNumber, LocalDateTime currentDateTime, AcceptedCurrency acceptedCurrency) {

        Ticket ticket = carParkParameter.getCurrentTicketsMap().get(ticketNumber);
        if (ticket == null) {
            ticket = new Ticket();
            ticket.updateTicketData(null, null, ErrorsAndMessages.ERROR_TICKET_NOT_FOUND);
            return ticket;
        }

        BigDecimal fee = calculateFee(ticketNumber, currentDateTime);
        if (fee == null) {
            ticket = new Ticket();
            ticket.updateTicketData(null, null, ErrorsAndMessages.ERROR_FEE);
            return ticket;
        }

        carParkParameter.getPayment().doPayment(fee, acceptedCurrency, true);
        boolean isPaymentOk = carParkParameter.getPayment().checkPaymentStatus();
        if (!isPaymentOk) {
            ticket.updateTicketData(currentDateTime, fee, ErrorsAndMessages.ERROR_PAYMENT);
        }
        ticket.updateTicketData(currentDateTime, fee, ErrorsAndMessages.MESSAGE_PAYMENT_OK);

        //prepare to calculate dailyFee
        String workingDay = TimeToStringConversions.doConversion(ticket.getReservedTo());
        if (carParkParameter.getDailyIncomeMap().get(workingDay) == null) {
            carParkParameter.getDailyIncomeMap().put(workingDay, prepareEmptyDailyFeeMap());
        }

        Map<AcceptedCurrency, BigDecimal> mapOfDailyFee = carParkParameter.getDailyIncomeMap().get(workingDay);
        BigDecimal val = mapOfDailyFee.get(acceptedCurrency);

        //no possibility to do val+=ticket.getticketFee() due to the fact that it is BigDecimal
        val = val.add(ticket.getTicketFee());
        mapOfDailyFee.put(acceptedCurrency, val);
        carParkParameter.getDailyIncomeMap().put(workingDay, mapOfDailyFee);

        if (!unDoReserveParkPlace(ticket.getParkPlace())) {
            ticket.updateTicketData(currentDateTime, fee, ErrorsAndMessages.ERROR_UNDO_RESERVATION);
        }

        ticket.updateTicketData(currentDateTime, fee, ErrorsAndMessages.MESSAGE_FAREWELL);
        carParkParameter.getCurrentTicketsMap().remove(ticket.getTicketNumber());
        return ticket;
    }

    /**
     * create empty fee map
     *
     * @return income map with proper currency, and zero as fee
     */

    public static Map<AcceptedCurrency, BigDecimal> prepareEmptyDailyFeeMap() {
        Map<AcceptedCurrency, BigDecimal> singleDayFee = new HashMap<>();
        for (AcceptedCurrency acceptedCurrency: AcceptedCurrency.values()){
            singleDayFee.put(acceptedCurrency, new BigDecimal(0));
        }
        return singleDayFee;
    }


    /**
     * @param carRegistryNumber string that has been validated inside (e.g. has got the length [1,15])
     * @return true if started, false otherwise
     * 2 As a parking operator, I want to check if the vehicle has started the parking meter.
     */
    @Override
    public boolean checkIfVehicleStartedParking(String carRegistryNumber) {

        if (!carRegistryValidator(carRegistryNumber)) {
            return false;
        }

        ParkPlaceType[] parkPlaceTypes = ParkPlaceType.values();
        //check every type of parkPlace (REGULAR or VIP)
        for (int i = 0; i < carParkParameter.getParkPlaceTypeListMap().size(); i++) {
            List<ParkPlace> tmpList = carParkParameter.getParkPlaceTypeListMap().get(parkPlaceTypes[i]);
            //check every parkPlace in the particular list (list of REGULAR or list of VIP)
            for (int j = 0; j < tmpList.size(); j++) {
                if (carRegistryNumber.equals(tmpList.get(j).getCarRegistryNumber())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean carRegistryValidator(String carRegistryNumber) {

        if ((carRegistryNumber.length() == 0) || (carRegistryNumber.length() > 15)) {
            return false;
        }
        return true;
    }


    /**
     * @param carRegistrationNumber string that has been validated inside (e.g. has got the length [1,15])
     * @param parkPlaceType         type of place i.e. REGULAR or VIP
     * @param currentDateTime       date and time of calculation     *
     * @return message dedicated to the ticket machine
     * 1. As a driver, I want to start the parking meter, so I don’t have to pay the fine for the invalid parking.
     */
    @Override
    public Ticket startParkAndGetTicket(String carRegistrationNumber, ParkPlaceType parkPlaceType, LocalDateTime currentDateTime) {

        if (!carRegistryValidator(carRegistrationNumber)) {
            Ticket ticket = new Ticket();
            ticket.generateEmptyTicketWithMessage(ErrorsAndMessages.ERROR_INVALID_REGISTRY_NUMBER);
            return ticket;
        }

        int emptyPlaceNumber = getEmptyPlaceNumber(parkPlaceType);
        if (emptyPlaceNumber == -1) {
            Ticket ticket = new Ticket();
            ticket.generateEmptyTicketWithMessage(ErrorsAndMessages.ERROR_NO_EMPTY_PLACES);
            return ticket;
        }

        boolean isReservationOk = doReserveParkPlace(parkPlaceType, emptyPlaceNumber, carRegistrationNumber, currentDateTime);
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
}

