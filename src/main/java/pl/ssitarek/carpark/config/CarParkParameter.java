package pl.ssitarek.carpark.config;

import pl.ssitarek.carpark.ParkPlace;
import pl.ssitarek.carpark.ParkPlaceType;
import pl.ssitarek.carpark.PaymentImpl;
import pl.ssitarek.carpark.Ticket;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarParkParameter {

    private int numberOfPlaces;
    private Map<ParkPlaceType, List<ParkPlace>> parkPlaceTypeListMap;
    private int lastTicketNumber;

    private Map<Integer, Ticket> currentTicketsMap;
    private Map<String, BigDecimal> dailyFeeMap;
    private PaymentImpl payment;


    public CarParkParameter(int initialNumberOfRegular, int initialNumberOfVip) {

        parkPlaceTypeListMap = new HashMap<>();

        List<ParkPlace> regularList = generatePlaces(initialNumberOfRegular, ParkPlaceType.REGULAR);
        parkPlaceTypeListMap.put(ParkPlaceType.REGULAR, regularList);

        List<ParkPlace> vipList = generatePlaces(initialNumberOfVip, ParkPlaceType.VIP);
        parkPlaceTypeListMap.put(ParkPlaceType.VIP, vipList);

        numberOfPlaces = parkPlaceTypeListMap.get(ParkPlaceType.REGULAR).size() + parkPlaceTypeListMap.get(ParkPlaceType.VIP).size();

        lastTicketNumber = -1;

        currentTicketsMap = new HashMap<>();
        dailyFeeMap = new HashMap<>();

        payment = new PaymentImpl();

    }

    private List<ParkPlace> generatePlaces(int numberOfPlaces, ParkPlaceType parkPlaceType) {

        List<ParkPlace> parkPlaces = new ArrayList<>();
        for (int i = 0; i < numberOfPlaces; i++) {
            parkPlaces.add(new ParkPlace(parkPlaceType.getValue() + i, parkPlaceType));
        }
        return parkPlaces;
    }

    public int getNumberOfPlaces() {
        return numberOfPlaces;
    }

    public Map<ParkPlaceType, List<ParkPlace>> getParkPlaceTypeListMap() {
        return parkPlaceTypeListMap;
    }

    public Map<Integer, Ticket> getCurrentTicketsMap() {
        return currentTicketsMap;
    }

    public Map<String, BigDecimal> getDailyFeeMap() {
        return dailyFeeMap;
    }


    public PaymentImpl getPayment() {
        return payment;
    }

    public void setLastTicketNumber(int lastTicketNumber) {
        this.lastTicketNumber = lastTicketNumber;
    }

    public int getLastTicketNumber() {
        return lastTicketNumber;
    }

    public void setNumberOfPlaces(int numberOfPlaces) {
        this.numberOfPlaces = numberOfPlaces;
    }

    public void setParkPlaceTypeListMap(Map<ParkPlaceType, List<ParkPlace>> parkPlaceTypeListMap) {
        this.parkPlaceTypeListMap = parkPlaceTypeListMap;
    }

    public void setCurrentTicketsMap(Map<Integer, Ticket> currentTicketsMap) {
        this.currentTicketsMap = currentTicketsMap;
    }

    public void setDailyFeeMap(Map<String, BigDecimal> dailyFeeMap) {
        this.dailyFeeMap = dailyFeeMap;
    }

    public void setPayment(PaymentImpl payment) {
        this.payment = payment;
    }
}
