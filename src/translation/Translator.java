package translation;

import translation.rule.Rule;
import translation.tree.Parser;
import translation.tree.TranslateTree;

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

        String sRule = "VBD3(nsubj.DT1.this(amod.VBG2))#С1.пад:им&чис:Y&род:Z,Г3.чис:y&род:z&зал:дей&вр:прош&асп:сов(VBD)$М1.пад:им&чис:y&род:z,С2.пад:им&чис:Y&род:Z,Г3.чис:y&род:z&зал:дей&вр:прош&асп:нес(VBD)";
        Rule rule = new Rule(sRule);

    }

}
