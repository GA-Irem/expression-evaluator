FROM openjdk:21-jdk
ARG JAR_FILE
ARG COMMIT=1
ARG BUILD_DATE=2023-11-20
ARG APP_VERSION=0.1
EXPOSE 8080

WORKDIR /usr/src/app

COPY target/expression-evaluator-0.0.1-SNAPSHOT.jar .

LABEL \
    org.opencontainers.image.title=expressionEvaluatorAPI \
    org.opencontainers.image.created=$BUILD_DATE \
    org.opencontainers.image.revision=$COMMIT \
    org.opencontainers.image.vendor=eaetirk \
    org.opencontainers.image.version=$APP_VERSION
ADD --chown=122937 target/expression-evaluator-0.0.1-SNAPSHOT.jar expression-evaluator-0.0.1-SNAPSHOT.jar
CMD java  -jar expression-evaluator-0.0.1-SNAPSHOT.jar