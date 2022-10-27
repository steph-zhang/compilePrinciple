package main;

import java.io.IOException;
import java.util.ArrayList;

import static main.utils.writeTxt;

public class MiddleCodeTable {
    private ArrayList<FourTuple> table;

    public MiddleCodeTable() {
        this.table = new ArrayList<FourTuple>();
    }

    public boolean add(String op, String arg1, String arg2, String result) {
        this.table.add(new FourTuple(op, arg1, arg2, result));
        return true;
    }

    public int NXQ() {
        return this.table.size();
    }

    public boolean backPatch(int index, String result) {
        if(index < 0 || index > this.table.size() - 1) return false;

        this.table.get(index).setResult(result);
        return true;
    }

    public void clear() {
        this.table.clear();
    }

    public void dump() throws Exception {
        for(int i = 0; i < this.table.size(); i ++) {
            writeTxt("resource/semantic_output.txt", "(" + i + ")" + this.table.get(i).toString());
        }
    }

}
