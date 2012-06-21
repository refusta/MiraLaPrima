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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 *
 * @author refusta
 */
public class SetMoza extends HttpServlet {

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
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException ex) {
            Logger.getLogger(GetMoza.class.getName()).log(Level.SEVERE, null, ex);
        }
        response.setContentType("text/javascript;charset=UTF-8");

        String url_moza;
        url_moza = request.getParameter("url_moza");

        String country_code;
        country_code = request.getParameter("country_code");

        if (country_code == null) {
            country_code = "ALL";
        }

        JSONObject jsonObject;
        String json_str;

        jsonObject = new JSONObject(this.setMozaInDB(url_moza, country_code));
        json_str = jsonObject.toString();

        String jsonpCallback = request.getParameter("callback");
        if (jsonpCallback != null) {
            out.write(jsonpCallback + "(" + json_str + ")");
        } else {
            out.println(json_str);
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
        return "Short description";
    }// </editor-fold>

    private HashMap<String, String> setMozaInDB(String url_moza, String country_code) {

        HashMap<String, String> respuestaJson = new HashMap<String, String>();
        try {
            Timestamp date_added = Utils.getTimestamp();
            String provider;
            if (url_moza.contains("http://")) {
                provider = url_moza.replace("http://", "");
                provider = "http://" + provider.substring(0, provider.indexOf("/"));
            } else {
                provider = "Provider not found";
            }
            if (checkUrlMoza(url_moza) && checkMimeUrlMoza(url_moza)) {
                Utils.getConnection().createStatement().execute("INSERT INTO `photos` (`url_prima`, `provider`, `approved`, `country_code`, `date_added`) VALUES ('" + url_moza + "', '" + provider + "', 0, '" + country_code + "', '" + date_added + "')");
                respuestaJson.put("result", "OK");
            } else {
                respuestaJson.put("result", "ERROR");
            }
        } catch (SQLException ex) {
            Logger.getLogger(GetMoza.class.getName()).log(Level.WARNING, null, ex);
            respuestaJson.put("result", "ERROR");
        } catch (URISyntaxException ex) {
            Logger.getLogger(GetMoza.class.getName()).log(Level.SEVERE, null, ex);
            respuestaJson.put("result", "ERROR");
        }
        return respuestaJson;
    }

    private Boolean checkUrlMoza(String url_moza) {
        Boolean result;
        URL url;
        HttpURLConnection conexion;
        try {
            url = new URL(url_moza);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.connect();
            BufferedReader respContent;
            respContent = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            respContent.close();
            conexion.disconnect();
            result = true;
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            result = false;
        }

        return result;
    }

    private Boolean checkMimeUrlMoza(String url_moza) {
        String type;
        Boolean result = false;
        URL url;
        URLConnection uc = null;
        try {
            url = new URL(url_moza);
            uc = url.openConnection();
        } catch (IOException ex) {
            Logger.getLogger(SetMoza.class.getName()).log(Level.SEVERE, null, ex);
            result = false;
        }
        type = uc.getContentType();
        if (type.equals("image/gif") || type.equals("image/jpeg")){
            result = true;
        }
//        System.out.println(type);
        return result;
    }
}