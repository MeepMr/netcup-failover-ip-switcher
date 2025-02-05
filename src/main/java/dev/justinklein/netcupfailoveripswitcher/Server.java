package dev.justinklein.netcupfailoveripswitcher;

import lombok.Data;

@Data
public class Server {
  private String name;
  private String url;
  private String macAddress;
}
