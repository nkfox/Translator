package translation.tree;

import translation.rule.Feature;
import translation.rule.Grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Nataliia Kozoriz on 23.02.2016.
 * Grammar of translated tree
 */
public class TranslateGrammar extends translation.rule.Grammar {

    String englishWord;
    List<TranslateGrammar> leftChildren;
    List<TranslateGrammar> rightChildren;

    public TranslateGrammar(String word) {
        leftChildren = new ArrayList<>();
        rightChildren = new ArrayList<>();
        this.englishWord = word;
    }

    public TranslateGrammar(String word, boolean b) {
        leftChildren = new ArrayList<>();
        rightChildren = new ArrayList<>();
        this.englishWord = word;
        partOfSpeech = "C";
        this.word = "пользователь";

        Feature f = new Feature("числ");
        f.setValue("ед");
        features.put("числ", f);

        f = new Feature("пад");
        f.setValue("им");
        features.put("пад", f);

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

    public String getEnglishWord() {
        return englishWord;
    }

    public String getPartOfSpeech() {
        return super.getPartOfSpeech();
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
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append(super.toString());
        if (englishWord != null)
            s.append(".").append(englishWord);
        return s.toString();
    }
}
