/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package abelymiguel.miralaprima;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;

/**
 *
 * @author refusta
 */
public class GetHistory extends HttpServlet {

    private Connection _con;
    private Statement _stmt;
    private ResultSet _rs;

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            _con = Utils.getConnection();
            _stmt = _con.createStatement();
        } catch (URISyntaxException ex) {
            Logger.getLogger(GetMoza.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(GetMoza.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            out = response.getWriter();
        } catch (IOException ex) {
            Logger.getLogger(GetMoza.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.setContentType("text/javascript;charset=UTF-8");

        String country_code;
        int limit;
        String limitStr;
        country_code = request.getParameter("country_code");
        limitStr = request.getParameter("limit");

        if (limitStr != null) {
            limit = Integer.parseInt(limitStr);
        } else {
            limit = -1;
        }

        String json_str;
        JSONArray jsonArray = new JSONArray(this.getHistory(country_code, limit));
        json_str = jsonArray.toString();

        String jsonpCallback = request.getParameter("callback");
        if (jsonpCallback != null) {
            out.write(jsonpCallback + "(" + json_str + ")");
        } else {
            out.println(json_str);
        }

        try {
            _con.close();
            _stmt.close();
            _rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(GetPrima.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.close();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "This servlet returns the risk premium of a gived country";
    }// </editor-fold>

    private ArrayList<Float> getHistory(String country, int rows) {

        ArrayList<Float> respuestaJson = new ArrayList<Float>();
        try {
            String query = null;
            String limitedQuery = " LIMIT " + rows;
            if (country != null) {
                if (rows != -1) {
                    query = "SELECT prima_value FROM country_values WHERE country_code = '" + country + "' ORDER BY last_update DESC" + limitedQuery;
                } else {
                    query = "SELECT prima_value FROM country_values WHERE country_code = '" + country + "' ORDER BY last_update DESC";
                }

            }
            _rs = _stmt.executeQuery(query);

            while (_rs.next()) {
                Float prima = _rs.getFloat("prima_value");
                respuestaJson.add(prima);
            }

            _con.close();
            _stmt.close();
            _rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(GetMoza.class.getName()).log(Level.SEVERE, null, ex);
        }
        return respuestaJson;
    }
}