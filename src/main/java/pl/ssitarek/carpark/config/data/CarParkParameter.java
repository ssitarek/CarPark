package pl.ssitarek.carpark.config.data;

import pl.ssitarek.carpark.AcceptedCurrency;
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

    private String carParkName;
    private String carParkAddress;
    private int numberOfPlaces;
    private Map<ParkPlaceType, List<ParkPlace>> parkPlaceTypeListMap;
    private int lastTicketNumber;

    private Map<Integer, Ticket> currentTicketsMap;
    private Map<String, Map<AcceptedCurrency, BigDecimal>> dailyIncomeMap;
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
        dailyIncomeMap = new HashMap<>();

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

    public Map<String, Map<AcceptedCurrency, BigDecimal>> getDailyIncomeMap() {
        return dailyIncomeMap;
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

    public String getCarParkName() {
        return carParkName;
    }

    public void setCarParkName(String carParkName) {
        this.carParkName = carParkName;
    }

    public String getCarParkAddress() {
        return carParkAddress;
    }

    public void setCarParkAddress(String carParkAddress) {
        this.carParkAddress = carParkAddress;
    }
}
