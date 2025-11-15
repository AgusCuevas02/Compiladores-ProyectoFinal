// src/domotica/runtime/Routine.java
package domotica.runtime;

import java.util.List;

public class Routine {
    private String name;
    private List<String> parameters;
    private List<Object> body; // Mas adelante sera una lista de nodos AST

    public Routine(String name, List<String> parameters, List<Object> body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    // Getters
    public String getName() { return name; }
    public List<String> getParameters() { return parameters; }
    public List<Object> getBody() { return body; }
}