package dev.justinklein.netcupfailoveripswitcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NetcupFailoverIpSwitcherApplication {

  public static void main(String[] args) {
    SpringApplication.run(NetcupFailoverIpSwitcherApplication.class, args);
  }

}
