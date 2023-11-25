package edu.ufl.cise.cop4020fa23;

import edu.ufl.cise.cop4020fa23.ast.NameDef;
import edu.ufl.cise.cop4020fa23.exceptions.TypeCheckException;

import java.security.KeyStore;
import java.util.*;
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
//            Integer num = scope_stack.peek();
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
//public class SymbolTable {
//    int current_Num;
//    int next_Num;
//    Stack<Integer> scope_stack = new Stack<>();
//    public class Entry {
//        Integer scope = 0;
//        NameDef nameDef = null;
//        public Entry(Integer s, NameDef n) {
//            scope = s;
//            nameDef = n;
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
//            symbolTable.put(nameDef.getName(),new Entry(current_Num,nameDef));
//        }
//        else{
//            symbolTable.put(nameDef.getName(),new Entry(current_Num,nameDef));
//        }
//    }
//    public NameDef lookup(String name) throws TypeCheckException {
//        if(symbolTable.containsKey(name)){
//            Integer num = scope_stack.peek();
//            Entry cur = symbolTable.get(name);
//            while(scope_stack!=null) {
//                if (Objects.equals(cur.scope, num)) {
//                    return cur.nameDef;
//                }
//                else {
//                    num -= 1;
//                }
//            }
//            if(Objects.equals(cur.scope, num)){
//                return cur.nameDef;
//            }else{
//                System.out.println("Name not in the scope");
//                return null;
//            }
//        }else{
//            System.out.println("Name not found");
//            return null;
//        }
//    }
//}
public class SymbolTable {
    int current_Num;
    int next_Num;
    Stack<Integer> scope_stack = new Stack<>();
    private class tableEntry {
        Integer scope = 0;
        NameDef nameDef = null;
        public tableEntry(Integer s, NameDef n) {
            scope = s;
            nameDef = n;
        }
    }

    HashMap<String, tableEntry> symbolTable = new HashMap<>();

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
//        for(Map.Entry<String, Entry> entry : symbolTable.entrySet()) {
//            if (entry.getValue().scope > current_Num) {
//                symbolTable.remove(entry.getKey());
//            }
//        }
//        Iterator<Map.Entry<String, Entry>> iterator = symbolTable.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Entry> entry = iterator.next();
//            if (entry.getValue().scope > current_Num) {
//                symbolTable.remove(entry.getKey());
//            }
//        }
    }

    public void insertName(NameDef nameDef) throws TypeCheckException {
        if(symbolTable.containsKey(nameDef.getName())){
            symbolTable.replace(nameDef.getName(), new tableEntry(current_Num, nameDef));
        }
        else{
            symbolTable.put(nameDef.getName(), new tableEntry(current_Num, nameDef));
        }
    }
    public NameDef lookup(String name) throws TypeCheckException {
        if(symbolTable.containsKey(name)){
            Integer num = scope_stack.peek();
            tableEntry cur = symbolTable.get(name);
            while(scope_stack!=null) {
                if (Objects.equals(cur.scope, num)) {
                    return cur.nameDef;
                }
                else {
                    num -= 1;
                }
            }
            if(Objects.equals(cur.scope, num)){
                return cur.nameDef;
            }else{
                System.out.println("Name not in the scope");
                return null;
            }
        }else{
            System.out.println("Name not found");
            return null;
        }
    }
}