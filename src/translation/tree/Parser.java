package translation.tree;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 *
 * Static methods only
 */
public class Parser {

    private static LexicalizedParser lp;
    private static TreebankLanguagePack tlp;
    private static GrammaticalStructureFactory gsf;

    static {
        String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
        String[] options = {"-maxLength", "80", "-retainTmpSubcategories", "-outputFormat", "typedDependencies"};
        lp = LexicalizedParser.loadModel(grammar, options);
        tlp = lp.getOp().langpack();
        gsf = tlp.grammaticalStructureFactory();
    }

    private Parser() {
    }

    public static List<TranslateTree> getTrees(List<String> stringSentences) {
        List<List<? extends HasWord>> sentences = parse(stringSentences);

        List<TranslateTree> trees = new ArrayList<>();
        for (List<? extends HasWord> sentence : sentences) {
            Tree parse = lp.parse(sentence);
            ArrayList<String> dependencies = getDependencies(parse);
            ArrayList<String> tags = getTags(parse);

            /*System.out.println(dependencies);
            System.out.println(tags);
            System.out.println();*/

            trees.add( new TranslateTree(tags, dependencies));

        }
        return trees;
    }

    private static List<List<? extends HasWord>> parse(List<String> stringSentences) {
        List<List<? extends HasWord>> tmp = new ArrayList<>();
        for (String sent2 : stringSentences) {
            Tokenizer<? extends HasWord> toke = tlp.getTokenizerFactory().getTokenizer(new StringReader(sent2));
            List<? extends HasWord> sentence2 = toke.tokenize();
            tmp.add(sentence2);
        }
        return tmp;
    }

    private static ArrayList<String> getDependencies(Tree parse){
        GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
        List<TypedDependency> tdl = (List<TypedDependency>) gs.typedDependencies();
        ArrayList<String> dependencies = new ArrayList<>();
        for (TypedDependency aTdl : tdl) {
            dependencies.add(aTdl.toString());
        }
        return dependencies;
    }

    private static ArrayList<String> getTags(Tree parse){
        ArrayList<String> tags = new ArrayList<>();
        for (int i = 0; i < parse.taggedYield().size(); i++) {
            tags.add(parse.taggedYield().get(i).toString());
        }
        return tags;
    }
}
