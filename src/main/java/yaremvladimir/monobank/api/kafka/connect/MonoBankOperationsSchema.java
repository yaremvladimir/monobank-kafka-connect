package yaremvladimir.monobank.api.kafka.connect;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

public class MonoBankOperationsSchema {
    public static final String ACCOUNT_ID = "accountId";

    public static final String OPERATION_ID = "id";
    public static final String OPERATION_TIME = "time";
    public static final String OPERATION_DESCRIPTION = "description";
    public static final String OPERATION_MCC = "mcc";
    public static final String OPERATION_ORIGINAL_MCC = "originalMcc";
    public static final String OPERATION_AMOUNT = "operationAmount";
    public static final String OPERATION_CURRENCY_CODE = "currencyCode";
    public static final String OPERATION_COMISSION_RATE = "comissionRate";
    public static final String OPERATION_CASHBACK_AMOUNT = "cashbackAmount";
    public static final String BALANCE_AFTER_OPERATION = "balance";
    public static final String OPERATION_HOLD = "hold";

    public static final String SCHEMA_KEY = "operation_key";
    public static final String SCHEMA_VALUE = "operation";

    public static final String TO = "to";

    // Key Schema
    public static Schema KEY_SCHEMA = SchemaBuilder.struct().name(SCHEMA_KEY).version(1)
            .field(ACCOUNT_ID, Schema.STRING_SCHEMA)
            .field(OPERATION_ID, Schema.STRING_SCHEMA).build();

    // Value Schema
    public static Schema VALUE_SCHEMA = SchemaBuilder.struct().name(SCHEMA_VALUE).version(1)
            .field(OPERATION_TIME, Schema.INT64_SCHEMA)
            .field(OPERATION_DESCRIPTION, Schema.STRING_SCHEMA)
            .field(OPERATION_MCC, Schema.INT32_SCHEMA)
            .field(OPERATION_ORIGINAL_MCC, Schema.INT32_SCHEMA)
            .field(OPERATION_AMOUNT, Schema.INT32_SCHEMA)
            .field(OPERATION_CURRENCY_CODE, Schema.INT32_SCHEMA)
            .field(OPERATION_COMISSION_RATE, Schema.INT32_SCHEMA)
            .field(OPERATION_CASHBACK_AMOUNT, Schema.INT32_SCHEMA)
            .field(BALANCE_AFTER_OPERATION, Schema.INT64_SCHEMA)
            .field(OPERATION_HOLD, Schema.BOOLEAN_SCHEMA).build();
}
