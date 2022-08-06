package yaremvladimir.monobank.api.kafka.connect.utils;

import java.time.Instant;

public class DateUtil {
    public static Instant maxInstant(Instant i1, Instant i2){
        return i1.compareTo(i2) > 0 ? i1 : i2;
    }

    public static Instant minInstant(Instant i1, Instant i2){
        return i1.compareTo(i2) < 0 ? i1 : i2;
    }
}
