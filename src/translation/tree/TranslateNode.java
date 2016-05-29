package translation.tree;

import edu.stanford.nlp.process.Morphology;
import translation.rule.Feature;
import translation.rule.Grammar;
import translation.rule.Rule;
import translation.rule.RuleNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 * Contains node of tree
 */
public class TranslateNode {

    String link;
    String word;
    int number;
    String tag;

    List<TranslateNode> leftChildren;
    List<TranslateNode> rightChildren;

    List<TranslateGrammar> grammar;

    public TranslateNode(String word, int number, String tag) {
        this.link = null;
        this.word = new Morphology().lemma(word, tag);
        this.number = number;
        this.tag = tag;
        this.leftChildren = new ArrayList<>();
        this.rightChildren = new ArrayList<>();
        this.grammar = new ArrayList<>();
        this.grammar.add(new TranslateGrammar(this.word));
    }

    public boolean combine(List<Rule> rules, HashMap<String, Integer> dependencies) {
        boolean onceChanged = false;
        while (!leftChildren.isEmpty() || !rightChildren.isEmpty()) {
            List<Rule> suitableRules = getSuitableRules(rules); // заменить на get firstSuitableRule?
            Rule suitableRule = getSuitableRule(suitableRules);

            if (suitableRule != null) {
                applyRule(suitableRule, dependencies);
                onceChanged = true;
            } else {
                boolean changed = combineChildren(rules, dependencies);
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

    public boolean combineChildren(List<Rule> rules, HashMap<String, Integer> dependencies) {
        int index = 0; // а разве не с конца??
        boolean changed = false;
        while (index < leftChildren.size() && !changed) {
            changed = leftChildren.get(index++).combine(rules, dependencies);
        }
        index = 0;
        while (index < rightChildren.size() && !changed) {
            changed = rightChildren.get(index++).combine(rules, dependencies);
        }
        return changed;
    }

    private void applyRule(Rule rule, HashMap<String, Integer> dependencies) {
        List<TranslateGrammar> newGrammars = new ArrayList<>();
        List<List<TranslateGrammar>> oldGrammars = getListOfGrammar(rule);

        for(List<TranslateGrammar> oldGrammar: oldGrammars) {
            for (List<Grammar> grammar : rule.getRightPart()) {
                List<TranslateGrammar> newGrammar = updateGrammar(cloneListOfTranslateGrammar(oldGrammar), grammar, dependencies);

                TranslateGrammar mainGrammar = makeNewGrammar(rule,newGrammar);
                newGrammars.add(mainGrammar);
            }
        }
        this.grammar = newGrammars;
    }

    private TranslateGrammar makeNewGrammar(Rule rule,List<TranslateGrammar> newGrammar){
        int mainGrammarNumber = rule.getLeftPart().getNumber() - 1; // change to newPOS!!!!
        TranslateGrammar mainGrammar = newGrammar.get(mainGrammarNumber);
        int pos = 0;
        for (int i = 0; i < mainGrammarNumber; i++) {
            TranslateGrammar childGrammar = newGrammar.get(i);
            if (childGrammar != null)
                mainGrammar.leftChildren.add(pos++,childGrammar);
        }
        for (int i = mainGrammarNumber + 1; i < newGrammar.size(); i++) {
            TranslateGrammar childGrammar = newGrammar.get(i);
            if (childGrammar != null)
                mainGrammar.rightChildren.add(childGrammar);
        }
        return mainGrammar;
    }

    private List<List<TranslateGrammar>> getListOfGrammar(Rule rule){
        List<List<TranslateGrammar>> allVariantsOfGrammar = new ArrayList<>();
        allVariantsOfGrammar.add(new ArrayList<>());
        addGrammarToList(rule.getLeftPart(), allVariantsOfGrammar);
        return makeNewListOfGrammar(allVariantsOfGrammar);
    }

    private List<List<TranslateGrammar>> makeNewListOfGrammar(List<List<TranslateGrammar>> allVariantsOfGrammar){
        List<List<TranslateGrammar>> newGrammars = new ArrayList<>();
        newGrammars.add(new ArrayList<>());
        int number=0;
        for(List<TranslateGrammar> grammars: allVariantsOfGrammar){
            List<List<TranslateGrammar>> newGrammarsAdded = new ArrayList<>();
            for(TranslateGrammar grammar:grammars){
                for(List<TranslateGrammar> g: newGrammars) {
                    List<TranslateGrammar> added = cloneListOfTranslateGrammar(g);

                    while (number >= added.size())
                        added.add(null);
                    added.set(number, grammar.clone());
                    newGrammarsAdded.add(added);
                }
            }
            newGrammars = newGrammarsAdded;
            number++;
        }
        return newGrammars;
    }

    private List<TranslateGrammar> cloneListOfTranslateGrammar(List<TranslateGrammar> grammars) {
        return grammars.stream().map(TranslateGrammar::clone).collect(Collectors.toList());
    }

    private List<TranslateGrammar> updateGrammar(List<TranslateGrammar> oldGrammar, List<Grammar> addedGrammar,
                                                 HashMap<String, Integer> dependencies) {
        List<TranslateGrammar> newGrammar = new ArrayList<>();
        for (int i = 0; i < addedGrammar.size(); i++)
            addedGrammar.set(i, addedGrammar.get(i).clone());

        PrepareGrammarDependencies(dependencies, addedGrammar);
        for (Grammar grammar : addedGrammar) {
            int number = grammar.getNumber() - 1;
            oldGrammar.get(number).update(grammar);

            while (number >= newGrammar.size())
                newGrammar.add(null);
            newGrammar.set(number, oldGrammar.get(number));
        }
        return newGrammar;
    }

    private void PrepareGrammarDependencies(HashMap<String, Integer> dependencies, List<Grammar> grammars) {
        updateDependencies(dependencies, grammars);
        updateGrammarDependencies(dependencies, grammars);
    }

    private void updateDependencies(HashMap<String, Integer> dependencies, List<Grammar> grammars) {
        for (Grammar grammar : grammars) {
            Set<String> featureNames = grammar.getFeatures().keySet();
            for (String featureName : featureNames) {
                Feature feature = grammar.getFeatures().get(featureName);
                for (String dep : feature.getDependencies()) {
                    char firstLetter = dep.charAt(0);
                    if (firstLetter >= 'A' && firstLetter <= 'Z') {
                        Integer number = dependencies.get("" + firstLetter);
                        if (number == null) {
                            dependencies.put("" + firstLetter, 1);
                        } else {
                            dependencies.put("" + firstLetter, ++number);
                        }
                    }
                }
            }
        }
    }

    private void updateGrammarDependencies(HashMap<String, Integer> dependencies, List<Grammar> grammars) {
        for (Grammar grammar : grammars) {
            Set<String> featureNames = grammar.getFeatures().keySet();
            for (String featureName : featureNames) {
                Feature feature = grammar.getFeatures().get(featureName);
                int i = 0;
                for (String dep : feature.getDependencies()) {
                    char firstLetter = dep.charAt(0);
                    Integer number = dependencies.get(("" + firstLetter).toUpperCase());
                    feature.getDependencies().set(i++, "" + firstLetter + number);
                }

            }
        }
    }

    private boolean addGrammarToList(RuleNode node, List<List<TranslateGrammar>> newGrammars) {
        if ((node.getTag().equals(this.tag) || node.parentTagOf(this.tag)) &&
                (node.getLink() == null || node.getLink().equals(this.link)) &&
                (node.getWord() == null || node.getWord().equals(this.word))) {

            int index = node.getNumber() - 1;
            while (index >= newGrammars.size()) {
                newGrammars.add(null);
            }
            newGrammars.set(index, this.grammar);
            addGrammarChildrenToList(node, newGrammars);

            return true;
        }
        return false;
    }

    private void addGrammarChildrenToList(RuleNode node, List<List<TranslateGrammar>> newGrammars) {
        int leftInd = leftChildren.size() - 1;
        int rightInd = 0;
        List<RuleNode> ruleChildren = node.getChildren();
        for (RuleNode ruleChild : ruleChildren) {
            if (node.isLeftChild(ruleChild)) {
                leftInd = findChild(ruleChild, leftChildren, leftInd, -1, newGrammars);
            } else {
                rightInd = findChild(ruleChild, rightChildren, rightInd, 1, newGrammars);
            }
        }
    }

    private int findChild(RuleNode ruleChild, List<TranslateNode> children, int index, int direction,
                          List<List<TranslateGrammar>> newGrammars) {
        boolean found = false;
        while (!found && (direction < 0 && index >= 0 || direction > 0 && index < children.size())) {
            TranslateNode child = children.get(index);
            found = child.addGrammarToList(ruleChild, newGrammars);
            if (found) {
                children.remove(index);
                if (direction < 0)
                    index--;
            } else
                index += direction;
        }
        return index;
    }

    private Rule getSuitableRule(List<Rule> rules) {

        print();
        System.out.println("\nAll rules");
        for (Rule rule : rules) {
            rule.printLeftPart();
            System.out.println();
        }

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
                return this.rightChildren.size() + this.leftChildren.size() == 0;
            }
            return isSuitableChildren(ruleLeftPart);
        }
        return false;
    }

    private boolean isSuitableChildren(RuleNode ruleLeftPart) {
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

    public void addChild(TranslateNode child) {
        int childNumber = child.number;
        if (number > childNumber) {
            leftChildren.add(child);
        } else {
            rightChildren.add(child);
        }
    }

    public void print() {
        System.out.print("(");
        leftChildren.forEach(translation.tree.TranslateNode::print);
        System.out.print(link + "." + word);
        rightChildren.forEach(translation.tree.TranslateNode::print);
        System.out.print(")");
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("(");
        for(TranslateNode node: leftChildren) {
            s.append(node.toString());
        }
        s.append(link).append(".").append(word);
        for(TranslateNode node: rightChildren) {
            s.append(node.toString());
        }
        s.append(")");
        return s.toString();
    }
}
