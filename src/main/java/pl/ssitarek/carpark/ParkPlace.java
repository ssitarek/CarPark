package pl.ssitarek.carpark;

import java.time.LocalDateTime;

public class ParkPlace {

    private int placeNumber;
    private boolean isEmpty;
    private LocalDateTime reservedFrom = null;
    private String carRegistryNumber = "";
    private ParkPlaceType placeType;
    private ParkPlaceFee parkPlaceFeeData;


    public ParkPlace(int number, ParkPlaceType placeType) {
        this.placeNumber = number;
        this.isEmpty = true;
        this.placeType = placeType;
        this.parkPlaceFeeData = createFeeData(placeType);
    }

    private ParkPlaceFee createFeeData(ParkPlaceType placeType) {

        switch (placeType) {
            case REGULAR:
                return new ParkPlaceFee(1.0, 2.0, 1.5);
            case VIP:
                return new ParkPlaceFee(0.0, 2.0, 1.2);
        }
        return null;
    }


    public void doReservation(String inCarRegistryNumber, LocalDateTime inReservedFrom) {

        isEmpty = false;
        reservedFrom = inReservedFrom;
        carRegistryNumber = inCarRegistryNumber;
    }

    public void unDoReservation() {

        isEmpty = true;
        reservedFrom = null;
        carRegistryNumber = "";
    }

    public boolean getIsEmpty() {
        return isEmpty;
    }

    public LocalDateTime getReservedFrom() {
        return reservedFrom;
    }

    public int getPlaceNumber() {
        return placeNumber;
    }

    public ParkPlaceType getPlaceType() {
        return placeType;
    }

    public ParkPlaceFee getParkPlaceFeeData() {
        return parkPlaceFeeData;
    }

    public String getCarRegistryNumber() {
        return carRegistryNumber;
    }


}
