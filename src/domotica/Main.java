package domotica;

import domotica.compiler.Parser;
import domotica.gui.DomoticaGUI;
import domotica.runtime.DomoticaRuntime;
import javax.swing.SwingUtilities;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import domotica.ast.ASTNode;

public class Main {

    public static void main(String[] args) {
        // 1. Inicia la GUI del compilador
        DomoticaGUI gui = new DomoticaGUI();
        
        // 2. Inicia el Runtime
        DomoticaRuntime runtime = new DomoticaRuntime(gui);

        // 3. Conectar el compilador
        gui.setOnCompileRequest(codigoFuente -> {
            compilarYEjecutar(codigoFuente, runtime, gui);
        });
    }

    private static void compilarYEjecutar(String codigoFuente, DomoticaRuntime runtime, DomoticaGUI gui) {
        try (InputStream stream = new ByteArrayInputStream(codigoFuente.getBytes(StandardCharsets.UTF_8))) {
            
            // Fase 1: Analisis lexico y sintactico
            domotica.compiler.Scanner scanner = new domotica.compiler.Scanner(stream);
            domotica.compiler.Parser parser = new domotica.compiler.Parser(scanner);
            
            parser.Parse();

            if (parser.getErrorCount() == 0) {
                // Fase 2: Obtener AST
                java.util.List<domotica.ast.ASTNode> ast = parser.getAST();
                
                // Mostrar AST REAL en la GUI
                String astReal = generarASTReal(ast);
                gui.mostrarASTReal(astReal);
                
                // Generar codigo Arduino
                String codigoArduino = generarCodigoArduino(ast);
                gui.mostrarCodigoObjeto(codigoArduino);
                
                gui.compilacionExitosa(ast.size());
                
                // Fase 3: Ejecucion (opcional)
                gui.log("Iniciando ejecucion del AST...");
                runtime.execute(ast);
                
            } else {
                gui.compilacionFallida(parser.getErrorCount());
            }
        } catch (Exception e) {
            gui.mostrarError("Error durante la compilacion: " + e.getMessage());
        }
    }

    private static String generarASTReal(java.util.List<domotica.ast.ASTNode> ast) {
        StringBuilder sb = new StringBuilder();
        sb.append("PROGRAMA\n");
        sb.append("└── SECUENCIA\n");
        
        for (int i = 0; i < ast.size(); i++) {
            ASTNode node = ast.get(i);
            String nodoStr = node.getClass().getSimpleName();
            
            // Determinar el prefijo de la linea
            String prefix = (i < ast.size() - 1) ? "├── " : "└── ";
            sb.append("    ").append(prefix).append(nodoStr).append("\n");
            
            // Agregar detalles especificos de cada nodo
            if (node instanceof domotica.ast.LightOnCommand) {
                int id = ((domotica.ast.LightOnCommand)node).getLightId();
                sb.append("    │   └── ID: ").append(id).append("\n");
            } else if (node instanceof domotica.ast.LightOffCommand) {
                int id = ((domotica.ast.LightOffCommand)node).getLightId();
                sb.append("    │   └── ID: ").append(id).append("\n");
            } else if (node instanceof domotica.ast.FanOnCommand) {
                int id = ((domotica.ast.FanOnCommand)node).getFanId();
                sb.append("    │   └── ID: ").append(id).append("\n");
            } else if (node instanceof domotica.ast.FanOffCommand) {
                int id = ((domotica.ast.FanOffCommand)node).getFanId();
                sb.append("    │   └── ID: ").append(id).append("\n");
            } else if (node instanceof domotica.ast.SetFanSpeedCommand) {
                int id = ((domotica.ast.SetFanSpeedCommand)node).getFanId();
                int speed = ((domotica.ast.SetFanSpeedCommand)node).getSpeed();
                sb.append("    │   └── ID: ").append(id).append(", Velocidad: ").append(speed).append("\n");
            } else if (node instanceof domotica.ast.ColorCommand) {
                int id = ((domotica.ast.ColorCommand)node).getLightId();
                String color = ((domotica.ast.ColorCommand)node).getColor();
                sb.append("    │   └── ID: ").append(id).append(", Color: ").append(color).append("\n");
            } else if (node instanceof domotica.ast.WaitCommand) {
                int duration = ((domotica.ast.WaitCommand)node).getDuration();
                String unit = ((domotica.ast.WaitCommand)node).getUnit();
                sb.append("    │   └── Duracion: ").append(duration).append(" ").append(unit).append("\n");
            } else if (node instanceof domotica.ast.LogCommand) {
                String message = ((domotica.ast.LogCommand)node).getMessage();
                sb.append("    │   └── Mensaje: \"").append(message).append("\"\n");
            }
        }
        
        return sb.toString();
    }

