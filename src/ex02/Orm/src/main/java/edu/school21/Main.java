package edu.school21;

import com.zaxxer.hikari.HikariDataSource;
import edu.school21.Annotations.OrmManager;
import edu.school21.Classes.User;
import edu.school21.DataBaseLoader.DataBaseLoader;

import java.sql.SQLException;


public class Main {
    public static void main(String[] args) {
        try {
            OrmManager manager = new OrmManager(DataBaseLoader.connectToDb());
            User user1 = new User(1, "Mark", "Litinsky", 179);
            manager.createTable();
            manager.save(user1);

            User findUser = manager.findById(1L, User.class);
            System.out.println(findUser);

            User updateUser1 = new User(1, "Mark", "Litinsky", 180);
            manager.update(updateUser1);

            findUser = manager.findById(1L, User.class);
            System.out.println(findUser);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}