package LexerPackage;


public class TwoTuple {

    private String type;
    private String value;


    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TwoTuple(){

    }

    public TwoTuple(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "TwoTuple(" +
                "" + type +
                ", " + value +
                ')';
    }
}
