package engine;
import java.util.List;

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