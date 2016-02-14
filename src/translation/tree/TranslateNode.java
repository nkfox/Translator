package translation.tree;

import translation.rule.Rule;
import translation.rule.RuleNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 */
public class TranslateNode {

    private String link;
    private String word;
    private int number;
    private String tag;

    private TranslateNode parent;

    private List<TranslateNode> leftChildren;
    private List<TranslateNode> rightChildren;

    private Grammar grammar;

    public TranslateNode(String word, int number, String tag) {
        this.link = null;
        this.word = word;
        this.number = number;
        this.tag = tag;
        this.parent = null;
        this.leftChildren = new ArrayList<>();
        this.rightChildren = new ArrayList<>();
        this.grammar = new Grammar();
    }

    public void combine(List<Rule> rules) {
        /*while*/if (!leftChildren.isEmpty() || !rightChildren.isEmpty()) { //!!!!
            List<Rule> suitableRules = getSuitableRules(rules); // заменить на get firstSuitableRule?

            print();
            System.out.println();
            for (Rule rule : suitableRules) {
                rule.printLeftPart(rule.getLeftPart());
                System.out.println();
            }

        }
    }

    private List<Rule> getSuitableRules(List<Rule> rules) {
        List<Rule> suitableRules =
                rules.stream().filter(rule -> isSuitableRule(rule.getLeftPart())).collect(Collectors.toList());
        return suitableRules;
    }

    private boolean isSuitableRule(RuleNode ruleLeftPart) {
        if (this.tag.equals(ruleLeftPart.getTag()) && //(this.link == null || this.link.equals(ruleLeftPart.getLink())) &&
                (ruleLeftPart.getWord() == null || this.word.equals(ruleLeftPart.getWord()))) {

            if (ruleLeftPart.getChildren().size() == 0) return true;

            int leftInd = leftChildren.size() - 1;
            int rightInd = 0;
            boolean firstFound = false;
            List<RuleNode> ruleChildren = ruleLeftPart.getChildren();
            for (RuleNode ruleChild : ruleChildren) {
                boolean found = false;
                if (ruleLeftPart.isLeftChild(ruleChild)) {
                    while (!found && leftInd >= 0) {
                        TranslateNode child = leftChildren.get(leftInd);
                        found = child.isSuitableRule(ruleChild);
                        if (found){
                            firstFound = true;
                        } else{
                            if (!firstFound)
                                return false;
                        }
                        leftInd--;
                    }
                } else {
                    while (!found && rightInd < rightChildren.size()) {
                        TranslateNode child = rightChildren.get(rightInd);
                        found = child.isSuitableRule(ruleChild);
                        if (found){
                            firstFound = true;
                        } else{
                            if (!firstFound)
                                return false;
                        }
                        rightInd++;
                    }
                }
                if (!found)
                    return false;
            }
            return true;
        }
        return false;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public TranslateNode getParent() {
        return parent;
    }

    public void setParent(TranslateNode parent) {
        this.parent = parent;
    }

    public List<TranslateNode> getLeftChildren() {
        return leftChildren;
    }

    public void setLeftChildren(List<TranslateNode> leftChildren) {
        this.leftChildren = leftChildren;
    }

    public List<TranslateNode> getRightChildren() {
        return rightChildren;
    }

    public void setRightChildren(List<TranslateNode> rightChildren) {
        this.rightChildren = rightChildren;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
    }

    public void addChild(TranslateNode child) {
        int childNumber = child.getNumber();
        if (number > childNumber) {
            leftChildren.add(child);
        } else {
            rightChildren.add(child);
        }
    }

    public void print() {
        System.out.print("(");
        leftChildren.forEach(translation.tree.TranslateNode::print);
        System.out.print(word);
        rightChildren.forEach(translation.tree.TranslateNode::print);
        System.out.print(")");
    }
}
