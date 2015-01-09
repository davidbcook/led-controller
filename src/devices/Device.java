package devices;

import java.net.InetAddress;

import discovery.DeviceType;

public interface Device {
  String getMacAddress();

  InetAddress getIp();

  DeviceType getDeviceType();

  int getProtocolVersion();

  int getVendorId();

  int getProductId();

  int getHardwareRevision();

  int getSoftwareRevision();

  long getLinkSpeed();

}