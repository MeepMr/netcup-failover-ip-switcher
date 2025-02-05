### STAGE 1: Build ###
FROM gradle:8-jdk21 AS build

WORKDIR /usr/app/
COPY . .
RUN gradle build -PprojVersion=0.0.0

### STAGE 2: Run ###
FROM openjdk:21-jdk

WORKDIR /app

COPY --from=build /usr/app/build/libs/netcup-failover-ip-switcher-0.0.0.jar ./netcup-failover-ip-switcher.jar

ENV UPTIME-CHECK_TIMEOUT=100
ENV UPTIME-CHECK_NETCUP_LOGIN-NAME="<netcup-login-name>"
ENV UPTIME-CHECK_NETCUP_WEBSERVICE-PASSWORD="<netcup-webservice-password>"
ENV UPTIME-CHECK_NETCUP_FAILOVER-IP="<netcup-failover-ip>"

ENV UPTIME-CHECK_SERVERS_0_NAME="<server-1-name>"
ENV UPTIME-CHECK_SERVERS_0_URL="<server-1-url>"
ENV UPTIME-CHECK_SERVERS_0_MAC-ADDRESS="<server-1-mac-address>"

ENV UPTIME-CHECK_SERVERS_1_NAME="<server-2-name>"
ENV UPTIME-CHECK_SERVERS_1_URL="<server-2-url>"
ENV UPTIME-CHECK_SERVERS_1_MAC-ADDRESS="<server-2-mac-address>"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "./netcup-failover-ip-switcher.jar"]