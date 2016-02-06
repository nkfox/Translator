package translation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 */
public class Translator {
    public static void main(String[] args) {

        List<String> sentences = new ArrayList<>();
        sentences.add("I sat on the chair.");
        sentences.add("Improved output quality can also be achieved by human intervention.");
        List<TranslateTree> trees = Parser.getTrees(sentences);
    }

}
