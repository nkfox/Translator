package translation.tree;

import translation.rule.Feature;
import translation.rule.Grammar;
import translation.database.Dictionary;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Nataliia Kozoriz on 23.02.2016.
 * Grammar of translated tree
 */
public class TranslateGrammar extends translation.rule.Grammar {

    String englishWord;
    List<TranslateGrammar> leftChildren;
    List<TranslateGrammar> rightChildren;

    private static Dictionary dictionary = new Dictionary();

    public TranslateGrammar(String word) {
        leftChildren = new ArrayList<>();
        rightChildren = new ArrayList<>();
        this.englishWord = word;
    }

    public TranslateGrammar(String word, int i) {
        leftChildren = new ArrayList<>();
        rightChildren = new ArrayList<>();
        this.englishWord = word;

        if (i==1) {
            partOfSpeech = "С";

            Feature f = new Feature("числ");
            f.setValue("ед");
            features.put("числ", f);

            f = new Feature("пад");
            f.setValue("им");
            features.put("пад", f);
        } else {
            partOfSpeech = "Г";

            Feature f = new Feature("числ");
            f.setValue("ед");
            features.put("числ", f);

            f = new Feature("род");
            f.setValue("мр");
            features.put("род", f);

            f = new Feature("зал");
            f.setValue("дст");
            features.put("зал", f);

            f = new Feature("вр");
            f.setValue("прш");
            features.put("вр", f);
        }

    }

    public void update(Grammar grammar) {
        this.partOfSpeech = grammar.getPartOfSpeech();
        if (grammar.getWord() != null)
            this.word = grammar.getWord();
        Set<String> featureNames = grammar.getFeatures().keySet();
        for (String featureName : featureNames) {
            Feature feature = this.features.get(featureName);
            if (feature != null)
                feature.setValue(grammar.getFeatures().get(featureName));
            else
                this.features.put(featureName, grammar.getFeatures().get(featureName));
        }
    }

    public void print() {
        leftChildren.forEach(translation.tree.TranslateGrammar::print);
        System.out.println(englishWord + " " + partOfSpeech);
        Set<String> featuresNames = features.keySet();
        for (String f : featuresNames) {
            System.out.print(f);
            features.get(f).print();
            System.out.println();
        }
        System.out.println();
        rightChildren.forEach(translation.tree.TranslateGrammar::print);
    }

    public void getList(List<TranslateGrammar> list) {
        for (TranslateGrammar grammar : leftChildren) {
            grammar.getList(list);
        }
        list.add(this.shallowClone());
        for (TranslateGrammar grammar : rightChildren) {
            grammar.getList(list);
        }
    }

    public boolean isTranslated() {
        return word != null;
    }

    public List<TranslateGrammar> translate() {
        return dictionary.translate(this);
    }

    public boolean canBeTranslated(){
        for (Map.Entry<String, Feature> entry : features.entrySet()) {
            Feature feature = entry.getValue();
            if (feature.isDependent())
                return false;
        }
        return true;
    }

    Map<String, String> getDependencies(){
        Map<String, String> dependencies = new HashMap<>();
        for (Map.Entry<String, Feature> entry : features.entrySet()) {
            Feature feature = entry.getValue();
            Map<String, String> dep = feature.getDependencyValues();
            for (Map.Entry<String, String> values : dep.entrySet()) {
                dependencies.put(values.getKey(),values.getValue());
            }
        }
        return dependencies;
    }

    void putDependencies(Map<String, String> dependencies){
        for (Map.Entry<String, Feature> entry : features.entrySet()) {
            Feature feature = entry.getValue();
            feature.putDependencyValues(dependencies);
        }
    }

    @Override
    public TranslateGrammar clone() {
        TranslateGrammar cloned = new TranslateGrammar(englishWord);
        cloned.number = this.number;
        cloned.partOfSpeech = this.partOfSpeech;
        cloned.word = this.word;
        Set<String> featureNames = features.keySet();
        for (String featureName : featureNames) {
            Feature feature = this.features.get(featureName);
            cloned.features.put(featureName, feature.clone());
        }
        cloned.leftChildren.addAll(leftChildren.stream().map(TranslateGrammar::clone).collect(Collectors.toList()));
        cloned.rightChildren.addAll(rightChildren.stream().map(TranslateGrammar::clone).collect(Collectors.toList()));
        return cloned;
    }

    public TranslateGrammar shallowClone() {
        TranslateGrammar cloned = new TranslateGrammar(englishWord);
        cloned.number = this.number;
        cloned.partOfSpeech = this.partOfSpeech;
        cloned.word = this.word;
        Set<String> featureNames = features.keySet();
        for (String featureName : featureNames) {
            Feature feature = this.features.get(featureName);
            cloned.features.put(featureName, feature.clone());
        }
        return cloned;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public String getPartOfSpeech() {
        return super.getPartOfSpeech().toLowerCase();
    }

    private String getFeature(String name) {
        Feature feature = features.get(name);
        if (feature == null)
            return null;
        return feature.getValue();
    }

    public String getVoice() {
        return getFeature("зал");
    }

    public String getTense() {
        return getFeature("вр");
    }

    public String getPerson() {
        return getFeature("лиц");
    }

    public String getNum() {
        if (getFeature("числ") == null)
            return "ед";
        return getFeature("числ");

    }

    public String getGender() {
        return getFeature("род");
    }

    public String getCase() {
        return getFeature("пад");
    }

    public String getAspect() {
        return getFeature("асп");
    }

    public String getAnimacy() {
        return getFeature("од");
    }

    public String getMood() {
        return getFeature("накл");
    }

    public String getComparison() {
        return getFeature("сс");
    }

    public void setGender(String name) {
        Feature feature = features.get("род");
        if (feature != null)
            feature.setValue(name);
        else {
            feature = new Feature("род");
            feature.setValue(name);
            features.put("род", feature);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(super.toString());
        if (englishWord != null)
            s.append(".").append(englishWord);
        return s.toString();
    }
}
