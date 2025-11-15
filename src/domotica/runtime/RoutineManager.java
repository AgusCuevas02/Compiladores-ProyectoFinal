// src/domotica/runtime/RoutineManager.java
package domotica.runtime;

import java.util.HashMap;
import java.util.Map;

public class RoutineManager {
    private Map<String, Routine> routines;
    
    public RoutineManager() {
        this.routines = new HashMap<>();
    }
    
    public void registerRoutine(String name, Routine routine) {
        routines.put(name, routine);
    }
    
    public Routine getRoutine(String name) {
        return routines.get(name);
    }
    
    public boolean routineExists(String name) {
        return routines.containsKey(name);
    }
}