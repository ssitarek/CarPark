package pl.ssitarek.carpark;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParkPlace {

    private int placeNumber;
    private boolean isEmpty = true;
    private LocalDateTime reservedFrom = null;
    private String carRegistryNumber = "";
    private ParkPlaceType placeType;
    private ParkPlaceFee parkPlaceFeeData;


    public ParkPlace(int number, ParkPlaceType placeType) {
        this.placeNumber = number;
        this.placeType = placeType;
        this.parkPlaceFeeData = createFeeData(placeType);
    }

    private ParkPlaceFee createFeeData(ParkPlaceType placeType) {

        switch (placeType) {
            case REGULAR:
                return new ParkPlaceFee(new BigDecimal(100.0), new BigDecimal(200.0), 1.5);
            case VIP:
                return new ParkPlaceFee(BigDecimal.ZERO, new BigDecimal(200.0), 1.2);
            default:
                return new ParkPlaceFee(new BigDecimal(100.0), new BigDecimal(200.0), 1.5);
        }
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

    public void setPlaceNumber(int placeNumber) {
        this.placeNumber = placeNumber;
    }
}
