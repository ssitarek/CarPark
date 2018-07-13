package pl.ssitarek.carpark.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.ssitarek.carpark.ParkingImpl;
import pl.ssitarek.carpark.config.data.CarParkParameter;

@Configuration
public class CarParkConfiguration {

    @Value("${carParkNumberOfRegularParkPlaces}")
    private int initialNumberOfRegular;

    @Value("${carParkNumberOfVipParkPlaces}")
    private int initialNumberOfVip;

    @Value("${carParkName}")
    private String name;

    @Value("${carParkAddress}")
    private String address;

    @Autowired
    CarParkParameter carParkParameter;

    @Bean
    public CarParkParameter carParkParameter() {

        CarParkParameter carParkParameter = new CarParkParameter(initialNumberOfRegular, initialNumberOfVip);
        carParkParameter.setCarParkAddress(address);
        carParkParameter.setCarParkName(name);
        return carParkParameter;
    }

    @Bean
    public ParkingImpl parkingImpl() {

        ParkingImpl parkingImpl = new ParkingImpl();
        parkingImpl.setCarParkParameter(carParkParameter);
        return parkingImpl;
    }
}
