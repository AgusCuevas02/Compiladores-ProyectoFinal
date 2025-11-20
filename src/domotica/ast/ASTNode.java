package domotica.ast;

import domotica.runtime.DomoticaRuntime;

public abstract class ASTNode {
    public abstract void execute(DomoticaRuntime runtime);
}