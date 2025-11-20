package domotica.ast;

import domotica.runtime.DomoticaRuntime;

public class ThermostatSetTemperatureCommand extends Statement {
    private int thermostatId;
    private int temperature;

    public ThermostatSetTemperatureCommand(int thermostatId, int temperature) {
        this.thermostatId = thermostatId;
        this.temperature = temperature;
    }

    @Override
    public void execute(DomoticaRuntime runtime) {
        runtime.getGUI().log("Configurando termostato " + thermostatId + " a " + temperature + "grados");
        runtime.getGUI().setThermostatTemperature(thermostatId, temperature);
        runtime.getGUI().updateThermostatDisplay(thermostatId, String.valueOf(temperature));

        if (runtime.getDeviceManager() != null) {
            domotica.devices.Thermostat t = runtime.getDeviceManager().getThermostat(thermostatId);
            if (t != null) t.setTemperature(temperature);
        }

        try { Thread.sleep(50); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public int getThermostatId() { return thermostatId; }
    public int getTemperature() { return temperature; }
}
