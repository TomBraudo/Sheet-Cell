public class Cell {
    private final String location;
    private Object value;
    public Cell(String location, String value) {
        this.location = location;
        this.value = value;
    }

    public String getLocation() {return location;}
    public Object getValue() {return value;}
    public void setValue(Object value) {this.value = value;}
}
