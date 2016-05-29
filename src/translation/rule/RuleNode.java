package translation.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 * Contains node of rule (node is lest part of a sentence)
 */
public class RuleNode {

    String link;
    String tag;
    int number;
    String word;
    List<RuleNode> children;

    public RuleNode(String leftPart, String link) {
        this.link = link;
        this.word = null;
        children = new ArrayList<>();
        getNode(leftPart);
    }

    public RuleNode(String leftPart) {
        this(leftPart, null);
    }

    public int compare(RuleNode node) {

        int compare = compareLink(node);
        if (compare != 0) return compare;

        compare = compareTag(node);
        if (compare != 0) return compare;

        compare = compareWord(node);
        if (compare != 0) return compare;

        compare = compareChildren(node);

        return compare;
    }

    private int compareLink(RuleNode ruleNode) {
        if (this.link == null) {
            if (ruleNode.link == null) {
                return 0;
            }
            return -1;
        }
        if (ruleNode.link == null) {
            return 1;
        }
        return this.link.compareTo(ruleNode.link);
    }

    public boolean parentTagOf(String childTag) {
        return "JJ".equals(tag) && ("JJS".equals(childTag) || "JJR".equals(childTag)) ||
                "NN".equals(tag) && ("NNS".equals(childTag) || "NNP".equals(childTag) || "NNPS".equals(childTag)) ||
                "RB".equals(tag) && ("RBS".equals(childTag) || "RBR".equals(childTag)) ||
                "VB".equals(tag) && (childTag.length() > 2 && childTag.charAt(0) == 'V' && childTag.charAt(1) == 'B');
    }

    private int compareTag(RuleNode ruleNode) {
        if (ruleNode.parentTagOf(this.tag)) {
            return -1;
        }
        if (this.parentTagOf(ruleNode.tag)) {
            return 1;
        }
        return this.tag.compareTo(ruleNode.tag);
    }

    private int compareWord(RuleNode ruleNode) {
        if (this.word == null) {
            if (ruleNode.word == null) {
                return 0;
            }
            return 1;
        }
        if (ruleNode.word == null) {
            return -1;
        }
        return this.word.compareTo(ruleNode.word);
    }

    private int compareChildren(RuleNode ruleNode) {
        int compare = compareDirection(ruleNode);
        if (compare != 0) return compare;

        int index = 0;
        while (compare == 0 && index < this.children.size() && index < ruleNode.children.size()) {
            compare = this.children.get(index).compare(ruleNode.children.get(index));
            index++;
        }
        if (compare == 0) {
            if (this.children.size() > ruleNode.children.size())
                return -1;
            else if (this.children.size() < ruleNode.children.size())
                return 1;
        }
        return compare;
    }

    private int compareDirection(RuleNode ruleNode) {
        int dir = this.direction();
        int otherDir = ruleNode.direction();
        if (dir == otherDir) return 0;
        if (Math.abs(dir) < Math.abs(otherDir) || Math.abs(dir) == Math.abs(otherDir) && dir > otherDir)
            return -1;
        return 1;
    }

    private int direction() {
        if (this.children.isEmpty())
            return 0;
        if (this.children.get(0).number < this.number) return -1;
        return 1;
    }

    private void getNode(String leftPart) {
        leftPart = leftPart.replaceAll("[\uFEFF-\uFFFF]", "");
        StringBuilder info = new StringBuilder();
        int i = 0;
        while (leftPart.charAt(i) >= 'A' && leftPart.charAt(i) <= 'Z' || leftPart.charAt(i) == '$')
            info.append(leftPart.charAt(i++));
        this.tag = info.toString();

        info = new StringBuilder();
        while (i < leftPart.length() && leftPart.charAt(i) >= '0' && leftPart.charAt(i) <= '9')
            info.append(leftPart.charAt(i++));
        this.number = Integer.valueOf(info.toString());

        if (i < leftPart.length() && leftPart.charAt(i) == '.') {
            i++;
            info = new StringBuilder();
            while (i < leftPart.length() && leftPart.charAt(i) >= 'a' && leftPart.charAt(i) <= 'z')
                info.append(leftPart.charAt(i++));
            this.word = info.toString();
        }

        i++;
        if (i < leftPart.length())
            getDependencies(leftPart.substring(i, leftPart.length() - 1));

        sortChildren();
    }

    private void sortChildren() {
        for (int i = 0; i < children.size() - 1; i++) {
            for (int j = i + 1; j < children.size(); j++) {
                if (Math.abs(children.get(i).number - this.number) > Math.abs(children.get(j).number - this.number)) {
                    RuleNode temp = children.get(i);
                    children.set(i, children.get(j));
                    children.set(j, temp);
                }
            }
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

    public boolean isLeftChild(RuleNode child) {
        return this.number > child.number;
    }

    public String getLink() {
        return link;
    }

    public String getTag() {
        return tag;
    }

    public int getNumber() {
        return number;
    }

    public String getWord() {
        return word;
    }

    public List<RuleNode> getChildren() {
        return children;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append(link);
        s.append(".").append(tag);
        s.append(number);
        if (word != null)
            s.append(".").append(word);
        if (children.size()>0) {
            s.append("(");
            for (RuleNode child : children) {
                s.append(child.toString());
            }
            s.append(")");
        }
        return s.toString();
    }
}
