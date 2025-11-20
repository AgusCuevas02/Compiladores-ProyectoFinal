package domotica.ast;

import domotica.runtime.DomoticaRuntime;

public class FanOffCommand extends Statement {
    private int fanId;

    // Constructor que recibe el ID del ventilador
    public FanOffCommand(int fanId) {
        this.fanId = fanId;
    }

    @Override
    public void execute(DomoticaRuntime runtime) {
        // Llama a la GUI para APAGAR la animacion del ventilador
        runtime.getGUI().log("Apagando ventilador " + fanId);
        runtime.getGUI().turnOffFan(fanId);
        
     
        // runtime.getDeviceManager().setFanState(fanId, false);
        
        try {
            // Se usa un retraso para evitar colapsar la GUI
            Thread.sleep(50); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getFanId() {
        return fanId;
    }
}