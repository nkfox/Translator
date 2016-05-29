package translation.tree;

import translation.rule.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 * Contains tree of a sentence.
 */
public class TranslateTree {

    private TranslateNode root;

    private HashMap<String, Integer> dependencies;

    public TranslateTree(List<String> tags, List<String> links) {
        makeTree(tags, links);
        dependencies = new HashMap<>();
        //print();System.out.println();
    }

    public void combine(List<Rule> rules) {
        root.rightChildren.get(0).combine(rules, dependencies);
    }

    private void makeTree(List<String> tags, List<String> links) {
        List<TranslateNode> nodes = new ArrayList<>();
        nodes.add(new TranslateNode("ROOT", 0, null));

        for (int i = 0; i < tags.size(); ) {
            String[] word = tags.get(i).split("/");
            nodes.add(new TranslateNode(word[0], ++i, word[1]));
        }

        for (String link : links) {
            addLink(nodes, link);
        }

        root = nodes.get(0);
    }

    private static boolean addLink(List<TranslateNode> nodes, String link) {
        List<String> info = parseLink(link);
        if (info != null && info.size() >= 5) {
            String linkName = info.get(0);
            TranslateNode parent = nodes.get(Integer.valueOf(info.get(2)));
            TranslateNode child = nodes.get(Integer.valueOf(info.get(4)));
            if (linkName.equals("mwe") || linkName.equals("prt")){
                parent.word = parent.word + " " + child.word;
                for(TranslateGrammar grammar :parent.grammar){
                    grammar.englishWord = parent.word;
                }

            } else {
                child.link = linkName;
                parent.addChild(child);
            }
        }
        return false;
    }

    private static List<String> parseLink(String s) {
        String[] array1 = s.split("\\(");
        Pattern p = Pattern.compile("(\\(.*\\))");
        Matcher m = p.matcher(s);
        if (m.find()) {
            String a = m.group();
            String b = a.substring(1, a.length() - 1);
            String[] array = b.split(", ");
            String[] parent = array[0].split("-");
            String[] son = array[1].split("-");
            ArrayList<String> list = new ArrayList<>();
            list.add(array1[0]);
            list.add(parent[0]);
            list.add(parent[1]);
            list.add(son[0]);
            list.add(son[1]);
            return list;
        } else {
            return null;
        }
    }

    public void print() {
        root.print();
    }

    public void printGrammar() {
        List<TranslateGrammar> grammars = root.rightChildren.get(0).grammar;
        int i=0;
        for(TranslateGrammar grammar:grammars){
            System.out.println("Translation #"+i++);
            grammar.print();
            System.out.println("--------------------------------------------------------------------------");
        }
    }
}
