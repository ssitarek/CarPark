package pl.ssitarek.carpark.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.ssitarek.carpark.ParkingImpl;

@Configuration
public class CarParkConfiguration {

    @Value("${carPark.configuration.numberOfRegularParkPlaces}")
    private int initialNumberOfRegular;

    @Value("${carPark.configuration.numberOfVipParkPlaces}")
    private int initialNumberOfVip;

    @Value("${carPark.configuration.name}")
    private String name;

    @Value("${carPark.configuration.address}")
    private String address;


    @Autowired
    CarParkParameter carParkParameter;

    @Bean
    public CarParkParameter carParkParameter(){

        CarParkParameter carParkParameter = new CarParkParameter(initialNumberOfRegular, initialNumberOfVip);
        carParkParameter.setCarParkAddress(address);
        carParkParameter.setCarParkName(name);
        return carParkParameter;
    }

    @Bean
    public ParkingImpl parkingImpl(){

        ParkingImpl parkingImpl = new ParkingImpl();
        parkingImpl.setCarParkParameter(carParkParameter);
        return parkingImpl;
    }
}
