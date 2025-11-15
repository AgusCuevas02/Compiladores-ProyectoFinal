package domotica.ast;

import domotica.runtime.DomoticaRuntime;

public class LightOnCommand extends Statement {
    private int lightId;
    
    public LightOnCommand(int lightId) {
        this.lightId = lightId;
    }

    
    
    @Override
    public void execute(DomoticaRuntime runtime) {
        runtime.getGUI().log("Encendiendo luz " + lightId);
        runtime.getGUI().turnOnLight(lightId);
        
        // Simular delay para la animacion
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getLightId() {
    return lightId;
    }
}