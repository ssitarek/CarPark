package pl.ssitarek.carpark;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParkingImpl implements Parking{

    @Value("${carPark.configuration.numberOfRegularParkPlaces}")
    private int initialNumberOfRegular;

    @Value("${carPark.configuration.numberOfVipParkPlaces}")
    private int initialNumberOfVip;

    @Value("${carPark.configuration.name}")
    private String name;

    @Value("${carPark.configuration.address}")
    private String address;

    private int numberOfPlaces;
    private Map<ParkPlaceType, List<ParkPlace>> parkPlaceTypeListMap;
    private int lastTicketNumber;

    private Map<Integer, Ticket> currentTicketsMap;
    private Map<String, BigDecimal> dailyFeeMap;
    private PaymentImpl payment;

    public ParkingImpl() {

    }

    @PostConstruct
    public void build() {

        parkPlaceTypeListMap = new HashMap<>();

        List<ParkPlace> regularList = generatePlaces(initialNumberOfRegular, ParkPlaceType.REGULAR);
        parkPlaceTypeListMap.put(ParkPlaceType.REGULAR, regularList);

        List<ParkPlace> vipList = generatePlaces(initialNumberOfVip, ParkPlaceType.VIP);
        parkPlaceTypeListMap.put(ParkPlaceType.VIP, vipList);

        numberOfPlaces = parkPlaceTypeListMap.get(ParkPlaceType.REGULAR).size() + parkPlaceTypeListMap.get(ParkPlaceType.VIP).size();
        lastTicketNumber=-1;

        currentTicketsMap = new HashMap<>();
        dailyFeeMap = new HashMap<>();

        payment = new PaymentImpl();

        System.out.println("has been created " + name);
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
    @Override
    public BigDecimal getDailyFeeForSingleDate(String date) {

        return dailyFeeMap.get(date);//Optional.ofNullable(dailyFeeMap.get(date)).orElse(new BigDecimal(0.0));

    }

    /**
     * 4. As a driver, I want to know how much I have to pay for parking.
     *
     * @param ticketNumber
     * @return
     */
    @Override
    public BigDecimal calculateFee(int ticketNumber, LocalDateTime currentDateTime) {

        Ticket ticket = currentTicketsMap.get(ticketNumber);
        if (ticket == null) {
            return null;//return new BigDecimal(0.0)
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

        Ticket ticket = currentTicketsMap.get(ticketNumber);
        if (ticket == null) {
            ticket = new Ticket();
            ticket.updateTicketData(null, null, ErrorsAndMessages.ERROR_TICKET_NOT_FOUND);
            return ticket;
        }

        LocalDateTime currentDateTime = getCurrentDateTime();
        BigDecimal fee = calculateFee(ticketNumber, currentDateTime);
        if (fee == null) {
            ticket = new Ticket();
            ticket.updateTicketData(null, null, "fee == zero ale czemu??");
            return ticket;
        }

        payment.doPayment(fee, true);
        boolean aa = payment.checkPaymentStatus();
        if (payment.checkPaymentStatus() == false) {
            ticket.updateTicketData(currentDateTime, fee, ErrorsAndMessages.ERROR_PAYMENT);
        }
        ticket.updateTicketData(currentDateTime, fee, ErrorsAndMessages.MESSAGE_PAYMENT_OK);

        //prepare to calculate dailyFee
        String workingDay = convertTimeToString(ticket.getReservedTo());
        if (dailyFeeMap.get(workingDay) == null) {
            dailyFeeMap.put(workingDay, new BigDecimal(0.0));
        }
        BigDecimal val = dailyFeeMap.get(workingDay);
        val = val.add(ticket.getTicketFee());
        dailyFeeMap.put(workingDay, val);

        if (!unDoReserveParkPlace(ticket.getParkPlace())) {
            ticket.updateTicketData(currentDateTime, fee, ErrorsAndMessages.ERROR_UNDO_RESERVATION);
        }

        ticket.updateTicketData(currentDateTime, fee, ErrorsAndMessages.MESSAGE_FAREWELL);
        currentTicketsMap.remove(ticket.getTicketNumber());
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
    @Override
    public Ticket startParkAndGetTicket(String carRegistrationNumber, ParkPlaceType parkPlaceType, LocalDateTime localDateTime) {

        int emptyPlaceNumber = getEmptyPlaceNumber(parkPlaceType);
        if (emptyPlaceNumber == -1) {
            Ticket ticket = new Ticket();
            ticket.generateEmptyTicketWithMessage(ErrorsAndMessages.ERROR_NO_EMPLTY_PLACES);
            return ticket;
        }

        boolean isReservationOk = doReserveParkPlace(parkPlaceType, emptyPlaceNumber, carRegistrationNumber, localDateTime);
        if (!isReservationOk) {
            Ticket ticket = new Ticket();
            ticket.generateEmptyTicketWithMessage(ErrorsAndMessages.ERROR_RESERVATION);
            return ticket;
        }

        int ticketNumber = generateTicketNumber();
        Ticket ticket = generateTicket(ticketNumber, parkPlaceTypeListMap.get(parkPlaceType).get(emptyPlaceNumber));
        currentTicketsMap.put(ticketNumber, ticket);
        lastTicketNumber++;
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

        int ticketNumber = lastTicketNumber+1;
        return ticketNumber;
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

    static class ErrorsAndMessages {

        static String ERROR_PAYMENT = "payment issue, please contact parking  administrator";
        static String ERROR_NO_EMPLTY_PLACES = "no empty places";
        static String ERROR_RESERVATION = "problems during reservation process";
        static String ERROR_TICKET_NOT_FOUND = "ticket not found in our database";
        static String ERROR_UNDO_RESERVATION = "payment has been registered but reservation has not been removed, please contact CarPark administrator";
        static String MESSAGE_FAREWELL = "have a nice day";
        static String MESSAGE_PAYMENT_OK = "payment OK";
    }
}

