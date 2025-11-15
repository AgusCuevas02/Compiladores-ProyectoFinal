package domotica.ast;

import domotica.runtime.DomoticaRuntime;

public class FanOnCommand extends Statement {
    private int fanId;

    // Constructor que recibe el ID del ventilador
    public FanOnCommand(int fanId) {
        this.fanId = fanId;
    }



    @Override
    public void execute(DomoticaRuntime runtime) {
        // Llama a la GUI para ENCENDER la animacion del ventilador
        runtime.getGUI().log("Encendiendo ventilador " + fanId);
        runtime.getGUI().turnOnFan(fanId);
        
     
        // runtime.getDeviceManager().setFanState(fanId, true); 
        
        try {
            // Se usa un pequenio retraso para que el hilo de ejecucion no colapse la GUI con llamadas rapidas
            Thread.sleep(50); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getFanId() {
        return fanId;
    }
}