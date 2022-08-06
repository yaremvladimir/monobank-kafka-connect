package yaremvladimir.monobank.api.kafka.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import yaremvladimir.monobank.api.kafka.connect.utils.DateUtil;


public class DateUtilTest {
    @DisplayName("Testing max instant")
    @Test
    public void maxInstant() {
        Instant i1 = ZonedDateTime.now().toInstant();
        Instant i2 = i1.plusSeconds(1);
        assertEquals(DateUtil.maxInstant(i1, i2), i2);
        assertEquals(DateUtil.maxInstant(i2, i1), i2);
    }

    @DisplayName("Testing min instant")
    @Test
    public void minInstant() {
        Instant i1 = ZonedDateTime.now().toInstant();
        Instant i2 = i1.plusSeconds(1);
        assertEquals(DateUtil.minInstant(i1, i2), i1);
        assertEquals(DateUtil.minInstant(i2, i1), i1);
    }
}
