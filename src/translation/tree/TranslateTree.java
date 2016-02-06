package translation.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 */
public class TranslateTree {

    private TranslateNode root;

    public TranslateTree(List<String> tags, List<String> links) {
        makeTree(tags, links);

        //printNode(root); System.out.println("\n");
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

    private static void addLink(List<TranslateNode> nodes, String link) {
        List<String> info = parseLink(link);
        if (info.size() >= 5) {
            String linkName = info.get(0);
            TranslateNode parent = nodes.get(Integer.valueOf(info.get(2)));
            TranslateNode child = nodes.get(Integer.valueOf(info.get(4)));
            Dependency dependency = new Dependency(linkName, parent, child);
            parent.addChild(dependency);
            child.setParentDependency(dependency);
        }
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

    private static void printNode(TranslateNode node) {
        System.out.print("(");
        for (Dependency dep : node.getLeftChildren()) {
            printNode(dep.getChild());
        }
        System.out.print(node.getWord());
        for (Dependency dep : node.getRightChildren()) {
            printNode(dep.getChild());
        }
        System.out.print(")");
    }
}
