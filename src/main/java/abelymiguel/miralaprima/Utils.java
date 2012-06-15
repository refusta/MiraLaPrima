/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package abelymiguel.miralaprima;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author refusta
 */
public class Utils {

    public static Connection getConnection() throws URISyntaxException, SQLException {
        System.out.println("**************"+ System.getenv("CLEARDB_DATABASE_URL"));
        URI dbUri = new URI(System.getenv("CLEARDB_DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl, username, password);
    }
}
