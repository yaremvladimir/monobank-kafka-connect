package yaremvladimir.monobank.api.kafka.connect.model;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Operation {
    private String id;
    private Long time;
    private String description;
    private Integer mcc;
    private Integer originalMcc;
    private Integer amount;
    private Integer operationAmount;
    private Integer currencyCode;
    private Integer commissionRate;
    private Integer cashbackAmount;
    private Long balance;
    private Boolean hold;

    public Instant getOperationTime() {
        return Instant.ofEpochSecond(time);
    }
}
