package domotica.ast;

import domotica.runtime.DomoticaRuntime;
import java.awt.Color;

public class ColorCommand extends Statement {
    private int lightId;
    private String color;
    
    public ColorCommand(int lightId, String color) {
        this.lightId = lightId;
        this.color = color;
    }

   
    
    @Override
    public void execute(DomoticaRuntime runtime) {
        runtime.getGUI().log("Cambiando color de luz " + lightId + " a " + color);
        runtime.getGUI().setLightColor(lightId, color);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

     public int getLightId() {
    return lightId;
    }

    public String getColor() {
        return color;
    }
}