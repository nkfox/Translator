package translation;

import translation.rule.Rule;
import translation.tree.Parser;
import translation.tree.TranslateTree;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 */
public class Translator {

    private static List<String> sentences = new ArrayList<>();
    private static List<Rule> rules = new ArrayList<>();

    public static void main(String[] args) {

        getSentences();
        List<TranslateTree> trees = Parser.getTrees(sentences);
        getRules();

        trees.get(0).combine(rules);
    }

    private static void getSentences() {
        sentences.add("I sat on the chair.");
        sentences.add("Improved output quality can also be achieved by human intervention.");
    }

    private static void getRules() {
        String content = null;
        try (FileInputStream inp = new FileInputStream("D:\\Users\\Documents\\IdeaProjects\\Translator\\src\\translation\\rule\\rules.txt")) {
            Scanner in = new Scanner(new InputStreamReader(inp, "UTF-8"));

            while (in.hasNext()) {
                content = in.nextLine();
                if (content.length() > 0) {
                    rules.add(new Rule(content));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(rules.size());
    }

}
