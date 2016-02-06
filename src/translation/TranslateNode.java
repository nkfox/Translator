package translation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 */
public class TranslateNode {

    private String word;
    private int number;
    private String tag;

    private Dependency parentDependency;

    private List<Dependency> leftChildren;
    private List<Dependency> rightChildren;

    private Grammar grammar;

    public TranslateNode(String word, int number, String tag) {
        this.word = word;
        this.number = number;
        this.tag = tag;
        this.parentDependency = null;
        this.leftChildren = new ArrayList<>();
        this.rightChildren = new ArrayList<>();
        this.grammar = new Grammar();
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

    public Dependency getParentDependency() {
        return parentDependency;
    }

    public void setParentDependency(Dependency parentDependency) {
        this.parentDependency = parentDependency;
    }

    public List<Dependency> getLeftChildren() {
        return leftChildren;
    }

    public void setLeftChildren(List<Dependency> leftChildren) {
        this.leftChildren = leftChildren;
    }

    public List<Dependency> getRightChildren() {
        return rightChildren;
    }

    public void setRightChildren(List<Dependency> rightChildren) {
        this.rightChildren = rightChildren;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
    }

    public void addChild(Dependency dependency){
        int childNumber = dependency.getChild().getNumber();
        if (number>childNumber){
            leftChildren.add(dependency);
        } else{
            rightChildren.add(dependency);
        }
    }
}
