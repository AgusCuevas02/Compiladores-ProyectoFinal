// src/domotica/devices/Fan.java
package domotica.devices;

public class Fan implements Device {
    private int id;
    private boolean isOn;
    private int speed; // 0-100

    public Fan(int id) {
        this.id = id;
        this.isOn = false;
        this.speed = 0;
    }

    @Override
    public int getId() { return id; }

    @Override
    public String getType() { return "FAN"; }

    @Override
    public void turnOn() { 
        this.isOn = true; 
        System.out.println("Ventilador " + id + " encendido");
    }

    @Override
    public void turnOff() { 
        this.isOn = false; 
        this.speed = 0;
        System.out.println("Ventilador " + id + " apagado");
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Integer) {
            this.speed = (Integer) value;
            System.out.println("Ventilador " + id + " velocidad ajustada a " + speed + "%");
        }
    }

    // Getters
    public boolean isOn() { return isOn; }
    public int getSpeed() { return speed; }
}