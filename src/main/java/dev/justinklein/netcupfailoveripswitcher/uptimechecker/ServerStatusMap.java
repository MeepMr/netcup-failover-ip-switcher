package dev.justinklein.netcupfailoveripswitcher.uptimechecker;

import dev.justinklein.netcupfailoveripswitcher.Server;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class ServerStatusMap {
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Map<Server, Boolean> serverMap = new HashMap<>();

  public void add(Server server, boolean up) {
    serverMap.put(server, up);
  }

  public boolean areAllUp() {
    return serverMap.values().stream().allMatch(Boolean::booleanValue);
  }

  public List<Server> getReadyServers() {
    return serverMap.keySet().stream()
      .filter(server -> serverMap.get(server))
      .toList();
  }

  public boolean isUp(Server server) {
    return Optional.ofNullable(serverMap.get(server)).orElse(false);
  }
}
