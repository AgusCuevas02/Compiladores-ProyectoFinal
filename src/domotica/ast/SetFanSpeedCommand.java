package domotica.ast;

import domotica.runtime.DomoticaRuntime;

public class SetFanSpeedCommand extends Statement {
    private int fanId;
    private int speed;

    // Constructor que recibe el ID y la velocidad
    public SetFanSpeedCommand(int fanId, int speed) {
        this.fanId = fanId;
        this.speed = speed;
    }

    

    @Override
    public void execute(DomoticaRuntime runtime) {
        // Simplemente registra la accion, ya que la animacion es gestionada por ON/OFF.
        runtime.getGUI().log("Estableciendo velocidad del ventilador " + fanId + " a " + speed);
        
        // No se necesita llamar a turnOnFan/turnOffFan, ya que el comando ON/OFF lo hace.
        
        try {
            Thread.sleep(50); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getFanId() {
    return fanId;
    }

    public int getSpeed() {
    return speed;
    }
}