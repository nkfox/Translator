package translation.tree;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 */
public class Dependency {

    private String link;
    private TranslateNode parent;
    private TranslateNode child;

    public Dependency(String link, TranslateNode parent, TranslateNode child) {
        this.link = link;
        this.parent = parent;
        this.child = child;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public TranslateNode getParent() {
        return parent;
    }

    public void setParent(TranslateNode parent) {
        this.parent = parent;
    }

    public TranslateNode getChild() {
        return child;
    }

    public void setChild(TranslateNode child) {
        this.child = child;
    }
}
