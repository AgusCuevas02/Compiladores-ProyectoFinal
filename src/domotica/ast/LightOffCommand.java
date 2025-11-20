package domotica.ast;

import domotica.runtime.DomoticaRuntime;

public class LightOffCommand extends Statement {
    private int lightId;
    
    public LightOffCommand(int lightId) {
        this.lightId = lightId;
    }
    public int getLightId() {
    return lightId;
    }


    
    @Override
    public void execute(DomoticaRuntime runtime) {
        runtime.getGUI().log("Apagando luz " + lightId);
        runtime.getGUI().turnOffLight(lightId);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}