package translation.rule;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 * Contains grammar of rule
 */
public class Grammar {

    protected String partOfSpeech;
    int number;
    protected String word;
    protected HashMap<String, String> features;

    public Grammar() {
        features = new HashMap<>();
    }

    public Grammar(String grammar) {
        this();
        makeGrammar(grammar);
    }

    private void makeGrammar(String stringGrammar) {
        String[] parts = stringGrammar.split("\\.");
        setPOSAnfNumber(parts[0]);
        if (parts.length>1) {
            String[] newFeatures = parts[1].split("&");
            for (String newFeature : newFeatures) {
                String[] featureParts = newFeature.split(":");
                features.put(featureParts[0], featureParts[1]);
            }
        }
    }

    private void setPOSAnfNumber(String posAndNumber) {
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

    public HashMap<String, String> getFeatures() {
        return features;
    }

    public String getWord() {
        return word;
    }
}
