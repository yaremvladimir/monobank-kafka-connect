#!/usr/bin/env bash
export CLASSPATH="$(find target/ -type f -name '*.jar'| grep '\-package' | tr '\n' ':')"
docker build . -t yaremvladimir/kafka-connect-source-monobank:0.0.1
docker run --net=host --rm -t \
       -v "$(pwd)/offsets:/kafka-connect-source-monobank/offsets" \
       yaremvladimir/kafka-connect-source-monobank:0.0.1