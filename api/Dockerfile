FROM 685321914474.dkr.ecr.eu-west-3.amazonaws.com/orbita/common/java:serverjre-8

RUN yum -y install telnet

RUN mkdir -v -p /var/config/api /var/log/api /app && \
    chmod -R 777 /var/config/api /var/log/api

COPY /build/resources/main/application.yml /var/config/api/application.yml
COPY /build/libs/api*.jar /app/api.jar

ENTRYPOINT exec java $JAVA_OPTS -jar /app/api.jar