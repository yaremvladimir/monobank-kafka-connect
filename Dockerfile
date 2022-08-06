FROM confluentinc/cp-kafka-connect:5.4.9

WORKDIR /kafka-connect-source-monobank
COPY config config
COPY target target

VOLUME /kafka-connect-source-monobank/config
VOLUME /kafka-connect-source-monobank/offsets

CMD CLASSPATH="$(find target/ -type f -name '*.jar'| grep '\-package' | tr '\n' ':')" connect-standalone config/worker.properties config/MonoBankSourceConnectorExample.properties