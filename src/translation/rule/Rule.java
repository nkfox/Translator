package translation.rule;

import com.sun.istack.internal.NotNull;

import java.util.*;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 * Contains rule
 */
public class Rule implements Comparable<Rule> {

    private RuleNode leftPart;
    private List<List<Grammar>> rightPart; // ...#grammar$grammar
    private String newTag;

    public static int amount=0;
    public static int maxLen=0;
    public static int sum=0;

    public Rule() {
        rightPart = new ArrayList<>();
    }

    public Rule(String rule) {
        this();
        makeRule(rule);
        amount++;
        maxLen=Math.max(maxLen,rule.length());
        sum+=rule.length();
    }

    public int compareTo(@NotNull Rule rule){
        return this.getLeftPart().compare(rule.getLeftPart());
    }

    private void makeRule(String rule) {
        String[] parts = rule.split("#");
        makeLeftPart(parts[0]);
        makeRightPart(parts[1]);
    }

    private void makeLeftPart(String left) {
        leftPart = new RuleNode(left);
    }

    private void makeRightPart(String right) {

        String[] parts = right.split("\\$");
        for (String part : parts) {
            List<Grammar> grammars = new ArrayList<>();
            part = splitNewTag(part);
            String[] stringGrammars = part.split(",");
            for (String stringGrammar : stringGrammars) {
                grammars.add(new Grammar(stringGrammar));
            }
            rightPart.add(grammars);
        }
    }

    private String splitNewTag(String part) {
        if (part.charAt(part.length() - 1) == ')') {
            String[] parts = part.split("[()]");
            newTag = parts[1];
            return parts[0];
        } else {
            newTag = null;
            return part;
        }
    }

    public void printLeftPart(RuleNode node) {
        if (node.getLink() != null) {
            System.out.print(node.getLink() + ".");
        }
        System.out.print(node.getTag() + node.getNumber());
        if (node.getWord()!=null){
            System.out.print( "." + node.getWord());
        }
        if (node.getChildren().size()>0) {
            System.out.print("(");
            int i=0;
            for (RuleNode child : node.getChildren()) {
                printLeftPart(child);
                if (i<node.getChildren().size()-1)
                    System.out.print(",");
                i++;
            }
            System.out.print(")");
        }
    }

    public void printRightPart() {
        System.out.println("New tag " + newTag);
        for (List<Grammar> grammarList : rightPart) {
            System.out.println("New grammarList");
            for (Grammar grammar : grammarList) {
                System.out.println("POS " + grammar.getPartOfSpeech() + "     number " + grammar.getNumber());
                HashMap<String, String> features = grammar.getFeatures();
                Set<String> keys = features.keySet();
                for (String key : keys) {
                    System.out.println("Key " + key + "     Value " + features.get(key));
                }

            }
            System.out.println();
        }
        /*
         System.out.println("New tag " + newTag);
        for (List<Grammar> grammarList : rightPart) {
            System.out.println("New grammarList");
            for (Grammar grammar : grammarList) {
                System.out.println("POS " + grammar.getPartOfSpeech() + "     number " + grammar.getNumber());
                HashMap<String, String> features = grammar.getFeatures();
                Set<String> keys = features.keySet();
                for (String key : keys) {
                    System.out.println("Key " + key + "     Value " + features.get(key));
                }

            }
            System.out.println();
        }
         */
    }

    public RuleNode getLeftPart() {
        return leftPart;
    }

    public List<List<Grammar>> getRightPart() {
        return rightPart;
    }

}
