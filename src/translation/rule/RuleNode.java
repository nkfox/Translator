package translation.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 */
public class RuleNode {

    private String link;
    private String tag;
    private int number;
    private String word;
    private List<RuleNode> children;

    public RuleNode(String tag, int number) {
        this.link = null;
        this.tag = tag;
        this.number = number;
        this.word = null;
        children = new ArrayList<>();
    }

    public RuleNode(String tag, int number, String word) {
        this(tag, number);
        this.word = word;
    }

    public RuleNode(String leftPart, String link) {
        this.link = link;
        this.word = null;
        children = new ArrayList<>();
        getNode(leftPart);
    }

    public RuleNode(String leftPart) {
        this(leftPart,null);
    }

    private void getNode(String leftPart) {
        StringBuilder info = new StringBuilder();
        int i = 0;
        while (leftPart.charAt(i) >= 'A' && leftPart.charAt(i) <= 'Z' || leftPart.charAt(i) == '$') {
            info.append(leftPart.charAt(i++));
        }
        this.tag = info.toString();

        info = new StringBuilder();
        while (i < leftPart.length() && leftPart.charAt(i) >= '0' && leftPart.charAt(i) <= '9') {
            info.append(leftPart.charAt(i++));
        }
        this.number = Integer.valueOf(info.toString());

        if (i < leftPart.length() && leftPart.charAt(i) == '.') {
            i++;
            info = new StringBuilder();
            while (i < leftPart.length() && leftPart.charAt(i) >= 'a' && leftPart.charAt(i) <= 'z') {
                info.append(leftPart.charAt(i++));
            }
            this.word = info.toString();
        }

        i++;
        if (i < leftPart.length()) {
            getDependencies(leftPart.substring(i, leftPart.length() - 1));
        }
    }

    private void getDependencies(String leftPart) {
        List<String> dependencies = splitDependencies(leftPart);

        for (String dependency : dependencies) {
            StringBuilder info = new StringBuilder();
            int i = 0;

            while (dependency.charAt(i) >= 'a' && dependency.charAt(i) <= 'z' || dependency.charAt(i) == ':') {
                info.append(dependency.charAt(i++));
            }
            String link = info.toString();

            i++;
            RuleNode child = new RuleNode(dependency.substring(i, dependency.length()), link);

            children.add(child);
        }

    }

    private List<String> splitDependencies(String leftPart) {
        List<String> dependencies = new ArrayList<>();
        StringBuilder info = new StringBuilder();
        int i = 0;
        int brackets = 0;

        while (i < leftPart.length()) {
            if (leftPart.charAt(i) == ',' && brackets == 0) {
                dependencies.add(info.toString());
                info = new StringBuilder();
            } else {
                info.append(leftPart.charAt(i));
                if (leftPart.charAt(i) == '(') {
                    brackets++;
                } else if (leftPart.charAt(i) == ')') {
                    brackets--;
                }
            }
            i++;
        }
        if (info.length() > 0) {
            dependencies.add(info.toString());
        }

        return dependencies;
    }

    public boolean isLeftChild(RuleNode child){
        return this.number> child.number;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setChildren(List<RuleNode> children) {
        this.children = children;
    }

    public List<RuleNode> getChildren() {
        return children;
    }

    public void addChild(RuleNode child) {
        children.add(child);
    }
}
