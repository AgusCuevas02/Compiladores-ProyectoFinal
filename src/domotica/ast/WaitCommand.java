package domotica.ast;

import domotica.runtime.DomoticaRuntime;

public class WaitCommand extends Statement {
    private int duration;
    private String unit;
    
    public WaitCommand(int duration, String unit) {
        this.duration = duration;
        this.unit = unit;
    }

    
    
    @Override
    public void execute(DomoticaRuntime runtime) {
        int milliseconds = duration * 1000; // Convertir a milisegundos
        
        if ("MINUTES".equals(unit)) {
            milliseconds = duration * 60 * 1000;
        } else if ("HOURS".equals(unit)) {
            milliseconds = duration * 60 * 60 * 1000;
        }
        
        runtime.getGUI().log("Esperando " + duration + " " + unit.toLowerCase());
        
        // Para la demostracion, acortamos las esperas largas
        //if (milliseconds > 5000) {
        //    milliseconds = 2000; // Maximo 2 segundos en demo
        //}
        
        try {
            // Mostrar cuenta regresiva en la GUI
            for (int i = duration; i > 0; i--) {
                runtime.getGUI().updateCountdown(i + " " + unit.toLowerCase());
                Thread.sleep(milliseconds / duration);
            }
            runtime.getGUI().updateCountdown("");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int getDuration() {
    return duration;
    }

    public String getUnit() {
        return unit;
    }
}