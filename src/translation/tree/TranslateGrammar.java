package translation.tree;

import translation.rule.Feature;
import translation.rule.Grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        for (TranslateGrammar child : leftChildren)
            cloned.leftChildren.add(child.clone());
        for (TranslateGrammar child : rightChildren)
            cloned.rightChildren.add(child.clone());
        return cloned;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    private String getFeature(String name){
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
        return getFeature("числ");
    }

    public String getNum() {
        return getFeature("зал");
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
}
