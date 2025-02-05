package dev.justinklein.netcupfailoveripswitcher.uptimechecker;

import dev.justinklein.netcupfailoveripswitcher.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UptimeService {
  private final UptimeCheckConfiguration uptimeCheckConfiguration;

  public boolean isUp(String url) {
    boolean result;
    try {
      result = InetAddress.getByName(url).isReachable(uptimeCheckConfiguration.getTimeout());
    } catch (IOException e) {
      log.error("Could not check uptime for url {}", url, e);
      result = false;
    }

    log.info("Uptime check result for '{}': {}", url, result);

    return result;
  }

  public boolean isInternetReachable() {
    try (var httpClient = HttpClient.newHttpClient()) {
      var request = HttpRequest.newBuilder()
        .uri(URI.create("https://example.com"))
        .GET()
        .build();
      var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      return response.statusCode() == 200;
    } catch (IOException | InterruptedException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  public ServerStatusMap getStatusMap(List<Server> serverList) {
    var statusMap = new ServerStatusMap();

    serverList.forEach(server -> statusMap.add(server, isUp(server.getUrl())));

    return statusMap;
  }
}
