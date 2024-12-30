package engine;
import java.util.List;

//Class that helps to expose a single cell to the UI layer
public class CellDTO {
    String location;
    String originalValue;
    String effectiveValue;
    List<String> dependentOn;
    List<String> dependents;

    //constructor
    public CellDTO(String location, String originalValue, String effectiveValue, List<String> dependentOn, List<String> dependents) {
        this.location = location;
        this.originalValue = originalValue;
        this.effectiveValue = effectiveValue;
        this.dependentOn = dependentOn;
        this.dependents = dependents;
    }
    public String getLocation() {
        return location;
    }
    public String getOriginalValue() {
        return originalValue;
    }
    public String getEffectiveValue() {
        return effectiveValue;
    }
    public List<String> getDependentOn() {
        return dependentOn;
    }
    public List<String> getDependents() {
        return dependents;
    }
}