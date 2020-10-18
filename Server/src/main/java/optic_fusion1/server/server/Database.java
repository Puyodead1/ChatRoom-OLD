package optic_fusion1.server.server;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.server.utils.BCrypt;

public class Database {

  private Connection connection;

  public Database() {
    File file = new File("server", "database.db");
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException ex) {
        Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:" + file.toURI());
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    executePrepareStatement("CREATE TABLE IF NOT EXISTS `users` (`user_name` TEXT UNIQUE NOT NULL PRIMARY KEY, `uuid` BINARY(16) NOT NULL UNIQUE, `pass` CHAR(60) NOT NULL UNIQUE)");
  }

  private void executePrepareStatement(String statement) {
    try {
      connection.prepareStatement(statement).execute();
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static final String INSERT_USER = "INSERT OR IGNORE INTO `users` (`user_name`, `uuid`, `pass`) VALUES (?, ?, ?)";

  public void insertUser(String userName, UUID uniqueId, String hashedPassword) {
    try {
      PreparedStatement statement = connection.prepareStatement(INSERT_USER);
      statement.setString(1, userName);
      statement.setString(2, uniqueId.toString());
      statement.setString(3, hashedPassword);
      statement.execute();
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static final String CONTAINS_USER = "SELECT * FROM `users` WHERE `user_name` LIKE ?";

  public boolean containsUser(String userName) {
    try {
      PreparedStatement statement = connection.prepareStatement(CONTAINS_USER);
      statement.setString(1, userName);
      ResultSet resultSet = statement.executeQuery();
      return resultSet.next();
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  private static final String GET_PASSWORD = "SELECT pass FROM `users` WHERE `user_name` = ?";

  public boolean isPasswordAlreadySet(String username) {
    try {
      PreparedStatement statement = connection.prepareStatement(GET_PASSWORD);
      statement.setString(1, username);
      ResultSet resultSet = statement.executeQuery();
      return resultSet.next();
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public boolean isPasswordCorrect(String username, String password) {
    try {
      PreparedStatement statement = connection.prepareStatement(GET_PASSWORD);
      statement.setString(1, username);
      ResultSet resultSet = statement.executeQuery();
      boolean hasNext = resultSet.next();
      if (hasNext) {
        return BCrypt.checkpw(password, resultSet.getString("pass"));
      }
    } catch (SQLException ex) {
      Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

}
