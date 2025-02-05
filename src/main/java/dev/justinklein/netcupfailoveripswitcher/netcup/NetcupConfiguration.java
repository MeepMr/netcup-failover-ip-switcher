package dev.justinklein.netcupfailoveripswitcher.netcup;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("uptime-check.netcup")
public class NetcupConfiguration {
  private String loginName;
  private String webservicePassword;
  private String failoverIp;
}
