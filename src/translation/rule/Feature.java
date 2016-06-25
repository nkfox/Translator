package translation.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Admin on 12.03.2016.
 * Feature contains value (if exists) and all dependencies
 */
public class Feature {

    String value;
    List<String> dependencies;

    public Feature(String feature) {
        dependencies = new ArrayList<>();
        setValue(feature);
    }

    public final void setValue(String feature) {
        if (feature == null || feature.length() == 0) return;
        if (feature.charAt(0) >= 'A' && feature.charAt(0) <= 'Z' || feature.charAt(0) >= 'a' && feature.charAt(0) <= 'z') {
            dependencies.add(feature);
        } else {
            value = feature;
        }
    }

    public final void setValue(Feature feature) {
        if (feature == null) return;
        setValue(feature.value);
        feature.dependencies.forEach(this::setValue);
    }

    public void print() {
        System.out.print(toString());
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public String getValue() {
        return value;
    }

    public boolean isDependent() {
        for (String dependency : dependencies) {
            for (int i = 0; i < dependency.length(); i++)
                if (dependency.charAt(i) >= 'a' && dependency.charAt(i) <= 'z')
                    return true;
        }
        return false;
    }

    public Map<String, String> getDependencyValues() {
        Map<String, String> values = new HashMap<>();
        for (String dependency : dependencies)
            values.put(dependency, value);
        dependencies.clear();
        return values;
    }

    public void putDependencyValues(Map<String,String> values){
        for(int i=0;i<dependencies.size();i++){
            String dep = dependencies.get(i);
            String val = values.get(dep.toUpperCase());
            if (val!=null){
                value = val;
                dependencies.remove(i);
            } else i++;
        }
    }

    @Override
    public Feature clone() {
        Feature cloned = new Feature(value);
        cloned.dependencies.addAll(dependencies.stream().collect(Collectors.toList()));
        return cloned;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (value != null)
            s.append(".").append(value);
        for (String dep : dependencies) {
            s.append(".").append(dep);
        }
        return s.toString();
    }
}
