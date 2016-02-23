package translation.tree;

import translation.rule.Grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Nataliia Kozoriz on 23.02.2016.
 */
public class TranslateGrammar extends translation.rule.Grammar {

    String englishWord;
    List<TranslateGrammar> leftChildren;
    List<TranslateGrammar> rightChildren;

    TranslateGrammar(String word) {
        leftChildren = new ArrayList<>();
        rightChildren = new ArrayList<>();
        this.englishWord = word;
    }

    public void update(Grammar grammar) {
        this.partOfSpeech = grammar.getPartOfSpeech();
        if (grammar.getWord() != null)
            this.word = grammar.getWord();
        Set<String> featureNames = grammar.getFeatures().keySet();
        for (String featureName: featureNames){
            //String feature = this.features.get(featureName);
            //pad:X
            this.features.put(featureName,grammar.getFeatures().get(featureName));
        }
    }

    public void print(){
        leftChildren.forEach(translation.tree.TranslateGrammar::print);
        System.out.println(englishWord+"."+partOfSpeech);
        Set<String> featuresNames = features.keySet();
        for (String f: featuresNames){
            System.out.println(f +"."+features.get(f));
        }
        System.out.println();
        rightChildren.forEach(translation.tree.TranslateGrammar::print);
    }
}
