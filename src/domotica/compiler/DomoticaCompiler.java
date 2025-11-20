package domotica.compiler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DomoticaCompiler {
    
    public CompilationResult compile(String sourceCode) {
        try {
            System.out.println(" Iniciando compilacion con COCO/R...");
            
            // Usar InputStream para el scanner de COCO/R
            InputStream inputStream = new ByteArrayInputStream(
                sourceCode.getBytes(StandardCharsets.UTF_8)
            );
            
            Scanner scanner = new Scanner(inputStream);
            Parser parser = new Parser(scanner);
            
            // Ejecutar el parsing de COCO/R (para validacin sintctica)
            parser.Parse();
            
            System.out.println(" Analisis sintactico exitoso con COCO/R");
            
            // Para esta demo, vamos a crear un AST manualmente basado en el cdigo
            // En un proyecto real completo, el AST se construira en las acciones semnticas
            List<domotica.ast.ASTNode> ast = createASTFromCode(sourceCode);
            
            System.out.println("AST generado con " + ast.size() + " comandos");
            return new CompilationResult(ast, true, "Compilacin completada exitosamente");
            
        } catch (Exception e) {
            System.err.println("Error durante la compilacn: " + e.getMessage());
            return new CompilationResult(null, false, "Error: " + e.getMessage());
        }
    }
    
    // Crea un AST basado en el cdigo fuente (simulacin)
    private List<domotica.ast.ASTNode> createASTFromCode(String sourceCode) {
        List<domotica.ast.ASTNode> ast = new ArrayList<>();
        String[] lines = sourceCode.split("\n");
        
        for (String line : lines) {
            String trimmedLine = line.trim();
            
            // Saltar lneas vacas y comentarios
            if (trimmedLine.isEmpty() || trimmedLine.startsWith("//")) {
                continue;
            }
            
            // Parsear comandos bsicos
            domotica.ast.ASTNode node = parseLine(trimmedLine);
            if (node != null) {
                ast.add(node);
            }
        }
        
        return ast;
    }
    
    // Parsea una lnea individual y crea el nodo AST correspondiente
    private domotica.ast.ASTNode parseLine(String line) {
        try {
            if (line.startsWith("ON LIGHT")) {
                int lightId = extractNumber(line, "ON LIGHT");
                return new domotica.ast.LightOnCommand(lightId);
            }
            else if (line.startsWith("OFF LIGHT")) {
                int lightId = extractNumber(line, "OFF LIGHT");
                return new domotica.ast.LightOffCommand(lightId);
            }
            else if (line.startsWith("COLOR LIGHT")) {
                // Ejemplo: "COLOR LIGHT 1 TO #FF0000"
                String[] parts = line.split(" ");
                if (parts.length >= 5) {
                    int lightId = Integer.parseInt(parts[2]);
                    String color = parts[4];
                    return new domotica.ast.ColorCommand(lightId, color);
                }
            }
            else if (line.startsWith("WAIT")) {
                // Ejemplo: "WAIT 2 SECONDS"
                String[] parts = line.split(" ");
                if (parts.length >= 3) {
                    int duration = Integer.parseInt(parts[1]);
                    String unit = parts[2];
                    return new domotica.ast.WaitCommand(duration, unit);
                }
            }
            else if (line.contains("LOG")) {
                // Ejemplo: LOG 'mensaje'
                int start = line.indexOf("'");
                int end = line.lastIndexOf("'");
                if (start != -1 && end != -1 && end > start) {
                    String message = line.substring(start + 1, end);
                    return new domotica.ast.LogCommand(message);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error parseando lnea: " + line + " - " + e.getMessage());
        }
        
        return null;
    }
    
    // Extrae un nmero de un comando
    private int extractNumber(String line, String prefix) {
        try {
            String numberStr = line.substring(prefix.length()).trim();
            return Integer.parseInt(numberStr);
        } catch (Exception e) {
            return 1; // Valor por defecto
        }
    }
    
    // Clase para representar el resultado de la compilacin
    public static class CompilationResult {
        private final List<domotica.ast.ASTNode> ast;
        private final boolean success;
        private final String message;
        
        public CompilationResult(List<domotica.ast.ASTNode> ast, boolean success, String message) {
            this.ast = ast;
            this.success = success;
            this.message = message;
        }
        
        // Getters
        public List<domotica.ast.ASTNode> getAST() { return ast; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
}