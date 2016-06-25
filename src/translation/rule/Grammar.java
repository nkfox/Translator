package translation.rule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 * Contains grammar of rule
 */
public class Grammar {

    protected String partOfSpeech;
    protected int number;
    protected String word;
    protected Map<String, Feature> features;

    public Grammar() {
        features = new HashMap<>();
    }

    public Grammar(String partOfSpeech, int number, String word) {
        this();
        this.partOfSpeech = partOfSpeech;
        this.number = number;
        this.word = word;
    }

    public Grammar(String grammar) {
        this();
        makeGrammar(grammar);
    }

    private void makeGrammar(String stringGrammar) {
        String[] parts = stringGrammar.split("\\.");
        setPOSAndNumber(parts[0]);
        if (parts.length > 1) {
            String[] newFeatures = parts[1].split("&");
            for (String newFeature : newFeatures) {
                String[] featureParts = newFeature.split(":");
                features.put(featureParts[0], new Feature(featureParts[1]));
            }
        }
    }

    private void setPOSAndNumber(String posAndNumber) {
        StringBuilder pos = new StringBuilder("");
        int i = 0;
        while (posAndNumber.charAt(i) > '9') {
            pos.append(posAndNumber.charAt(i++));
        }
        partOfSpeech = pos.toString();

        Pattern num = Pattern.compile("[\\d]+");
        Matcher m = num.matcher(posAndNumber);
        if (m.find()) {
            number = Integer.valueOf(m.group());
        } else {
            number = 0;
        }
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public int getNumber() {
        return number;
    }

    public Map<String, Feature> getFeatures() {
        return features;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public Grammar clone() {
        Grammar cloned = new Grammar(partOfSpeech, number, word);
        Set<String> featureNames = features.keySet();
        for (String featureName : featureNames) {
            Feature feature = this.features.get(featureName);
            cloned.features.put(featureName, feature.clone());
        }
        return cloned;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append(partOfSpeech);
        s.append(number);
        if (word != null)
            s.append(".").append(word);
        return s.toString();
    }
}
