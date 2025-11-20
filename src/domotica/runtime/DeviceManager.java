package domotica.runtime;

import domotica.gui.DomoticaGUI;
import domotica.devices.Device;
import domotica.devices.Light;
import domotica.devices.Fan;
import java.util.HashMap;
import java.util.Map;

public class DeviceManager {
    private DomoticaGUI gui;
    private Map<Integer, Device> devices;

    // Mapa separado para termostatos indexado por roomId (1..n)
    private Map<Integer, domotica.devices.Thermostat> thermostats;
    
    public DeviceManager(DomoticaGUI gui) {
        this.gui = gui;
        this.devices = new HashMap<>();
        this.thermostats = new HashMap<>();
        initializeDevices();
    }
    
    private void initializeDevices() {
        // Luces
        registerDevice(1, new Light(1));
        registerDevice(2, new Light(2));
        registerDevice(3, new Light(3));
        
        // Ventiladores
        registerDevice(4, new Fan(1));
        registerDevice(5, new Fan(2));

        // Termostato en habitacion 3 (id 3)
        thermostats.put(3, new domotica.devices.Thermostat(3));
    }

    public void registerDevice(int id, Device device) {
        devices.put(id, device);
    }
    
    public Device getDevice(int id) {
        return devices.get(id);
    }
    
    public boolean deviceExists(int id) {
        return devices.containsKey(id);
    }
    
    // Metodos especificos para tipos de dispositivos
    public Light getLight(int id) {
        Device device = getDevice(id);
        return (device instanceof Light) ? (Light) device : null;
    }
    
    public Fan getFan(int id) {
        Device device = getDevice(id);
        return (device instanceof Fan) ? (Fan) device : null;
    }

    // ------ Termostatos ------

    public domotica.devices.Thermostat getThermostat(int roomId) {
        Device d = getDevice(roomId);
        return (d instanceof domotica.devices.Thermostat) ? (domotica.devices.Thermostat) d : null;
    }

    public void registerThermostat(int roomId, domotica.devices.Thermostat t) {
        thermostats.put(roomId, t);
    }

    public void setThermostatTemperature(int roomId, int temperature) {
        domotica.devices.Thermostat t = thermostats.get(roomId);
        if (t != null) t.setTemperature(temperature);
    }
}
