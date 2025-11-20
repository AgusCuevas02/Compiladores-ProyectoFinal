package domotica.runtime;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, Object> variables;
    
    public SymbolTable() {
        this.variables = new HashMap<>();
    }
    
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }
    
    public Object getVariable(String name) {
        return variables.get(name);
    }
    
    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }
}