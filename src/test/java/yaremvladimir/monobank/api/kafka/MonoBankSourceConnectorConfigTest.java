package yaremvladimir.monobank.api.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import yaremvladimir.monobank.api.kafka.connect.MonoBankSourceConnectorConfig;

import static yaremvladimir.monobank.api.kafka.connect.MonoBankSourceConnectorConfig.*;

public class MonoBankSourceConnectorConfigTest {
    private ConfigDef configDef = MonoBankSourceConnectorConfig.conf();

    private Map<String, String> initialConfig() {
        Map<String, String> baseProps = new HashMap<>();
        baseProps.put(TOPIC_CONFIG, "monobank-operations");
        baseProps.put(SINCE, "2017-04-26T01:23:45Z");
        baseProps.put(AUTH_TOKEN, "foo");
        baseProps.put(ACCOUNT_ID, "bar");
        return baseProps;
    }


    @Test
    public void doc() {
        System.out.println(MonoBankSourceConnectorConfig.conf().toRst());
    }

    @Test
    @DisplayName("Verifying that initial config is valid")
    public void initialConfigIsValid() {
        assert (configDef.validate(initialConfig()).stream()
                .allMatch(configValue -> configValue.errorMessages().size() == 0));
    }

    @Test
    @DisplayName("Validating SINCE config")
    public void validateSince() {
        Map<String, String> config = initialConfig();
        config.put(SINCE, "something-not-a-date");
        ConfigValue configValue = configDef.validateAll(config).get(SINCE);
        assert (configValue.errorMessages().size() > 0);
    }

    @Test
    @DisplayName("Validating auth token")
    public void validateUsername() {
        Map<String, String> config = initialConfig();
        config.put(AUTH_TOKEN, "jioJIOJUIOUiouidosiio88Hhhuj");
        ConfigValue configValue = configDef.validateAll(config).get(AUTH_TOKEN);
        assert (configValue.errorMessages().size() == 0);
    }

    @Test
    @DisplayName("Validating account id")
    public void validateAccountId() {
        Map<String, String> config = initialConfig();
        config.put(ACCOUNT_ID, "hmoCO44kmoyBpmOg");
        ConfigValue configValue = configDef.validateAll(config).get(ACCOUNT_ID);
        assert (configValue.errorMessages().size() == 0);
    }
}
