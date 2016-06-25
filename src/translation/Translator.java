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

        /*Dictionary translator = new Dictionary();

        TranslateGrammar tr = new TranslateGrammar("user",1);
        List<TranslateGrammar> list = translator.getRussianTranslation(tr);
        List<TranslateGrammar> list1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            list1.addAll(translator.getEndings(list.get(i)));
        }
        System.out.println(list.size());
        for(TranslateGrammar s: list1)
            System.out.println(s.getWord()+" "+ s.getGender());



        TranslateGrammar tr2 = new TranslateGrammar("leave",2);
        list = translator.getRussianTranslation(tr2);
        list1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            list1.addAll(translator.getEndings(list.get(i)));
        }
        System.out.println(list.size());
        for(TranslateGrammar s: list1)
            System.out.println(s.getWord()+" "+ s.getGender());

*/

        getSentences();
        List<TranslateTree> trees = Parser.getTrees(sentences);
        getRules();

        System.out.println("\n");

        int sentenceIndex = 4;
        trees.get(sentenceIndex).combine(rules);
        System.out.println("\n--------------------------------------------------------------------------\n");
        trees.get(sentenceIndex).printGrammar();

        List<List<TranslateGrammar>> translations = trees.get(sentenceIndex).translate();
        System.out.println("\n--------------------------------------------------------------------------\n");
        int i=0;
        for(List<TranslateGrammar> translation:translations){
            if (i++==2) break;
            for(TranslateGrammar word: translation){
                System.out.print(word.getWord()+" ");
            }
            System.out.println();
        }
    }

    private static void getSentences() {
        sentences.add("I sat on the chair.");
        sentences.add("Improved output quality are achieved by human intervention.");//Improved output quality can also be achieved by human intervention.
        sentences.add("Machine translation uses a method based on linguistic rules.");
        sentences.add("I like dogs as well as cats.");
        sentences.add("user left the room.");
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
