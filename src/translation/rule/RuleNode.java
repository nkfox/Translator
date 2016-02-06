package translation.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 */
public class RuleNode {

    private String tag;
    private int number;
    List<RuleNode> children;

    public RuleNode(String tag, int number) {
        this.tag = tag;
        this.number = number;
        children = new ArrayList<>();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<RuleNode> getChildren() {
        return children;
    }

    public void addChild(RuleNode child){
        children.add(child);
    }
}
