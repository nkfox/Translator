package translation;

import translation.rule.Rule;
import translation.tree.Parser;
import translation.tree.TranslateTree;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 * Main class. Gets all rules and sentences, builds trees, combines them.
 */
public class Translator {

    private static List<String> sentences = new ArrayList<>();
    private static List<Rule> rules = new ArrayList<>();
    //private static List<String> r = new ArrayList<>();

    public static void main(String[] args) {

        getSentences();
        List<TranslateTree> trees = Parser.getTrees(sentences);
        Rule rule1 = new Rule("VB2(nsubj.NN1)#С1.пад:им&чис:Y&род:Z,Г2.чис:y&род:z&зал:дей&вр:наст(VB)");
        Rule rule2 = new Rule("VB3(nsubj.NN1,aus.MD2.will)#С1.пад:им&чис:Y&род:Z,Г3.чис:y&род:z&зал:дей&вр:буд&асп:сов(VB)");
        rule1.compareTo(rule2);
        getRules();

        System.out.println("\n");

        trees.get(1).combine(rules);
        System.out.println("\n--------------------------------------------------------------------------\n");
        trees.get(1).printGrammar();
    }

    private static void getSentences() {
        sentences.add("I sat on the chair.");
        sentences.add("Improved output quality are achieved by human intervention.");//Improved output quality can also be achieved by human intervention.
        sentences.add("Machine translation uses a method based on linguistic rules.");
    }

    private static void getRules() {
        String content;
        try (FileInputStream inp = new FileInputStream("D:\\Users\\Documents\\IdeaProjects\\Translator\\src\\translation\\rule\\rules.txt")) {
            Scanner in = new Scanner(new InputStreamReader(inp, "UTF-8"));

            while (in.hasNext()) {
                content = in.nextLine();
                if (content.length() > 0) {
                    rules.add(new Rule(content));
                    //r.add(content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Rule.amount+"  "+Rule.maxLen+"  "+Rule.sum);

        Collections.sort(rules);
        /*Collections.sort(r);
        for(String s:r){
            System.out.println(s);
        }*/

        /*System.out.println("------------------------------------------------------------");
        for (Rule rule : rules) {
            rule.printLeftPart(rule.getLeftPart());
            //rule.printRightPart();
            System.out.println();
        }
        System.out.println("------------------------------------------------------------");*/
    }

}
