# Kafka Connect Source Monobank

This connector allows you to get a stream of your operations from Monobank, using the api: [https://api.monobank.ua](https://api.monobank.ua/docs/)

Operations are retrieved by batches of 1 month and have colldown period of 10 minutes if retrieving latest data. This is to avoid 429 errors and decrease load on Monobank systems.


# Contributing

This connector is not perfect and can be improved, please feel free to submit any PR you think useful. 

# Configuration

```
name=MonoBankSourceConnectorDemo
tasks.max=1
connector.class=yaremvladimir.monobank.api.kafka.connect.MonoBankSourceConnector
topic=monobank-operations
since.timestamp=2022-01-01T00:00:00Z
# Get it from https://api.monobank.ua
auth.token=your_auth_token
# Get it from https://api.monobank.ua/personal/client-info (supports accounts and jars)
account.id=your_card_account_or_jar_id
```

# Running in development

Note: Java 8 is required for this connector. 
Make sure `config/worker.properties` is configured to wherever your kafka cluster is

```
mvn clean package
./run_with_docker.sh 
```

# Deploying

Java 8 is required for this connector. 

Build with 

```
mvn clean package
```

And after that deploy to Kafka