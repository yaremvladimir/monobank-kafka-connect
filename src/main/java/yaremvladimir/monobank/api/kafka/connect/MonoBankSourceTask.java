package yaremvladimir.monobank.api.kafka.connect;

import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.ACCOUNT_ID;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.BALANCE_AFTER_OPERATION;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.KEY_SCHEMA;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.OPERATION_AMOUNT;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.OPERATION_CASHBACK_AMOUNT;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.OPERATION_COMISSION_RATE;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.OPERATION_CURRENCY_CODE;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.OPERATION_DESCRIPTION;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.OPERATION_HOLD;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.OPERATION_ID;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.OPERATION_MCC;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.OPERATION_ORIGINAL_MCC;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.OPERATION_TIME;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.TO;
import static yaremvladimir.monobank.api.kafka.connect.MonoBankOperationsSchema.VALUE_SCHEMA;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import yaremvladimir.monobank.api.kafka.connect.model.Operation;
import yaremvladimir.monobank.api.kafka.connect.utils.DateUtil;


public class MonoBankSourceTask extends SourceTask {
    static final Logger log = LoggerFactory.getLogger(MonoBankSourceTask.class);
    private MonoBankSourceConnectorConfig config;
    private MonoBankHttpClient httpClient;

    private Instant from;
    private Instant to;

    @Override
    public String version() {
        return VersionUtil.getVersion();
    }

    @Override
    public void start(Map<String, String> map) {
        config = new MonoBankSourceConnectorConfig(map);
        httpClient = new MonoBankHttpClient(config);
        initializeVariables();
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {
        if (from.plus(10, ChronoUnit.MINUTES).isAfter(to)) {
            log.info("From and To are nearly equal. Ignoring data for 10 minutes.");
            Thread.sleep(1000 * 10);
            // grace period of 10 minutes
            return null;
        }
        log.info(String.format("Polling operations for account '%s' from '%s' to '%s'", config.getAccountId(), from.toString(), to.toString()));
        List<Operation> operations = httpClient.getOperations(from, to);
        from = getTo(from);
        to = getTo(from);
        log.info(String.format("Found %s new operations", operations.size()));

        final ArrayList<SourceRecord> records = new ArrayList<>();
        operations.forEach(operation -> {
            records.add(generateSourceRecord(operation));
        });
        return records;
    }

    @Override
    public void stop() {
        // do nothing
    }

    private void initializeVariables() {
        Map<String, Object> sinceOffset = null;
        sinceOffset = context.offsetStorageReader().offset(sourcePartition());
        if (sinceOffset == null) {
            from = config.getSince();
            to = getTo(from);
        } else {
            Object operationTime = sinceOffset.get(OPERATION_TIME);
            Object toTime = sinceOffset.get(TO);
            if (operationTime != null && operationTime instanceof String) {
                from = Instant.parse((String) operationTime);
            } else {
                from = config.getSince();
            }
            if (toTime != null && toTime instanceof String) {
                to = Instant.parse((String) toTime);
            } else {
                to = getTo(from);
            }
        }
    }

    private Instant getTo(Instant from) {
        Instant now = Instant.now();
        Instant theTo = LocalDateTime.ofInstant(from, ZoneId.of("UTC")).with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS)
                .plus(1, ChronoUnit.MONTHS).toInstant(ZoneOffset.UTC);;
        return DateUtil.minInstant(now, theTo);
    }


    private SourceRecord generateSourceRecord(Operation operation) {
        return new SourceRecord(sourcePartition(), 
                sourceOffset(operation.getOperationTime()),
                config.getTopic(),
                null, // partition will be inferred by the framework
                KEY_SCHEMA,
                buildRecordKey(operation),
                VALUE_SCHEMA,
                buildRecordValue(operation),
                operation.getOperationTime().toEpochMilli());
    }

    private Struct buildRecordKey(Operation operation) {
        // Key Schema
        Struct key = new Struct(KEY_SCHEMA)
                .put(ACCOUNT_ID, config.getAccountId())
                .put(OPERATION_ID, operation.getId());

        return key;
    }

    private Struct buildRecordValue(Operation operation) {
        // Issue top level fields
        Struct valueStruct =
                new Struct(VALUE_SCHEMA).put(OPERATION_TIME, operation.getOperationTime().getEpochSecond())
                        .put(OPERATION_DESCRIPTION, operation.getDescription())
                        .put(OPERATION_MCC, operation.getMcc())
                        .put(OPERATION_ORIGINAL_MCC, operation.getOriginalMcc())
                        .put(OPERATION_AMOUNT, operation.getAmount())
                        .put(OPERATION_CURRENCY_CODE, operation.getCurrencyCode())
                        .put(OPERATION_COMISSION_RATE, operation.getCommissionRate())
                        .put(OPERATION_CASHBACK_AMOUNT, operation.getCashbackAmount())
                        .put(BALANCE_AFTER_OPERATION, operation.getBalance())
                        .put(OPERATION_HOLD, operation.getHold());

        return valueStruct;
    }

    private Map<String, String> sourcePartition() {
        Map<String, String> map = new HashMap<>();
        map.put(ACCOUNT_ID, config.getAccountId());
        return map;
    }

    private Map<String, String> sourceOffset(Instant updatedAt) {
        Map<String, String> map = new HashMap<>();
        map.put(OPERATION_TIME, DateUtil.maxInstant(updatedAt, from).toString());
        map.put(TO, to.toString());
        return map;
    }
}
