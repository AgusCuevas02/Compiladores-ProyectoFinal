package domotica.ast;

import domotica.runtime.DomoticaRuntime;

public class LogCommand extends Statement {
    private String message;
    
    public LogCommand(String message) {
        this.message = message;
    }

    
    
    @Override
    public void execute(DomoticaRuntime runtime) {
        runtime.getGUI().log(message);
        
        // Pequenia pausa para que se vea el log
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public String getMessage() {
    return message;
    }
}