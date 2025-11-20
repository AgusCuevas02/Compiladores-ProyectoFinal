// src/domotica/devices/Device.java

package domotica.devices;


public interface Device {
    int getId();
    String getType();
    void turnOn();
    void turnOff();
    void setValue(Object value);
}