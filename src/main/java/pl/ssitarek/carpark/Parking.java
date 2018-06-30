package pl.ssitarek.carpark;

import java.util.ArrayList;
import java.util.List;

public class Parking {

    private static final int INITIAL_NUMBER_OF_REGULAR = 10;
    private static final int INITIAL_NUMBER_OF_VIP = 5;

    private int numberOfPlaces;
    private int numberOfRegular = INITIAL_NUMBER_OF_REGULAR;
    private int numberOfVip = INITIAL_NUMBER_OF_VIP;
    private List<ParkPlace> parkPlaces = new ArrayList<>();

    public Parking() {

        generatePlaces(numberOfRegular, ParkPlaceType.REGULAR);
        generatePlaces(numberOfVip, ParkPlaceType.VIP);
        numberOfPlaces = numberOfRegular + numberOfVip;
    }

    private void generatePlaces(int numberOfPlaces, ParkPlaceType parkPlaceType) {

        for (int i = 0; i < numberOfPlaces; i++) {
            parkPlaces.add(new ParkPlace(parkPlaceType.getValue() + 1, true, parkPlaceType));
        }
    }
}
