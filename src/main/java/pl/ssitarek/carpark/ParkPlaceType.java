package pl.ssitarek.carpark;

public enum ParkPlaceType {

    REGULAR(1000), VIP(2000);

    //number of regular places started from 1000
    //number of vip places started from 2000
    private int value;

    ParkPlaceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
