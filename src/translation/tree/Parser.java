package translation.tree;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.trees.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Nataliia Kozoriz on 06.02.2016.
 *
 * Static methods only
 */
public class Parser {

    private static LexicalizedParser lexicalizedParser;
    private static TreebankLanguagePack treebankLanguagePack;
    private static GrammaticalStructureFactory grammaticalStructureFactory;

    static {
        String grammar = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
        String[] options = {"-maxLength", "80", "-retainTmpSubcategories", "-outputFormat", "typedDependencies"};
        lexicalizedParser = LexicalizedParser.loadModel(grammar, options);
        treebankLanguagePack = lexicalizedParser.getOp().langpack();
        grammaticalStructureFactory = treebankLanguagePack.grammaticalStructureFactory();
    }

    private Parser() {
    }

    public static List<TranslateTree> getTrees(List<String> stringSentences) {
        List<List<? extends HasWord>> sentences = parse(stringSentences);

        List<TranslateTree> trees = new ArrayList<>();
        for (List<? extends HasWord> sentence : sentences) {
            Tree parse = lexicalizedParser.parse(sentence);
            List<String> dependencies = getDependencies(parse);
            List<String> tags = getTags(parse);
            trees.add( new TranslateTree(tags, dependencies));
        }
        return trees;
    }

    private static List<List<? extends HasWord>> parse(List<String> stringSentences) {
        List<List<? extends HasWord>> sentences = new ArrayList<>();
        for (String stringSentence : stringSentences) {
            Tokenizer<? extends HasWord> toke =
                    treebankLanguagePack.getTokenizerFactory().getTokenizer(new StringReader(stringSentence));
            List<? extends HasWord> sentence = toke.tokenize();
            sentences.add(sentence);
        }
        return sentences;
    }

    private static List<String> getDependencies(Tree parse){
        GrammaticalStructure grammaticalStructure = grammaticalStructureFactory.newGrammaticalStructure(parse);
        List<TypedDependency> typedDependencies = (List<TypedDependency>) grammaticalStructure.typedDependencies();
        return typedDependencies.stream().map(TypedDependency::toString).collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<String> getTags(Tree parse){
        List<String> tags = new ArrayList<>();
        for (int i = 0; i < parse.taggedYield().size(); i++) {
            tags.add(parse.taggedYield().get(i).toString());
        }
        return tags;
    }
}
