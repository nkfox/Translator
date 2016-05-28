package translation.database;
import translation.tree.TranslateGrammar;

import java.sql.*;
import java.util.*;


public class Dictionary {

    private static ConnectionPool pool = ConnectionPool.getInstance();
    private ResourceBundle queries = ResourceBundle.getBundle("queries");
    private ResourceBundle files = ResourceBundle.getBundle("files");

    public static void main(String[] args) throws PersistException {
        TranslateGrammar tr = new TranslateGrammar("user",true);
        Dictionary translator = new Dictionary();
        //List<String> list = translator.getRussianTranslation("user", "сущ.");
        List<String> list1 = translator.getEndings(tr);
        System.out.println();
       /* Connection connection = pool.getConnection();
        //URL к базе состоит из протокола:подпротокола://[хоста]:[порта_СУБД]/[БД] и других_сведений
        String url = "jdbc:sqlserver://localhost:1433;databaseName=MT;integratedSecurity=true;";
        //Имя пользователя БД
        String name = "VITALIK\\VITALIK";
        //Пароль
        String password = "123456";
        try {
            //Загружаем драйвер
           *//* Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("Драйвер подключен");*//*
            //Создаём соединение
            connection = DriverManager.getConnection(url);
            System.out.println("Соединение установлено");
            Statement statement = connection.createStatement();
            //fillRGramTable(connection);
            //fillFGroupEndingTables(connection);
            //fillLexemeBasesTable(connection);
            //BASECONNECTION(connection);
            List<String> list = getRussianTranslation("battery", "сущ.", connection);
            List<String> list1 = getEndings(list.get(3), connection);
            System.out.println();
        } catch (Exception ex) {
            //выводим наиболее значимые сообщения
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }*/

    }

    public List<String> getRussianTranslation(String word, String partOfSpeech) throws PersistException {
        List<String> translations = new ArrayList<>();
        try {
            try(Connection connection = pool.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(queries.getString("QUERY.TARGET.SELECT"))) {
                preparedStatement.setString(1, word);
                preparedStatement.setString(2, partOfSpeech);
                ResultSet result = preparedStatement.executeQuery();
                while (result.next()) {
                    translations.add(result.getString(1));
                }
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

    public List<String> getEndings(TranslateGrammar translateGrammar) throws  PersistException {

        Connection connection = pool.getConnection();
        Map<Integer, String> fGroups = getRussianBase(translateGrammar.getWord(), connection);
        List<String> result = new ArrayList<>();
        if (fGroups != null) {
            for (Integer s : fGroups.keySet()) {
                Integer fgroupId = s  ;
                if(translateGrammar.getGender() == null && translateGrammar.getPartOfSpeech().equals("C")  )
                    setGender(translateGrammar, s, connection);
                String sql = generateSQL(translateGrammar);
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setInt(1, fgroupId);
                    ResultSet rs = preparedStatement.executeQuery();
                    while (rs.next()) {
                        result.add(fGroups.get(s).trim() + rs.getString(1).trim());
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
            where += "and case is NULL ";
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
}