package pl.ssitarek.carpark;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class Parking {


    public static final String ERROR_PAYMENT = "payment issue, please contact parking  administrator";
    public static final String ERROR_NO_EMPLTY_PLACES = "no empty places";
    public static final String ERROR_RESERVATION = "problems during reservation process";
    public static final String ERROR_TICKET_NOT_FOUND = "ticket not found in our database";
    public static final String ERROR_UNDO_RESERVATION = "payment has been registered but reservation has not been removed, please contact CarPark administrator";
    public static final String MESSAGE_FAREWELL = "have a nice day";
    public static final String MESSAGE_PAYMENT_OK = "payment OK";

    /**
     * these two are necessary for tests
     */
    private static final int INITIAL_NUMBER_OF_REGULAR = 5;
    private static final int INITIAL_NUMBER_OF_VIP = 3;

    @Value("${carPark.configuration.numberOfRegularParkPlaces}")
    private int initialNumberOfRegular;

    @Value("${carPark.configuration.numberOfVipParkPlaces}")
    private int initialNumberOfVip;

    @Value("${carPark.configuration.name}")
    private String name;

    @Value("${carPark.configuration.address}")
    private String address;
    private int numberOfPlaces;
    private Map<ParkPlaceType, List<ParkPlace>> parkPlaceTypeListMap = new HashMap<>();
    private Map<Integer, Ticket> ticketsMapForSingleDay = new HashMap<>();
    private Map<String, Double> dailyFeeMap = new HashMap<>();

    public Parking() {

    }

    @PostConstruct
    public void build() {

        List<ParkPlace> regularList = generatePlaces(initialNumberOfRegular, ParkPlaceType.REGULAR);
        parkPlaceTypeListMap.put(ParkPlaceType.REGULAR, regularList);

        List<ParkPlace> vipList = generatePlaces(initialNumberOfVip, ParkPlaceType.VIP);
        parkPlaceTypeListMap.put(ParkPlaceType.VIP, vipList);

        numberOfPlaces = parkPlaceTypeListMap.get(ParkPlaceType.REGULAR).size() + parkPlaceTypeListMap.get(ParkPlaceType.VIP).size();

        System.out.println("has been created " + name);
    }


    /**
     * This constructor is necessary for tests
     *
     * @param name
     * @param address
     */
    public Parking(String name, String address) {

        this.name = name;
        this.address = address;

        List<ParkPlace> regularList = generatePlaces(INITIAL_NUMBER_OF_REGULAR, ParkPlaceType.REGULAR);
        parkPlaceTypeListMap.put(ParkPlaceType.REGULAR, regularList);

        List<ParkPlace> vipList = generatePlaces(INITIAL_NUMBER_OF_VIP, ParkPlaceType.VIP);
        parkPlaceTypeListMap.put(ParkPlaceType.VIP, vipList);

        numberOfPlaces = parkPlaceTypeListMap.get(ParkPlaceType.REGULAR).size() + parkPlaceTypeListMap.get(ParkPlaceType.VIP).size();
    }


    private List<ParkPlace> generatePlaces(int numberOfPlaces, ParkPlaceType parkPlaceType) {

        List<ParkPlace> parkPlaces = new ArrayList<>();
        for (int i = 0; i < numberOfPlaces; i++) {
            parkPlaces.add(new ParkPlace(parkPlaceType.getValue() + i, parkPlaceType));
        }
        return parkPlaces;
    }


    @Override
    public String toString() {
        return "Parking{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", numberOfPlaces=" + numberOfPlaces +
                ", numberOfRegular=" + parkPlaceTypeListMap.get(ParkPlaceType.REGULAR).size() +
                ", numberOfVip=" + parkPlaceTypeListMap.get(ParkPlaceType.VIP).size() +
                '}';
    }


    /**
     * 5. As a parking owner, I want to know how much money was earned during a given day
     *
     * @param date
     * @return
     */
    public double getDailyFeeForSingleDate(String date) {

        return Optional.ofNullable(dailyFeeMap.get(date)).orElse(0.0);

    }

    /**
     * 4. As a driver, I want to know how much I have to pay for parking.
     *
     * @param ticketNumber
     * @return
     */
    public double calculateFee(int ticketNumber, LocalDateTime currentDateTime) {

        Ticket ticket = ticketsMapForSingleDay.get(ticketNumber);
        if (ticket == null) {
            return 0.0;
        }
        return ticket.calculateTicketFee(currentDateTime);
    }


    /**
     * 3. As a driver, I want to stop the parking meter, so that I pay only for the actual parking time
     *
     * @param ticketNumber
     * @return
     */
    public Ticket stopPark(int ticketNumber) {

        Ticket ticket = ticketsMapForSingleDay.get(ticketNumber);
        if (ticket == null) {
            ticket = new Ticket();
            ticket.updateTicketData(null, 0, ERROR_TICKET_NOT_FOUND);
            return ticket;
        }

        LocalDateTime currentDateTime = getCurrentDateTime();
        double fee = calculateFee(ticketNumber, currentDateTime);
        boolean isPaymentOk = doPayment(ticket.getTicketFee());
        if (isPaymentOk == false) {
            ticket.updateTicketData(currentDateTime, fee, ERROR_PAYMENT);
        }
        ticket.updateTicketData(currentDateTime, fee, MESSAGE_PAYMENT_OK);

        //prepare to calculate dailyFee
        String workingDay = convertTimeToString(ticket.getReservedTo());
        if (dailyFeeMap.get(workingDay) == null) {
            dailyFeeMap.put(workingDay, 0.0);
        }
        Double val = dailyFeeMap.get(workingDay);
        val += ticket.getTicketFee();
        dailyFeeMap.put(workingDay, val);

        if (!unDoReserveParkPlace(ticket.getParkPlace())) {
            ticket.updateTicketData(currentDateTime, fee, ERROR_UNDO_RESERVATION);
        }

        ticket.updateTicketData(currentDateTime, fee, MESSAGE_FAREWELL);
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
    public boolean checkIfVehicleStartedParking(String carRegistryNumber) {

        ParkPlaceType[] parkPlaceTypes = ParkPlaceType.values();
        for (int i = 0; i < parkPlaceTypeListMap.size(); i++) {
            List<ParkPlace> tmpList = parkPlaceTypeListMap.get(parkPlaceTypes[i]);
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
    public Ticket startParkAndGetTicket(String carRegistrationNumber, ParkPlaceType parkPlaceType, LocalDateTime localDateTime) {

        int emptyPlaceNumber = getEmptyPlaceNumber(parkPlaceType);
        if (emptyPlaceNumber == -1) {
            Ticket ticket = new Ticket();
            ticket.generateEmptyTicketWithMessage(ERROR_NO_EMPLTY_PLACES);
            return ticket;
        }

        boolean isReservationOk = doReserveParkPlace(parkPlaceType, emptyPlaceNumber, carRegistrationNumber, localDateTime);
        if (!isReservationOk) {
            Ticket ticket = new Ticket();
            ticket.generateEmptyTicketWithMessage(ERROR_RESERVATION);
            return ticket;
        }

        int ticketNumber = generateTicketNumber();
        Ticket ticket = generateTicket(ticketNumber, parkPlaceTypeListMap.get(parkPlaceType).get(emptyPlaceNumber));
        ticketsMapForSingleDay.put(ticketNumber, ticket);
        return ticket;
    }


    private boolean doPayment(double fee) {

        //assume that payment is OK
        return true;
    }


    private Ticket generateTicket(int ticketNumber, ParkPlace parkPlace) {

        Ticket ticket = new Ticket(ticketNumber, parkPlace);
        return ticket;
    }


    private boolean doReserveParkPlace(ParkPlaceType parkPlaceType, int emptyPlaceNumber, String carRegistryNumber, LocalDateTime localDateTime) {

        if (checkIfVehicleStartedParking(carRegistryNumber)) {
            return false;
        }
        List<ParkPlace> parkPlaceList = parkPlaceTypeListMap.get(parkPlaceType);
        ParkPlace singlePlace = parkPlaceList.get(emptyPlaceNumber);

        singlePlace.doReservation(carRegistryNumber, localDateTime);
        return true;
    }

    private boolean unDoReserveParkPlace(ParkPlace parkPlace) {

        parkPlace.unDoReservation();
        return true;
    }

    private int generateTicketNumber() {

        return ticketsMapForSingleDay.size();
    }

    private int getEmptyPlaceNumber(ParkPlaceType parkPlaceType) {

        List<ParkPlace> parkPlaceList = parkPlaceTypeListMap.get(parkPlaceType);
        for (int i = 0; i < parkPlaceList.size(); i++) {
            if (parkPlaceList.get(i).getIsEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private String convertTimeToString(LocalDateTime localDateTime) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(localDateTime.getYear());
        stringBuilder.append(String.format("%02d", localDateTime.getMonthValue()));
        stringBuilder.append(String.format("%02d", localDateTime.getDayOfMonth()));
        return stringBuilder.toString();
    }
}
