package translation.tree;

import edu.stanford.nlp.process.Morphology;
import translation.rule.Grammar;
import translation.rule.Rule;
import translation.rule.RuleNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 * Contains node of tree
 */
public class TranslateNode {

    private String link;
    private String word;
    private int number;
    private String tag;

    private List<TranslateNode> leftChildren;
    private List<TranslateNode> rightChildren;

    private TranslateGrammar grammar;

    public TranslateNode(String word, int number, String tag) {
        this.link = null;
        this.word = new Morphology().lemma(word, tag);//word;
        this.number = number;
        this.tag = tag;
        this.leftChildren = new ArrayList<>();
        this.rightChildren = new ArrayList<>();
        this.grammar = new TranslateGrammar(this.word,this.tag);
    }

    public boolean combine(List<Rule> rules) {
        boolean onceChanged = false;

        while (!leftChildren.isEmpty() || !rightChildren.isEmpty()) {

            List<Rule> suitableRules = getSuitableRules(rules); // заменить на get firstSuitableRule?

            print();
            System.out.println("\nAll rules");
            for (Rule rule : suitableRules) {
                rule.printLeftPart(rule.getLeftPart());
                System.out.println();
            }

            Rule suitableRule = getSuitableRule(suitableRules);
            if (suitableRule != null) {
                applyRule(suitableRule);
                onceChanged = true;
            } else {
                int index = 0; // а разве не с конца??
                boolean changed = false;
                while (index < leftChildren.size() && !changed) {
                    changed = leftChildren.get(index++).combine(rules);
                }
                index = 0;
                while (index < rightChildren.size() && !changed) {
                    changed = rightChildren.get(index++).combine(rules);
                }
                if (changed) {
                    onceChanged = true;
                } else {
                    System.out.println("No suitable rules");
                    return onceChanged;
                }
            }
            System.out.println();

        }
        return onceChanged;
    }

    private void applyRule(Rule rule) {
        List<TranslateGrammar> newGrammar = new ArrayList<>();
        addGrammarToList(rule.getLeftPart(), newGrammar);
        /*for(TranslateGrammar grammar: newGrammar){
            System.out.print(grammar.englishWord+" ");
        }
        System.out.println();*/

        newGrammar = updateGrammar(newGrammar, rule.getRightPart().get(0));         // change

        int mainGrammarNumber = rule.getLeftPart().getNumber() - 1;
        TranslateGrammar mainGrammar = newGrammar.get(mainGrammarNumber);
        for (int i = 0; i < mainGrammarNumber; i++) {
            TranslateGrammar childGrammar = newGrammar.get(i);
            if (childGrammar != null)
                mainGrammar.leftChildren.add(childGrammar);
        }
        for (int i = mainGrammarNumber + 1; i < newGrammar.size(); i++) {
            TranslateGrammar childGrammar = newGrammar.get(i);
            if (childGrammar != null)
                mainGrammar.rightChildren.add(childGrammar);
        }
        this.grammar = mainGrammar;
    }

    private List<TranslateGrammar> updateGrammar(List<TranslateGrammar> oldGrammar, List<Grammar> addedGrammar) {
        List<TranslateGrammar> newGrammar = new ArrayList<>();
        for (Grammar grammar : addedGrammar) {
            int number = grammar.getNumber() - 1;
            oldGrammar.get(number).update(grammar);

            while (number >= newGrammar.size()) {
                newGrammar.add(null);
            }
            newGrammar.set(number, oldGrammar.get(number));
        }
        return newGrammar;
    }

