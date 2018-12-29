FROM openjdk:8-jdk-alpine as builder
WORKDIR /src
ADD . /src
RUN ./gradlew -DskipTests clean bootJar

FROM openjdk:8-jre-alpine
WORKDIR /app
# Inspiration http://xmodulo.com/geographic-location-ip-address-command-line.html
RUN apk --no-cache add geoip && \
  cd /usr/share/GeoIP && \
  wget http://geolite.maxmind.com/download/geoip/database/GeoLiteCountry/GeoIP.dat.gz && \
  gunzip GeoIP.dat.gz

COPY --from=builder /src/build/libs/*-SNAPSHOT.jar .
USER guest
CMD exec java -jar *.jar
