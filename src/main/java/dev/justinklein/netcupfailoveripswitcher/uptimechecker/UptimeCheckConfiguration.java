package dev.justinklein.netcupfailoveripswitcher.uptimechecker;

import dev.justinklein.netcupfailoveripswitcher.Server;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("uptime-check")
public class UptimeCheckConfiguration {
  private List<Server> servers;
  private int timeout;
}
