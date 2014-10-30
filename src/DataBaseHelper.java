import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper {
  private String jdbcUrl;
  private Connection connection;
  private String tablename = "TwitterFeed";

  public DataBaseHelper() {
    init();
    connectToDB();
  }

  private void connectToDB() {
    try {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      connection = DriverManager.getConnection(jdbcUrl);
      connection.setAutoCommit(false);
      System.out.println("Connected to DB");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void init() {
    /*String dbName = System.getProperty("RDS_DB_NAME");
    String userName = System.getProperty("RDS_USERNAME");
    String password = System.getProperty("RDS_PASSWORD");
    String hostname = System.getProperty("RDS_HOSTNAME");
    String port = System.getProperty("RDS_PORT");*/
    String dbName = "TweetDB";
    String userName = "annapurna";
    String password = "Password123";
    String hostname = "cloudassgninst.ct233hyipvfx.us-east-1.rds.amazonaws.com";
    String port = "3306";
    jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName +
        "?user=" + userName + "&password=" + password;
    System.out.println("JDBC URL to connect to :" + jdbcUrl);
  }

  public boolean batchInsert(List<TwitterStatus> twitterStatusList) {
    PreparedStatement statement = null;
    try {
      checkAndCreateTable();
      String insertStatement = "INSERT INTO " + tablename + " VALUES(?,?,?,?)";
      statement = connection.prepareStatement(insertStatement);
      for (TwitterStatus status : twitterStatusList) {
        statement.setString(1, status.getUserName());
        statement.setLong(2, status.getTweetId());
        statement.setString(3, status.getLocation());
        statement.setString(4, status.getContent());
        statement.addBatch();
      }
     
      statement.executeBatch();
      connection.commit();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    } finally {
      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return true;
  }

  private void checkAndCreateTable() throws SQLException {
    String sqlStatement = "CREATE TABLE IF NOT EXISTS "  + tablename +
        "(username VARCHAR(100), tweetID BIGINT, location VARCHAR(100), " +
        "content VARCHAR(500))";
    Statement stmt = connection.createStatement();
    stmt.executeUpdate(sqlStatement);
  }

  public List<String> getLocations() {
    Statement statement = null;
    ResultSet rs = null;
    String sqlStatement = "SELECT location FROM "+tablename;
    List<String> locations = new ArrayList<String>();
    try {
      statement = connection.createStatement();
      rs = statement.executeQuery(sqlStatement);
      while (rs.next()) {
        locations.add(rs.getString("location"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
      if (statement != null) {
        try {
          statement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
    return locations;
  }

  public List<String> getTweetAndLocations(String keyword) {
	    Statement statement = null;
	    ResultSet rs = null;
	    String sqlStatement = "SELECT content,location FROM "+tablename+" where content LIKE '%"+keyword+"%'";
	    List<String> locations = new ArrayList<String>();
	    try {
	      statement = connection.createStatement();
	      rs = statement.executeQuery(sqlStatement);
	      while (rs.next()) {
	    	  String tweet=rs.getString("content");
	    	  if(tweet.contains(keyword))
	        locations.add(rs.getString("location"));
	      }
	    } catch (SQLException e) {
	      e.printStackTrace();
	    } finally {
	      if (rs != null) {
	        try {
	          rs.close();
	        } catch (SQLException e) {
	          e.printStackTrace();
	        }
	      }
	      if (statement != null) {
	        try {
	          statement.close();
	        } catch (SQLException e) {
	          e.printStackTrace();
	        }
	      }
	    }
	    return locations;
	  }

}
