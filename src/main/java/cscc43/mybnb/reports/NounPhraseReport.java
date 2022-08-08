package cscc43.mybnb.reports;

import cscc43.mybnb.entities.Listing;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;

public class NounPhraseReport {
  private static final String MODEL_FILE = "en-parser-chunking.bin";

  private Connection connection;
  private ParserModel model;
  private Parser parser;

  public NounPhraseReport(Connection connection) {
    this.connection = connection;

    InputStream modelStream = null;
    try {
      modelStream = ClassLoader.getSystemResourceAsStream(MODEL_FILE);
      model = new ParserModel(modelStream);
      modelStream.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    parser = ParserFactory.create(model);
  }

  public void run() {
    try {
      createTempTable();
      loadAllPhrases();
      printTable();
    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  void createTempTable() throws SQLException {
    var s = connection.createStatement();
    s.execute("DROP TABLE IF EXISTS NP_Report");
    var stmt = connection.prepareStatement("CREATE TEMPORARY TABLE NP_Report ("
        + "Listing_ID INTEGER, "
        + "Noun_Phrase VARCHAR(64))"
    );
    stmt.executeUpdate();
    stmt.close();
  }

  public void loadAllPhrases() throws SQLException {
    var stmt = connection.prepareStatement("SELECT Text, Listing_ID "
        + "FROM Comment C JOIN Renter_Comment R ON C.Comment_ID = R.Comment_ID");
    ResultSet results = stmt.executeQuery();

    connection.setAutoCommit(false);
    while (results.next()) {
      String text = results.getString("Text");
      int listingId = results.getInt("Listing_ID");

      var insertStmt = connection.prepareStatement("INSERT INTO NP_Report(Listing_ID, Noun_Phrase) "
        + "VALUES(?, ?)");
      for (String np : getNounPhrases(text.toLowerCase())) {
        insertStmt.setInt(1, listingId);
        insertStmt.setString(2, np);
        insertStmt.addBatch();
      }
      insertStmt.executeBatch();
    }
    results.close();
    stmt.close();
    connection.setAutoCommit(true);
  }

  public List<String> getNounPhrases(String text) {
    var nps = new ArrayList<String>();
    Parse[] parses = ParserTool.parseLine(text, parser, 1);
    for (var p : parses) {
      extract(p, nps);
    }
    return nps;
  }

  public void extract(Parse parse, List<String> nps) {
    if (parse.getType().equals("NP")) {
      nps.add(parse.getCoveredText());
    }

    for (var child : parse.getChildren()) {
      extract(child, nps);
    }
  }

  public void printTable() throws SQLException {
    for (Listing lst : Listing.getAll(connection)) {
      String header = String.format("** \"%s\" - %s, %s, %s **", lst.getTitle(), lst.getStreetAddress(), lst.getCity(), lst.getCountry());

      String sql = "SELECT Noun_Phrase, COUNT(*) AS Occurrences "
          + "FROM NP_Report WHERE Listing_ID = ? "
          + "GROUP BY Noun_Phrase "
          + "ORDER BY Occurrences DESC LIMIT 10";
      var stmt = connection.prepareStatement(sql);
      stmt.setInt(1, lst.getId());

      ResultSet results = stmt.executeQuery();
      System.out.println(header);
      while (results.next()) {
        String text = results.getString(1);
        int count = results.getInt(2);

        System.out.printf("\"%s\" - %d\n", text, count);
      }

      results.close();
      stmt.close();
    }
  }
}
