package translation.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 */
public class RuleNode {

    private String tag;
    private int number;
    private String word;
    private List<RuleDependency> children;

    public RuleNode(String tag, int number) {
        this.tag = tag;
        this.number = number;
        this.word = null;
        children = new ArrayList<>();
    }

    public RuleNode(String tag, int number, String word) {
        this(tag, number);
        this.word = word;
    }

    public RuleNode(String leftPart) {
        this.word = null;
        children = new ArrayList<>();
        getNode(leftPart);
    }

    private void getNode(String leftPart){
        StringBuilder info = new StringBuilder();
        int i=0;
        while(leftPart.charAt(i)>='A' && leftPart.charAt(i)<='Z' || leftPart.charAt(i)=='$') {
            info.append(leftPart.charAt(i++));
        }
        this.tag = info.toString();

        info = new StringBuilder();
        while(i<leftPart.length() && leftPart.charAt(i)>='0' && leftPart.charAt(i)<='9') {
            info.append(leftPart.charAt(i++));
        }
        this.number = Integer.valueOf(info.toString());

        if (i<leftPart.length() && leftPart.charAt(i)=='.'){
            i++;
            info = new StringBuilder();
            while(i<leftPart.length() && leftPart.charAt(i)>='a' && leftPart.charAt(i)<='z') {
                info.append(leftPart.charAt(i++));
            }
            this.word = info.toString();
        }

        i++;
        if (i<leftPart.length()) {
            getDependencies(leftPart.substring(i, leftPart.length() - 1));
        }
    }

    private void getDependencies(String leftPart){
        List<String> dependencies = splitDependencies(leftPart);

        for(String dependency:dependencies) {
            StringBuilder info = new StringBuilder();
            int i = 0;

            while (leftPart.charAt(i) >= 'a' && leftPart.charAt(i) <= 'z') {
                info.append(leftPart.charAt(i++));
            }
            String link = info.toString();

            i++;
            RuleNode child = new RuleNode(dependency.substring(i,dependency.length()));

            children.add(new RuleDependency(link,child));
        }

    }

    private List<String> splitDependencies(String leftPart){
        List<String> dependencies = new ArrayList<>();
        StringBuilder info = new StringBuilder();
        int i=0;
        int brackets = 0;

        while(i<leftPart.length()){
            if (leftPart.charAt(i)==',' && brackets == 0){
                dependencies.add(info.toString());
            } else{
                info.append(leftPart.charAt(i));
                if (leftPart.charAt(i)=='('){
                    brackets++;
                }
                else if (leftPart.charAt(i)==')'){
                    brackets--;
                }
            }
            i++;
        }
        if(info.length()>0){
            dependencies.add(info.toString());
        }

        return dependencies;
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

    public void setChildren(List<RuleDependency> children) {
        this.children = children;
    }

    public List<RuleDependency> getChildren() {
        return children;
    }

    public void addChild(RuleDependency child) {
        children.add(child);
    }
}
