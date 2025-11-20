package domotica.ast;

import domotica.runtime.DomoticaRuntime;

public class ThermostatOffCommand extends Statement {
    private int thermostatId;

    public ThermostatOffCommand(int thermostatId) {
        this.thermostatId = thermostatId;
    }

    @Override
    public void execute(DomoticaRuntime runtime) {
        runtime.getGUI().log("Apagando termostato " + thermostatId);
        runtime.getGUI().turnOffThermostat(thermostatId);

        if (runtime.getDeviceManager() != null) {
            domotica.devices.Thermostat t = runtime.getDeviceManager().getThermostat(thermostatId);
            if (t != null) t.turnOff();
        }

        try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public int getThermostatId() { return thermostatId; }
}
