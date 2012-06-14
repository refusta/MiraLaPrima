/*
=============
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package abelymiguel.miralaprima;

import java.io.File;
import java.io.IOException;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author Abel Toledano Andrés
 */
public class DBConnect {

    private String _dbUri;
    private String _dbUser;
    private String _dbPassword;
    private static DBConnect dbconnect;

    private DBConnect(String dbConfFilePath) throws JDOMException, IOException {

        SAXBuilder builder = new SAXBuilder();
        File conf = new File(dbConfFilePath);
        Document doc = builder.build(conf);
        Element root = doc.getRootElement();

        this._dbUri = root.getChild("dbUri").getTextTrim();
        this._dbUser = root.getChild("dbUser").getTextTrim();
        this._dbPassword = root.getChild("dbPassword").getTextTrim();
    }

    public static DBConnect getInstance(String dbConfFilePath) throws JDOMException, IOException {
        
        if (dbconnect == null) {
            dbconnect = new DBConnect(dbConfFilePath);
        }
        return dbconnect;
    }

    public ResultSet doQuery(String query) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(_dbUri, _dbUser, _dbPassword);
        Statement stm = con.createStatement();
        ResultSet res = stm.executeQuery(query);
        return res;
    }
}
