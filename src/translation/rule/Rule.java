package translation.rule;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 * Contains rule
 */
public class Rule implements Comparable<Rule> {

    RuleNode leftPart;
    List<List<Grammar>> rightPart; // ...#grammar$grammar
    String newTag;

    public static int amount = 0;
    public static int maxLen = 0;
    public static int sum = 0;

    public Rule() {
        rightPart = new ArrayList<>();
    }

    public Rule(String rule) {
        this();
        makeRule(rule);
        amount++;
        maxLen = Math.max(maxLen, rule.length());
        sum += rule.length();
    }

    public int compareTo(@NotNull Rule rule) {
        return this.leftPart.compare(rule.leftPart);
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

    public void printLeftPart() {
        printLeftPart(leftPart);
    }

    private void printLeftPart(RuleNode node) {
        if (node.link != null) {
            System.out.print(node.link + ".");
        }
        System.out.print(node.tag + node.number);
        if (node.word != null) {
            System.out.print("." + node.word);
        }
        if (node.children.size() > 0) {
            System.out.print("(");
            int i = 0;
            for (RuleNode child : node.children) {
                printLeftPart(child);
                if (i < node.children.size() - 1)
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
                System.out.println("POS " + grammar.partOfSpeech + "     number " + grammar.number);
                HashMap<String, Feature> features = grammar.features;
                Set<String> keys = features.keySet();
                for (String key : keys) {
                    System.out.print("Key " + key + "     Value ");
                    features.get(key).print();
                    System.out.println();
                }
            }
            System.out.println();
        }
    }

    public RuleNode getLeftPart() {
        return leftPart;
    }

    public List<List<Grammar>> getRightPart() {
        return rightPart;
    }
}
