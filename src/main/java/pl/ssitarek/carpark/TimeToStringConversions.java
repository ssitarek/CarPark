package pl.ssitarek.carpark;

import java.time.LocalDateTime;

public class TimeToStringConversions {

    public static final String doConversion(LocalDateTime localDateTime){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(localDateTime.getYear());
        stringBuilder.append(String.format("%02d", localDateTime.getMonthValue()));
        stringBuilder.append(String.format("%02d", localDateTime.getDayOfMonth()));
        return stringBuilder.toString();
    }
}