    private static String generarCodigoArduino(java.util.List<domotica.ast.ASTNode> ast) {
        StringBuilder sb = new StringBuilder();
        sb.append("// CODIGO ARDUINO GENERADO AUTOMATICAMENTE\n");
        sb.append("// Compilador Domotico - Trabajo Practico\n\n");
        
        sb.append("void setup() {\n");
        sb.append("    // Configurar pines de luces\n");
        sb.append("    pinMode(2, OUTPUT);  // Luz 1\n");
        sb.append("    pinMode(3, OUTPUT);  // Luz 2\n");
        sb.append("    pinMode(4, OUTPUT);  // Luz 3\n");
        sb.append("    // Configurar pines de ventiladores\n");
        sb.append("    pinMode(5, OUTPUT);  // Ventilador 1\n");
        sb.append("    pinMode(6, OUTPUT);  // Ventilador 2\n");
        sb.append("    // Inicializar comunicacion serial\n");
        sb.append("    Serial.begin(9600);\n");
        sb.append("}\n\n");
        
        sb.append("void loop() {\n");
        
        for (ASTNode node : ast) {
            if (node instanceof domotica.ast.LightOnCommand) {
                int id = ((domotica.ast.LightOnCommand)node).getLightId();
                int pin = id + 1; // Luz 1 -> pin 2, Luz 2 -> pin 3, etc.
                sb.append("    digitalWrite(").append(pin).append(", HIGH);  // Encender Luz ").append(id).append("\n");
            } else if (node instanceof domotica.ast.LightOffCommand) {
                int id = ((domotica.ast.LightOffCommand)node).getLightId();
                int pin = id + 1;
                sb.append("    digitalWrite(").append(pin).append(", LOW);   // Apagar Luz ").append(id).append("\n");
            } else if (node instanceof domotica.ast.FanOnCommand) {
                int id = ((domotica.ast.FanOnCommand)node).getFanId();
                int pin = id + 4; // Ventilador 1 -> pin 5, Ventilador 2 -> pin 6
                sb.append("    digitalWrite(").append(pin).append(", HIGH);  // Encender Ventilador ").append(id).append("\n");
            } else if (node instanceof domotica.ast.FanOffCommand) {
                int id = ((domotica.ast.FanOffCommand)node).getFanId();
                int pin = id + 4;
                sb.append("    digitalWrite(").append(pin).append(", LOW);   // Apagar Ventilador ").append(id).append("\n");
            } else if (node instanceof domotica.ast.SetFanSpeedCommand) {
                int id = ((domotica.ast.SetFanSpeedCommand)node).getFanId();
                int speed = ((domotica.ast.SetFanSpeedCommand)node).getSpeed();
                // En Arduino, podriamos usar PWM para controlar la velocidad
                int pin = id + 4;
                sb.append("    analogWrite(").append(pin).append(", ").append(speed * 51).append(");  // Velocidad Ventilador ").append(id).append(" a ").append(speed).append("/5\n");
            } else if (node instanceof domotica.ast.ColorCommand) {
                // Para colores necesitariamos LED RGB, asi que lo simulamos
                int id = ((domotica.ast.ColorCommand)node).getLightId();
                String color = ((domotica.ast.ColorCommand)node).getColor();
                sb.append("    // Cambiar color Luz ").append(id).append(" a ").append(color).append(" (requiere LED RGB)\n");
            } else if (node instanceof domotica.ast.WaitCommand) {
                int duration = ((domotica.ast.WaitCommand)node).getDuration();
                String unit = ((domotica.ast.WaitCommand)node).getUnit();
                int milisegundos = duration * 1000;
                if ("MINUTES".equals(unit)) {
                    milisegundos = duration * 60 * 1000;
                } else if ("HOURS".equals(unit)) {
                    milisegundos = duration * 60 * 60 * 1000;
                }
                sb.append("    delay(").append(milisegundos).append(");  // Esperar ").append(duration).append(" ").append(unit).append("\n");
            } else if (node instanceof domotica.ast.LogCommand) {
                String message = ((domotica.ast.LogCommand)node).getMessage();
                sb.append("    Serial.println(\"").append(message).append("\");  // Log\n");
            }
        }
        
        sb.append("}\n");
        return sb.toString();
    }
}