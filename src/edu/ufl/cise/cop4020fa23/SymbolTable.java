package edu.ufl.cise.cop4020fa23;

import edu.ufl.cise.cop4020fa23.ast.NameDef;
import edu.ufl.cise.cop4020fa23.exceptions.TypeCheckException;

import java.util.HashMap;
import java.util.Objects;
import java.util.Stack;

//public class SymbolTable {
//    int current_Num;
//    int next_Num;
//    Stack<Integer> scope_stack = new Stack<>();
//    public class Entry {
//        Integer scope = 0;
//        NameDef nameDef = null;
//        Entry entry = null;
//        public Entry(Integer s, NameDef n, Entry e){
//            scope = s;
//            nameDef = n;
//            entry = e;
//        }
//        public void next(Entry e){
//            entry = e;
//        }
//    }
//
//    HashMap<String, Entry> symbolTable = new HashMap<>();
//
//    public SymbolTable() {
//        current_Num = 0;
//        next_Num = 0;
//    }
//    public void enterScope() {
//        current_Num = next_Num++;
//        scope_stack.push(current_Num);
//    }
//    public void exitScope() {
//        current_Num = scope_stack.pop();
//    }
//
//    public void insertName(NameDef nameDef) throws TypeCheckException {
//        if(symbolTable.containsKey(nameDef.getName())){
//            symbolTable.get(nameDef.getName()).next(new Entry(current_Num,nameDef,null));
//        }else{
//            symbolTable.put(nameDef.getName(),new Entry(current_Num,nameDef, null));
//        }
//    }
//    public NameDef lookup(String name) throws TypeCheckException {
//        if(symbolTable.containsKey(name)){
//            Integer num = scope_stack.firstElement();
//            Entry cur = symbolTable.get(name);
//            while(cur.entry!=null) {
//                if (Objects.equals(cur.scope, num)) {
//                    return cur.nameDef;
//                } else {
//                    cur = cur.entry;
//                }
//            }
//            if(Objects.equals(cur.scope, num)){
//                return cur.nameDef;
//            }else{
//                throw new TypeCheckException("Name not found");
//            }
//        }else{
//            throw new TypeCheckException("Name not found");
//        }
//    }
//}

//Sonny's ST for Testing
public class SymbolTable {
    int current_Num;
    int next_Num;
    Stack<Integer> scope_stack = new Stack<>();
    public class Entry {
        Integer scope = 0;
        NameDef nameDef = null;
        edu.ufl.cise.cop4020fa23.SymbolTable.Entry entry = null;
        public Entry(Integer s, NameDef n, edu.ufl.cise.cop4020fa23.SymbolTable.Entry e){
            scope = s;
            nameDef = n;
            entry = e;
        }
        public void next(edu.ufl.cise.cop4020fa23.SymbolTable.Entry e){
            entry = e;
        }
    }

    HashMap<String, edu.ufl.cise.cop4020fa23.SymbolTable.Entry> symbolTable = new HashMap<>();

    public SymbolTable() {
        current_Num = 0;
        next_Num = 0;
    }
    public void enterScope() {
        current_Num = next_Num++;
        scope_stack.push(current_Num);
    }
    public void exitScope() {
        current_Num = scope_stack.pop();
    }

    public void insertName(NameDef nameDef) throws TypeCheckException {
        if(symbolTable.containsKey(nameDef.getName())){
            symbolTable.get(nameDef.getName()).next(new edu.ufl.cise.cop4020fa23.SymbolTable.Entry(next_Num,nameDef,null));
        }else{
            symbolTable.put(nameDef.getName(),new edu.ufl.cise.cop4020fa23.SymbolTable.Entry(next_Num,nameDef, null));
        }
    }
    public NameDef lookup(String name) throws TypeCheckException {
        NameDef nameDef = null;
        if(symbolTable.containsKey(name)){
            Integer num = scope_stack.peek();
            edu.ufl.cise.cop4020fa23.SymbolTable.Entry cur = symbolTable.get(name);
            if (num.equals(cur.scope)) {
                nameDef = cur.nameDef;
            }
            else {
                throw new TypeCheckException("Not in proper scope");
            }
        }
        else {
            throw new TypeCheckException("Name could not be found");
        }
        return nameDef;
    }
}
