package pl.ssitarek.carpark;

public class ParkPlace {

    private int number;
    private boolean isEmpty;
    private ParkPlaceType placeType;
    private ParkPlaceFee parkPlaceFee;


    public ParkPlace(int number, boolean isEmpty, ParkPlaceType placeType) {
        this.number = number;
        this.isEmpty = isEmpty;
        this.placeType = placeType;
        this.parkPlaceFee = createFee(placeType);
    }

    private ParkPlaceFee createFee(ParkPlaceType placeType) {

        switch (placeType) {
            case REGULAR:
                return new ParkPlaceFee(1.0, 2.0, 1.5);
            case VIP:
                return new ParkPlaceFee(0.0, 2.0, 1.2);
        }
        return null;
    }
}
