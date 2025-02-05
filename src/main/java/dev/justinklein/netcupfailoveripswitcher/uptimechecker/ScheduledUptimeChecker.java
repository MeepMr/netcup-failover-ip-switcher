package dev.justinklein.netcupfailoveripswitcher.uptimechecker;

import dev.justinklein.netcupfailoveripswitcher.Server;
import dev.justinklein.netcupfailoveripswitcher.netcup.NetcupApiService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledUptimeChecker {
  private final UptimeCheckConfiguration uptimeCheckConfiguration;
  private final UptimeService uptimeService;
  private final NetcupApiService netcupApiService;

  @Scheduled(fixedRate = 60000)
  public void testConnection() {
    var statusMap = uptimeService.getStatusMap(uptimeCheckConfiguration.getServers());
    var mappedServer = netcupApiService.getCurrentlyMappedServer();

    mappedServer.ifPresent(server -> {
      if (!uptimeService.isInternetReachable()) {
        log.info("Internet is not reachable. Skipping Server-Mapping");
        return;
      }
      if (statusMap.isUp(getPrimaryServer()) && !isPrimaryServer(server)) {
        log.info("Primary server is up, but not active. Mapping to primary server");
        netcupApiService.switchToServer(getPrimaryServer());
        log.info("Switched to Primary Server");
      } else if (!statusMap.areAllUp() && !statusMap.isUp(server)) {
        log.info("Current server is not up. Mapping to next available server");
        var nextServer = statusMap.getReadyServers().getFirst();
        netcupApiService.switchToServer(nextServer);
        log.info("Switched to Server {}", nextServer);
      } else if (isPrimaryServer(server)) {
        log.info("Primary server is up and mapped");
      } else {
        log.info("IP is routing to an available server");
      }
    });
  }

  private boolean isPrimaryServer(Server server) {
    return getPrimaryServer().getName().equals(server.getName());
  }

  @NonNull
  private Server getPrimaryServer() {
    return uptimeCheckConfiguration.getServers().getFirst();
  }
}
