package edu.ufl.cise.cop4020fa23;

import edu.ufl.cise.cop4020fa23.ast.NameDef;
import edu.ufl.cise.cop4020fa23.exceptions.TypeCheckException;

import java.security.KeyStore;
import java.util.*;
public class SymbolTable {
    int current_Num;
    int next_Num;
    Stack<Integer> scope_stack = new Stack<>();
    public class Entry {
        Integer scope = 0;
        NameDef nameDef = null;
        Entry entry = null;
        public Entry(Integer s, NameDef n, Entry e){
            scope = s;
            nameDef = n;
            entry = e;
        }
        public void next(Entry e){
            entry = e;
        }
    }

    HashMap<String, Entry> symbolTable = new HashMap<>();

    public SymbolTable() {
        current_Num = 0;
        next_Num = 0;
    }
    public void enterScope() {
        current_Num = next_Num++;
//        System.out.println(current_Num);
        scope_stack.push(current_Num);
    }
    public void exitScope() {
//        System.out.println("exit");
        current_Num = scope_stack.peek();
        scope_stack.pop();
    }

    public void insertName(NameDef nameDef) throws TypeCheckException {
        if(symbolTable.containsKey(nameDef.getName())){
            Entry cur = symbolTable.get(nameDef.getName());
            while(cur.entry!=null){
                if(cur.scope==current_Num)throw new TypeCheckException("Already Exists");
                cur = cur.entry;
            }
            if(cur.scope==current_Num)throw new TypeCheckException("Already Exists");
            cur.entry = new Entry(current_Num,nameDef,null);
        }else{
            symbolTable.put(nameDef.getName(),new Entry(current_Num,nameDef, null));
        }
    }
    public NameDef lookup(String name) throws TypeCheckException {
        if(symbolTable.containsKey(name)){
            Stack <Integer> check = (Stack<Integer>)scope_stack.clone();
            Integer num = current_Num;//may change to check.top
            Entry cur = symbolTable.get(name);
            while(!check.isEmpty()){
                num = check.peek();
                while(cur.entry!=null) {
                    if (Objects.equals(cur.scope, num)) {
                        return cur.nameDef;
                    } else {
                        cur = cur.entry;
                    }
                }
                if(Objects.equals(cur.scope, num)){
                    return cur.nameDef;
                }
                check.pop();
                cur = symbolTable.get(name);
            }
            if(Objects.equals(cur.scope, num)){
                return cur.nameDef;
            }else{
                System.out.println("Name not found");
                return null;
            }
        }else{
            System.out.println("Name not found");
            return null;
        }
    }
}