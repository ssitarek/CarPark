package pl.ssitarek.carpark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarparkApplication {

    public static void main(String[] args) {

        SpringApplication.run(CarparkApplication.class, args);

//        Parking parking = new Parking("SomeCarParkName", "SomeCarParkAddress");
//        System.out.println("Parking application has just started:\n"+parking.toString());
    }
}
