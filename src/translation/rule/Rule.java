package translation.rule;

import translation.tree.Grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 */
public class Rule {

    private RuleNode leftPart;
    private List<List<Grammar>> rightPart;
    private String newTag;

    public Rule(String rule) {
        rightPart = new ArrayList<>();
        makeRule(rule);
        //print();
    }

    private void makeRule(String rule) {
        String[] parts = rule.split("#");
        makeLeftPart(parts[0]);
        makeRightPart(parts[1]);
    }

    private void makeLeftPart(String leftPart) {

    }

    private void makeRightPart(String right) {

        String[] parts = right.split("\\$");
        for(String part : parts){
            List<Grammar> grammars = new ArrayList<>();
            part = splitNewTag(part);
            String[] stringGrammars = part.split(",");
            for(String stringGrammar : stringGrammars){
                grammars.add(new Grammar(stringGrammar));
            }
            rightPart.add(grammars);
        }
    }

    private String splitNewTag(String part){
        if (part.charAt(part.length()-1) == ')'){
            String[] parts = part.split("[()]");
            newTag = parts[1];
            return parts[0];
        } else{
            newTag = null;
            return part;
        }
    }

    public void print(){
        System.out.println("New tag "+newTag);
        for(List<Grammar> grammarList: rightPart){
            System.out.println("New grammarList");
            for(Grammar grammar:grammarList){
                System.out.println("POS "+grammar.getPartOfSpeech()+"     number "+grammar.getNumber());
                HashMap<String, String> features = grammar.getFeatures();
                Set<String> keys = features.keySet();
                for(String key: keys){
                    System.out.println("Key "+key+"     Value "+features.get(key));
                }

            }
            System.out.println();
        }
    }
}
