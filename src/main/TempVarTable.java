package main;

import LexerPackage.Identifier;

import java.util.ArrayList;

public class TempVarTable {
    private ArrayList<String> table;

    public TempVarTable() {
        this.table = new ArrayList<String>();
    }

    public Identifier getNewTempVar(){
        int index = this.table.size();
        this.table.add("T" + index);
        return new Identifier("T" + index);
    }
}
