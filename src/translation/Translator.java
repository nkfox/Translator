package translation;

import translation.database.Dictionary;
import translation.database.PersistException;
import translation.rule.Rule;
import translation.tree.Parser;
import translation.tree.TranslateGrammar;
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

    public static void main(String[] args) throws PersistException {

       /* TranslateGrammar gr = new TranslateGrammar("user",true);
        Dictionary translator = new Dictionary();
        List<String> list = translator.getEndings(gr); //getRussianTranslation("user", "сущ.");*/

        TranslateGrammar tr = new TranslateGrammar("user",true);
        Dictionary translator = new Dictionary();
        //List<String> list = translator.getRussianTranslation("user", "сущ.");
        List<String> list = translator.getEndings(tr);
        System.out.println(list.size());

        for(String s: list)
        System.out.println(s);

        getSentences();
        List<TranslateTree> trees = Parser.getTrees(sentences);
        getRules();

        /*System.out.println("\n");

        trees.get(1).combine(rules);
        System.out.println("\n--------------------------------------------------------------------------\n");
        trees.get(1).printGrammar();*/
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
