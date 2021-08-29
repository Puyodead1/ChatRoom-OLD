/*
* Copyright (C) 2021 Optic_Fusion1
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package optic_fusion1.server;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import static optic_fusion1.server.Main.LOGGER;
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
        LOGGER.exception(ex);
      }
    }
    try {
      connection = DriverManager.getConnection("jdbc:sqlite:" + file.toURI());
    } catch (SQLException ex) {
      LOGGER.exception(ex);
    }
    executePrepareStatement("CREATE TABLE IF NOT EXISTS `users` (`username` TEXT NOT NULL PRIMARY KEY, `uuid` BINARY(16) NOT NULL, `pass` CHAR(60) NOT NULL, `nickname` TEXT NOT NULL DEFAULT `Client`)");
  }

  private void executePrepareStatement(String statement) {
    try {
      connection.prepareStatement(statement).execute();
    } catch (SQLException ex) {
      LOGGER.exception(ex);
    }
  }

  private static final String INSERT_USER = "INSERT OR IGNORE INTO `users` (`username`, `uuid`, `pass`) VALUES (?, ?, ?)";

  public void insertUser(String userName, UUID uniqueId, String hashedPassword) {
    try {
      PreparedStatement statement = connection.prepareStatement(INSERT_USER);
      statement.setString(1, userName);
      statement.setString(2, uniqueId.toString());
      statement.setString(3, hashedPassword);
      statement.execute();
    } catch (SQLException ex) {
      LOGGER.exception(ex);
    }
  }

  private static final String CONTAINS_USER = "SELECT * FROM `users` WHERE `username` LIKE ?";

  public boolean containsUser(String userName) {
    try {
      PreparedStatement statement = connection.prepareStatement(CONTAINS_USER);
      statement.setString(1, userName);
      ResultSet resultSet = statement.executeQuery();
      return resultSet.next();
    } catch (SQLException ex) {
      LOGGER.exception(ex);
    }
    return false;
  }

  private static final String GET_UUID = "SELECT uuid FROM `users` WHERE `username` = ?";

  public UUID getUUID(String username) {
    try {
      PreparedStatement statement = connection.prepareStatement(GET_UUID);
      statement.setString(1, username);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return UUID.fromString(resultSet.getString("uuid"));
      }
    } catch (SQLException ex) {
      LOGGER.exception(ex);
    }
    return null;
  }

  private static final String GET_PASSWORD = "SELECT pass FROM `users` WHERE `username` = ?";

  public boolean isPasswordAlreadySet(String username) {
    try {
      PreparedStatement statement = connection.prepareStatement(GET_PASSWORD);
      statement.setString(1, username);
      ResultSet resultSet = statement.executeQuery();
      return resultSet.next();
    } catch (SQLException ex) {
      LOGGER.exception(ex);
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
      LOGGER.exception(ex);
    }
    return false;
  }

  private static final String UPDATE_NICKNAME = "UPDATE users SET nickname=? WHERE uuid=?";

  public void updateNickname(UUID uniqueId, String nickname) {
    try {
      PreparedStatement statement = connection.prepareStatement(UPDATE_NICKNAME);
      statement.setString(1, nickname);
      statement.setString(2, uniqueId.toString());
      statement.execute();
    } catch (SQLException ex) {
      LOGGER.exception(ex);
    }
  }

}
