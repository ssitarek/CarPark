package pl.ssitarek.carpark;

public enum ParkPlaceType {

    REGULAR(1000), VIP(2000);

    private int value;

    ParkPlaceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
