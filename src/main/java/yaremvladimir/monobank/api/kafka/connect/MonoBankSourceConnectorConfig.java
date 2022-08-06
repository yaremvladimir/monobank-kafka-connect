package yaremvladimir.monobank.api.kafka.connect;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import yaremvladimir.monobank.api.kafka.connect.validators.TimestampValidator;


public class MonoBankSourceConnectorConfig extends AbstractConfig {

    public static final String TOPIC_CONFIG = "topic";
    private static final String TOPIC_DOC = "Topic to write to";

    public static final String SINCE = "since.timestamp";
    private static final String SINCE_DOC =
            "Only issues updated at or after this time are returned.\n"
                    + "This is a timestamp in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ.";

    public static final String AUTH_TOKEN = "auth.token";
    private static final String AUTH_TOKEN_DOC = "Auth token to authenticate calls";

    public static final String ACCOUNT_ID = "account.id";
    private static final String ACCOUNT_ID_DOC =
            "Billing account id of card or jar from https://api.monobank.ua/personal/client-info";

    public MonoBankSourceConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
        super(config, parsedConfig);
    }

    public MonoBankSourceConnectorConfig(Map<String, String> parsedConfig) {
        this(conf(), parsedConfig);
    }

    public static ConfigDef conf() {
        return new ConfigDef()
                .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, TOPIC_DOC)
                .define(SINCE, Type.STRING, ZonedDateTime.now().minusMonths(1).toInstant().toString(),
                        new TimestampValidator(), Importance.HIGH, SINCE_DOC)
                .define(AUTH_TOKEN, Type.STRING, Importance.HIGH, AUTH_TOKEN_DOC)
                .define(ACCOUNT_ID, Type.STRING, Importance.HIGH, ACCOUNT_ID_DOC);
    }

    public String getTopic() {
        return this.getString(TOPIC_CONFIG);
    }

    public Instant getSince() {
        return Instant.parse(this.getString(SINCE));
    }

    public String getAuthToken() {
        return this.getString(AUTH_TOKEN);
    }

    public String getAccountId() {
        return this.getString(ACCOUNT_ID);
    }
}
