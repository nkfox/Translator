package translation.database;
import translation.tree.TranslateGrammar;

import java.sql.*;
import java.util.*;

public class Dictionary {

    private static ConnectionPool pool = ConnectionPool.getInstance();
    private ResourceBundle queries = ResourceBundle.getBundle("queries");
    private ResourceBundle files = ResourceBundle.getBundle("files");

    public List<TranslateGrammar> getRussianTranslation(TranslateGrammar translateGrammar) throws PersistException {
        List<TranslateGrammar> translations = new ArrayList<>();
        try {
            try(Connection connection = pool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(queries.getString("QUERY.TARGET.SELECT"))) {
                String word = translateGrammar.getEnglishWord();
                String partofspeech = translateGrammar.getPartOfSpeech();
                preparedStatement.setString(1, translateGrammar.getEnglishWord());
                preparedStatement.setString(2, translateGrammar.getPartOfSpeech());
                ResultSet result = preparedStatement.executeQuery();
                while (result.next()) {
                    TranslateGrammar tr = translateGrammar.clone();
                    tr.setWord(result.getString(1));
                    translations.add(tr);
                }
                connection.close();
            }
        } catch (SQLException e) {
            throw new PersistException("Wrong transaction in getRussianTranslation()");
        }
        return translations;
    }

    private Map<Integer, String> getRussianBase(String word, Connection connection) throws PersistException {
        Map<Integer, String> map = new HashMap<>();
        String sql = queries.getString("QUERY.LEXEME_BASE.SELECT");
        for (int i = word.length(); i >= 0; i--) {
            String searchTarget = word.substring(0, i);
            try {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, searchTarget);
                    ResultSet rs = preparedStatement.executeQuery();
                    if (rs.next()) {
                        map = addRsToHashMap(map, rs);
                        break;
                    }
                }
            } catch (SQLException e) {
                throw new PersistException("Wrong transaction in getRussianBase()");
            }
        }

        return map;
    }

    public List<TranslateGrammar> getEndings(TranslateGrammar translateGrammar) throws  PersistException {

        Connection connection = pool.getConnection();
        Map<Integer, String> fGroups = getRussianBase(translateGrammar.getWord(), connection);
        List<TranslateGrammar> result = new ArrayList<>();
        if (fGroups != null) {
            for (Integer s : fGroups.keySet()) {
                Integer fgroupId = s  ;
                if(translateGrammar.getGender() == null && translateGrammar.getPartOfSpeech().equals("с")  )
                    setGender(translateGrammar, s, connection);
                String sql = generateSQL(translateGrammar);
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, fgroupId);
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        TranslateGrammar tr = translateGrammar.clone();
                        tr.setWord(fGroups.get(s).trim() + rs.getString(1).trim());
                        result.add(tr);
                    }
                } catch (SQLException e) {
                    throw new PersistException(e);
                }
            }
        }

        return result;

    }

    public static Map<Integer, String> addRsToHashMap(Map<Integer, String> map ,ResultSet rs) throws SQLException {
        do {
            map.put(rs.getInt(2), rs.getString(1));
        } while (rs.next());
        return map;
    }

    private String generateSQL(TranslateGrammar translateGrammar) {

        String select = "select Ending.Value from Ending    " +
                "                       join grammar_ending_fgroup on grammar_ending_fgroup.Ending_id = Ending.id" +
                "                       join Grammar on grammar_ending_fgroup.Grammar_id = Grammar.id ";
        String where = "where  grammar_ending_fgroup.Fgroup_id = ? ";
        /*if (translateGrammar.getPartOfSpeech() != null) {
            select += "join PartOfSpeech on PartOfSpeech.id = Grammar.partOfSpeech ";
            where += "and PartOfSpeech.value =  '";
            where += translateGrammar.getPartOfSpeech() + "' ";
        }*/
        if (translateGrammar.getGender() != null) {
            select += "join Gender on Gender.id = Grammar.gender ";
            where += "and Gender.value = '";
            where += translateGrammar.getGender() + "' ";
        }
        else {
            where += "and gender is NULL ";
        }
        if (translateGrammar.getNum() != null){
            select += "join Number on Number.id = Grammar.number ";
            where += "and Number.value =  '";
            where += translateGrammar.getNum() + "' ";
        }
        else {
            where += "and number is NULL ";
        }
        if (translateGrammar.getCase() != null) {
            select += "join [Case] on [Case].id = Grammar.[case] ";
            where += "and [Case].value = '";
            where += translateGrammar.getCase() + "' ";
        }
        else {
            where += "and [case] is NULL ";
        }
        if (translateGrammar.getTense() != null){
            select += "join Tense on Tense.id = Grammar.tense ";
            where += "and Tense.value = '";
            where += translateGrammar.getTense() + "' ";
        }
        else {
            where += "and tense is NULL ";
        }
        if(translateGrammar.getPerson() != null){
            select += "join Person on Person.id = Grammar.person ";
            where += "and Person.value = '";
            where += translateGrammar.getPerson() + "' ";
        }
        else {
            where += "and person is NULL ";
        }
        if (translateGrammar.getAnimacy()!= null){
            select += "join Animacy on Animacy.id = Grammar.animacy ";
            where += "and Animacy.value = '";
            where += translateGrammar.getAnimacy() + "' ";
        }
        else {
            where += "and animacy is NULL ";
        }
        if (translateGrammar.getAspect() != null){
            select += " join Aspect on Aspect.id = Grammar.aspect ";
            where += " and Aspect.value = '";
            where += translateGrammar.getAspect() + "' ";
        }
        else {
            where += "and aspect is NULL ";
        }
        if (translateGrammar.getVoice() != null) {
            select += "join Voice on Voice.id = Grammar.voice ";
            where += "and Voice.value = '";
            where += translateGrammar.getVoice() +"' ";
        }
        else {
            where += "and voice is NULL ";
        }
        return select + where;
    }

    private void setGender(TranslateGrammar translateGrammar, int fgroup, Connection connection) throws PersistException {
        String sql = " select Gender.value from Gender    " +
                "               join Grammar on Grammar.gender = Gender.id " +
                "               join grammar_ending_fgroup on grammar_ending_fgroup.Grammar_id = Grammar.id " +
                "               where grammar_ending_fgroup.Fgroup_id = ? ";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, fgroup);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            String str = rs.getString(1).trim();
            translateGrammar.setGender(str);
        } catch (SQLException e) {
            throw new PersistException(e);
        }
    }

    public List<TranslateGrammar> translate(TranslateGrammar word){
        List<TranslateGrammar> list = null;
        try {
            list = getRussianTranslation(word);
        } catch (PersistException e) {}
        List<TranslateGrammar> list1 = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            try {
                list1.addAll(getEndings(list.get(i)));
            } catch (PersistException e) {}
        }

        System.out.println(list.size()+" "+list1.size());

        for(TranslateGrammar s: list1)
            System.out.println(s.getWord()+" "+ s.getGender());

        return list1;
    }
}