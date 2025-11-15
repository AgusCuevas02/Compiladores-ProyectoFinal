package domotica.devices;

public class Light implements Device {
    private int id;
    private boolean isOn;
    private String color;
    private int brightness;

    // CORREGIDO: Constructor solo con ID
    public Light(int id) {
        this.id = id;
        this.isOn = false;
        this.color = "#FFFFFF";
        this.brightness = 100;
    }

    @Override
    public int getId() { return id; }

    @Override
    public String getType() { return "LIGHT"; }

    @Override
    public void turnOn() { 
        this.isOn = true; 
        System.out.println("Luz " + id + " encendida");
    }

    @Override
    public void turnOff() { 
        this.isOn = false; 
        System.out.println("Luz " + id + " apagada");
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof String) {
            this.color = (String) value;
            System.out.println("Luz " + id + " color cambiado a " + color);
        } else if (value instanceof Integer) {
            this.brightness = (Integer) value;
            System.out.println("Luz " + id + " brillo ajustado a " + brightness + "%");
        }
    }

    // Getters
    public boolean isOn() { return isOn; }
    public String getColor() { return color; }
    public int getBrightness() { return brightness; }
}