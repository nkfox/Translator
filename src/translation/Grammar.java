package translation;

import java.util.HashMap;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 */
public class Grammar {

    private String partOfSpeech;
    private HashMap<String, String> features;

    public Grammar() {
        partOfSpeech = "";
        features = new HashMap<>();
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public HashMap<String, String> getFeatures() {
        return features;
    }

    public void setFeatures(HashMap<String, String> features) {
        this.features = features;
    }
}