    private boolean addGrammarToList(RuleNode node, List<TranslateGrammar> newGrammar) {

        if ((node.getTag().equals(this.tag) || node.parentTagOf(this.tag)) &&
                (node.getLink() == null || node.getLink().equals(this.link)) &&
                (node.getWord() == null || node.getWord().equals(this.word))) {

            int index = node.getNumber() - 1;
            while (index >= newGrammar.size()) {
                newGrammar.add(null);
            }
            newGrammar.set(index, this.grammar);

            int leftInd = leftChildren.size() - 1;
            int rightInd = 0;
            List<RuleNode> ruleChildren = node.getChildren();
            for (RuleNode ruleChild : ruleChildren) {
                if (node.isLeftChild(ruleChild)) {
                    leftInd = findChild(ruleChild, leftChildren, leftInd, -1, newGrammar);
                } else {
                    rightInd = findChild(ruleChild, rightChildren, rightInd, 1, newGrammar);
                }
            }
            return true;
        }
        return false;
    }

    private int findChild(RuleNode ruleChild, List<TranslateNode> children, int index, int direction, List<TranslateGrammar> newGrammar) {
        boolean found = false;
        while (!found && (direction < 0 && index >= 0 || direction > 0 && index < children.size())) {
            TranslateNode child = children.get(index);
            found = child.addGrammarToList(ruleChild, newGrammar);
            if (found) {
                children.remove(index);
                if (direction<0)
                    index--;
            }
            else
                index += direction;
        }
        return index;
    }

    private Rule getSuitableRule(List<Rule> rules) {
        if (rules.isEmpty()) return null;
        return rules.get(0);

        /*for(Rule rule: rules){
            if (isSuitableRule(rule.getLeftPart()))
                return rule;
        }
        return null;*/
    }

    private List<Rule> getSuitableRules(List<Rule> rules) {
        return rules.stream().filter(rule -> isSuitableRule(rule.getLeftPart())).collect(Collectors.toList());
    }

    private boolean isSuitableRule(RuleNode ruleLeftPart) {
        if ((ruleLeftPart.getTag().equals(this.tag) || ruleLeftPart.parentTagOf(this.tag)) &&
                (ruleLeftPart.getLink() == null || ruleLeftPart.getLink().equals(this.link)) &&
                (ruleLeftPart.getWord() == null || ruleLeftPart.getWord().equals(this.word))) {

            if (ruleLeftPart.getChildren().size() == 0) {
                return this.getRightChildren().size() + this.getLeftChildren().size() == 0;
            }

            int leftInd = leftChildren.size() - 1;
            int rightInd = 0;
            boolean firstFound = false;
            List<RuleNode> ruleChildren = ruleLeftPart.getChildren();
            for (RuleNode ruleChild : ruleChildren) {
                boolean found = false;
                if (ruleLeftPart.isLeftChild(ruleChild)) {
                    leftInd = findChild(ruleChild, leftChildren, leftInd, -1, firstFound);
                } else {
                    rightInd = findChild(ruleChild, rightChildren, rightInd, 1, firstFound);
                }
                if (leftInd >= -1 && rightInd >= -1) {
                    found = true;
                    firstFound = true;
                }
                if (!found)
                    return false;
            }
            return true;
        }
        return false;
    }

    private int findChild(RuleNode ruleChild, List<TranslateNode> children, int index, int direction, boolean firstFound) {
        boolean found = false;
        while (!found && (direction < 0 && index >= 0 || direction > 0 && index < children.size())) {
            TranslateNode child = children.get(index);
            found = child.isSuitableRule(ruleChild);
            if (!found && !firstFound)
                return -5;
            if (found)
                firstFound = true;
            index += direction;
        }
        if (found)
            return index;
        else
            return -5;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getNumber() {
        return number;
    }

    public List<TranslateNode> getLeftChildren() {
        return leftChildren;
    }

    public List<TranslateNode> getRightChildren() {
        return rightChildren;
    }

    public void addChild(TranslateNode child) {
        int childNumber = child.getNumber();
        if (number > childNumber) {
            leftChildren.add(child);
        } else {
            rightChildren.add(child);
        }
    }

    public TranslateGrammar getGrammar() {
        return grammar;
    }

    public void print() {
        System.out.print("(");
        leftChildren.forEach(translation.tree.TranslateNode::print);
        System.out.print(link + "." + word);
        rightChildren.forEach(translation.tree.TranslateNode::print);
        System.out.print(")");
    }
}
