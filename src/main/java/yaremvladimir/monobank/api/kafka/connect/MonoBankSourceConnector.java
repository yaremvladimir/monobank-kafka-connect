package yaremvladimir.monobank.api.kafka.connect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonoBankSourceConnector extends SourceConnector {
    private static Logger log = LoggerFactory.getLogger(MonoBankSourceConnector.class);
    private MonoBankSourceConnectorConfig config;

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        log.info("Starting MonobankSourceConnector");
        config = new MonoBankSourceConnectorConfig(map);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MonoBankSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int i) {
        // Define the individual task configurations that will be executed.
        ArrayList<Map<String, String>> configs = new ArrayList<>(1);
        configs.add(config.originalsStrings());
        return configs;
    }

    @Override
    public void stop() {
        log.info("Stopping MonobankSourceConnector");
        // Do nothing
    }

    @Override
    public ConfigDef config() {
        return MonoBankSourceConnectorConfig.conf();
    }
}
