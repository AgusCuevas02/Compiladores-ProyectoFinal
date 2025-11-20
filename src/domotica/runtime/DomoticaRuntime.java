package domotica.runtime;

import domotica.ast.ASTNode;
import domotica.gui.DomoticaGUI;
import java.util.List;

public class DomoticaRuntime {
    private DomoticaGUI gui;
    private boolean isRunning;

    // DeviceManager para manejar estado de dispositivos
    private DeviceManager deviceManager;
    
    public DomoticaRuntime(DomoticaGUI gui) {
        this.gui = gui;
        this.isRunning = false;
        this.deviceManager = new DeviceManager(gui);
    }
    
    public void execute(List<ASTNode> ast) {
        if (isRunning) {
            gui.log(" Ya hay una ejecucion en curso");
            return;
        }
        
        isRunning = true;
        
        new Thread(() -> {
            try {
                gui.log(" Iniciando ejecucion de rutina...");
                
                for (ASTNode node : ast) {
                    if (!isRunning) break;
                    node.execute(this);
                }
                
                gui.log(" Rutina ejecutada completamente");
                
            } catch (Exception e) {
                gui.log("Error durante la ejecucion: " + e.getMessage());
            } finally {
                isRunning = false;
            }
        }).start();
    }
    
    public void stop() {
        isRunning = false;
        gui.log(" Ejecucion detenida por el usuario");
    }
    
    public DomoticaGUI getGUI() {
        return gui;
    }
    
    public boolean isRunning() {
        return isRunning;
    }

    // Nuevo: exponer DeviceManager a los comandos AST
    public DeviceManager getDeviceManager() {
        return deviceManager;
    }
}
