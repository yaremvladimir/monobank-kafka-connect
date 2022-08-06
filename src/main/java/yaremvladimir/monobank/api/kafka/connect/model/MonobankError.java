package yaremvladimir.monobank.api.kafka.connect.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MonoBankError {
    private String errorDescription;
}
