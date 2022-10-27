package LexerPackage;

public class Identifier {
    private String name;
    private String type;
    private String value;
    public Identifier(String name) {
        this.name = name;
        this.type = "";
        this.value = "";
    }

    public Identifier(String name, String value) {
        this.name = name;
        this.type = "";
        this.value = value;
    }

    public Identifier(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
