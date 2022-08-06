package yaremvladimir.monobank.api.kafka.connect.validators;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;

public class TimestampValidator implements ConfigDef.Validator {

    @Override
    public void ensureValid(String name, Object value) {
        String timestamp = (String) value;
        try {
            Instant.parse(timestamp);
        } catch (DateTimeParseException e) {
            throw new ConfigException(name, value,
                    "Unable to parse the timestamp. Make sure it is formatted according to ISO-8601");
        }


    }
}
