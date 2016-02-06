package translation.rule;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 */
public class RuleDependency {

    private String link;
    private RuleNode child;

    public RuleDependency(String link, RuleNode child) {
        this.link = link;
        this.child = child;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public RuleNode getChild() {
        return child;
    }

    public void setChild(RuleNode child) {
        this.child = child;
    }
}
