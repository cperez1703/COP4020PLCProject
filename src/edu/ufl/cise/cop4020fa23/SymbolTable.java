package edu.ufl.cise.cop4020fa23;

import edu.ufl.cise.cop4020fa23.ast.NameDef;
import edu.ufl.cise.cop4020fa23.exceptions.TypeCheckException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.jar.Attributes;

public class SymbolTable {
    int current_Num;
    int next_Num;
    Stack<Integer> scope_stack = new Stack<>();

    public SymbolTable() {
        Map<String, NameDef> symbolTable = new HashMap<>();
    }
    public void enterScope() {
        current_Num = next_Num++;
        scope_stack.push(current_Num);
    }
    public void exitScope() {
        current_Num = scope_stack.pop();
    }

    public void insertName(NameDef nameDef) throws TypeCheckException {

    }

    public NameDef lookup(NameDef name) {
            return null;
    }
//hello
}
