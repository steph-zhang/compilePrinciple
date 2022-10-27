package LexerPackage;
import main.SemanticException;

import java.io.IOException;
import java.util.ArrayList;

import static main.utils.writeTxt;

public class IdentifierTable {
    private ArrayList<Identifier> table;

    public boolean flag = true;

    public IdentifierTable(){
        table = new ArrayList<>();
    }

    public int isExist(String name){
        // -1不存在,否则返回index
        for (int i = 0; i < table.size(); i ++) {
            if(table.get(i).getName().equals(name)) return i;
        }
        return -1;
    }

    public boolean add(String name) throws SemanticException {
        if(!flag) return false;
        // true成功添加
        if(this.isExist(name) != -1) {
            return false;
        }
        Identifier newIdentifier = new Identifier(name);
        table.add(newIdentifier);
        return true;
    }

    public void clear(){
        table.clear();
    }

    public void dump(){
        for(Identifier i : table){
            System.out.println(i.getName() + "\t" + i.getType() + "\t" + i.getValue());
        }
    }

    public boolean updateTypeByName(String name, String type) throws IOException {
        int index = isExist(name);
        if(index != -1){
            table.get(index).setType(type);
            writeTxt("resource/out.txt", "更新标识符" + name + "类型为" + type);
            return true;
        }return false;
    }

    public boolean updateValueByName(String name, String value) throws IOException {
        int index = isExist(name);
        if(index != -1){
            table.get(index).setValue(value);
            writeTxt("resource/out.txt", "更新标识符" + name + "值为" + value);
            return true;
        }return false;
    }

    public String getValueByName(String name){
        int index = isExist(name);
        return table.get(index).getValue();
    }

    public String getTypeByName(String name){
        int index = isExist(name);
        return table.get(index).getType();
    }
}
