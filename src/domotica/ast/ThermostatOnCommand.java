package domotica.ast;

import domotica.runtime.DomoticaRuntime;

public class ThermostatOnCommand extends Statement {
    private int thermostatId;

    public ThermostatOnCommand(int thermostatId) {
        this.thermostatId = thermostatId;
    }

    @Override
    public void execute(DomoticaRuntime runtime) {
        runtime.getGUI().log("Encendiendo termostato " + thermostatId);
        runtime.getGUI().turnOnThermostat(thermostatId);

        if (runtime.getDeviceManager() != null) {
            domotica.devices.Thermostat t = runtime.getDeviceManager().getThermostat(thermostatId);
            if (t != null) t.turnOn();
        }

        try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public int getThermostatId() { return thermostatId; }
}
