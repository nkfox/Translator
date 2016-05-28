package translation.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Scanner;

public class DatabaseCreator {


    private static ConnectionPool pool = ConnectionPool.getInstance();
    private ResourceBundle queries = ResourceBundle.getBundle("queries");
    private ResourceBundle files = ResourceBundle.getBundle("files");

    public void fillFGroupEndingTables() throws FileNotFoundException, PersistException {

        Connection connection = pool.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            connection.setAutoCommit(false);
            Scanner sc = new Scanner(new File(files.getString("FGROUP.INSERT")));
            sc.nextLine();
            int number = sc.nextInt();
            sc.nextLine();

            for (int i = 0; i < number; i++) {
                insertFGroupNumber(connection, i);
                int fGroupId = getFGroupId(connection, i);

                String[] line = sc.nextLine().split("%");
                for (int j = 1; j < line.length; j++) {

                    String tagEnd[] = line[j].split("\\*");
                    ResultSet resultSetOfEndings = getResultSetOfEndings(connection, tagEnd[0]);

                    if (!resultSetOfEndings.next()) {
                        insertEnding(connection, tagEnd[0]);
                    }

                    ResultSet resultSetOfFGroupIds = getResultSetOfFGroupIds(connection, fGroupId, tagEnd);

                    if (!resultSetOfFGroupIds.next()) {
                        int endingId = getEndingId(connection, tagEnd[0]);
                        ResultSet resultSetOfGrammars = getResultSetOfGrammars(connection, tagEnd[1]);
                        if (resultSetOfGrammars.next()) {
                            int tagId = resultSetOfGrammars.getInt("id");
                            insertGrammarEndingFGroup(connection, fGroupId, endingId, tagId);
                        }
                    }
                }
                connection.commit();
            }
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        finally {
            try {
                connection.close();
                preparedStatement.close();
            } catch (SQLException e) {
                throw new PersistException("Closing connection problem in fillFGroupEndingTables()");
            }
        }
    }

    private void insertGrammarEndingFGroup(Connection connection, int fGroupId, int endingId, int tagId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(queries.getString("QUERY.GRAMMAR_ENDING_FGROUP.INSERT"));
        preparedStatement.setInt(1, endingId);
        preparedStatement.setInt(2, fGroupId);
        preparedStatement.setInt(3, tagId);
        preparedStatement.executeUpdate();
    }

    private ResultSet getResultSetOfGrammars(Connection connection, String tag) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(queries.getString("QUERY.GRAMMAR.SELECT"));
        preparedStatement.setString(1, tag);
        preparedStatement.executeQuery();
        return preparedStatement.executeQuery();
    }

    private int getEndingId(Connection connection, String tag) throws SQLException {
        ResultSet resultSetOfEndings1 = getResultSetOfEndings(connection, tag);
        resultSetOfEndings1.next();
        return resultSetOfEndings1.getInt("id");
    }

    private ResultSet getResultSetOfFGroupIds(Connection connection, int fGroupId, String[] tagEnd) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(queries.getString("QUERY.GRAMMAR_ENDING_FGROUP.SELECT"));
        preparedStatement.setString(1, tagEnd[0]);
        preparedStatement.setString(2, tagEnd[1]);
        preparedStatement.setInt(3, fGroupId);
        return preparedStatement.executeQuery();
    }

    private void insertEnding(Connection connection,String tag) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(queries.getString("QUERY.ENDING.INSERT"));
        preparedStatement.setString(1, tag);
        preparedStatement.executeUpdate();
    }

    private ResultSet getResultSetOfEndings(Connection connection, String tag) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(queries.getString("QUERY.ENDING.SELECT"));
        preparedStatement.setString(1, tag);
        return preparedStatement.executeQuery();
    }

    private void insertFGroupNumber(Connection connection, int i) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(queries.getString("QUERY.FGROUP.INSERT"));
        preparedStatement.setInt(1, i + 1);
        preparedStatement.executeUpdate();
    }

    private int getFGroupId(Connection connection, int i) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(queries.getString("QUERY.FGROUP.SELECT"));
        preparedStatement.setInt(1, i + 1);
        ResultSet result = preparedStatement.executeQuery();
        result.next();
        return result.getInt("id");
    }

    public void fillLexemeBasesTable() throws SQLException {
        try {
            try(Connection connection = pool.getConnection()) {
                Scanner sc = new Scanner(new File(files.getString("BASES.INSERT")));
                sc.nextLine();
                while (sc.hasNext()) {
                    String[] line = sc.nextLine().split(" ");
                    int tmp = Integer.valueOf(line[1]) + 1;
                    int fGroupId = getFGroupId(connection, tmp);
                    insertLexemeBase(connection, line[0], fGroupId);
                }
            } catch (FileNotFoundException e) {
                throw new PersistException("Wrong file path in lexeme bases insert");
            }
        } catch (PersistException e) {
            e.printStackTrace();
        }


    }

    private void insertLexemeBase(Connection connection, String s, int fGroupId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(queries.getString("QUERY.LEXEME_BASE.INSERT"));
        preparedStatement.setString(1, s);
        preparedStatement.setInt(2, fGroupId);
        preparedStatement.executeUpdate();
    }

}
