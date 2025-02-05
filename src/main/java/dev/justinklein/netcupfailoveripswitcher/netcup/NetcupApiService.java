package dev.justinklein.netcupfailoveripswitcher.netcup;

import dev.justinklein.netcupfailoveripswitcher.Server;
import dev.justinklein.netcupfailoveripswitcher.uptimechecker.UptimeCheckConfiguration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NetcupApiService {
  private final NetcupConfiguration netcupConfiguration;
  private final UptimeCheckConfiguration uptimeCheckConfiguration;

  public void switchToServer(
    @NonNull Server server
  ) {
    sendSoapRequest(getSwitchToServerRequest(server));
  }

  @NonNull
  public Optional<Server> getCurrentlyMappedServer() {
    return uptimeCheckConfiguration.getServers().stream()
      .filter(this::hasFailoverIp)
      .findFirst();
  }

  private boolean hasFailoverIp(Server server) {
    return sendSoapRequest(getIpsForServerRequest(server)).body().contains(netcupConfiguration.getFailoverIp());
  }

  @NonNull
  private HttpResponse<String> sendSoapRequest(HttpRequest request) {
    try (var httpClient = HttpClient.newHttpClient()) {
      return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException | InterruptedException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  private HttpRequest getIpsForServerRequest(
    @NonNull Server server
  ) {
    return HttpRequest.newBuilder()
      .uri(URI.create("https://www.servercontrolpanel.de:443/WSEndUser?xsd=1"))
      .POST(HttpRequest.BodyPublishers.ofString("""
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns1="http://enduser.service.web.vcp.netcup.de/">
            <SOAP-ENV:Body>
                <ns1:getVServerIPs>
                    <loginName>%s</loginName>
                    <password>%s</password>
                    <vserverName>%s</vserverName>
                </ns1:getVServerIPs>
            </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
        """.formatted(
        netcupConfiguration.getLoginName(),
        netcupConfiguration.getWebservicePassword(),
        server.getName()
      )))
      .build();
  }

  @NonNull
  private HttpRequest getSwitchToServerRequest(
    @NonNull Server server
  ) {
    return HttpRequest.newBuilder()
      .uri(URI.create("https://www.servercontrolpanel.de:443/WSEndUser?xsd=1"))
      .POST(HttpRequest.BodyPublishers.ofString("""
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns1="http://enduser.service.web.vcp.netcup.de/">
            <SOAP-ENV:Body>
                <ns1:changeIPRouting>
                    <loginName>%s</loginName>
                    <password>%s</password>
                    <routedIP>%s</routedIP>
                    <routedMask>32</routedMask>
                    <destinationVserverName>%s</destinationVserverName>
                    <destinationInterfaceMAC>%s</destinationInterfaceMAC>
                </ns1:changeIPRouting>
            </SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
        """.formatted(
        netcupConfiguration.getLoginName(),
        netcupConfiguration.getWebservicePassword(),
        netcupConfiguration.getFailoverIp(),
        server.getName(),
        server.getMacAddress()
      )))
      .build();
  }
}
