package domotica.devices;

public class Thermostat implements Device {
    private final int id;
    private final String type = "THERMOSTAT";
    private boolean on = false;
    private int temperature = 20; // default

    public Thermostat(int id) {
        this.id = id;

    }

    public int getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void turnOn() {
        this.on = true;
        // agregar logica real de hardware / simulacion
    }

    @Override
    public void turnOff() {
        this.on = false;
    }

    public boolean isOn() {
        return on;
    }

    public void setTemperature(int t) {
        this.temperature = t;
    }

    public int getTemperature() {
        return temperature;
    }

    @Override
    public void setValue(Object value) {
        if (value == null) return;

        // Number -> temperatura
        if (value instanceof Number) {
            int t = ((Number) value).intValue();
            setTemperature(t);
            return;
        }

        // String -> intentar parseo a entero
        if (value instanceof String) {
            String s = ((String) value).trim();
            try {
                int t = Integer.parseInt(s);
                setTemperature(t);
                return;
            } catch (NumberFormatException e) {
                // No es un numero: podria ser "ON"/"OFF"
                String up = s.toUpperCase();
                if (up.equals("ON")) {
                    turnOn();
                    return;
                } else if (up.equals("OFF")) {
                    turnOff();
                    return;
                }
                // No reconocido: ignorar
                return;
            }
        }

        // Boolean -> on/off
        if (value instanceof Boolean) {
            boolean b = (Boolean) value;
            if (b) turnOn();
            else turnOff();
            return;
        }

        // Otros tipos: ignorar por seguridad
    }
    public String status() {
        return (on ? "ON" : "OFF") + " " + temperature;
    }
}
