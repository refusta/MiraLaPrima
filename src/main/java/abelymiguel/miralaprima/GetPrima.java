/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package abelymiguel.miralaprima;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author refusta
 */
public class GetPrima extends HttpServlet {

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
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/javascript;charset=UTF-8");

        String country_code;
        country_code = request.getParameter("country_code");

        JSONObject jsonObject;
        JSONArray jsonArray;
        String json_str;
        if (country_code != null) {
            jsonObject = new JSONObject(this.getCountry(country_code));
            json_str = jsonObject.toString();
        } else {
            jsonArray = new JSONArray(this.getAllCountries());
            json_str = jsonArray.toString();
        }

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
        return "This servlet returns the risk premium of a gived country";
    }// </editor-fold>

    private HashMap<String, Object> getCountry(String country_code) {
        HashMap<String, Object> respuestaJson = new HashMap<String, Object>();

        String country_prime;
        String name;
        if (country_code.equals("ES")) {
            country_prime = "!SPN:IND";
            name = "España";
        } else if (country_code.equals("IT")) {
            country_prime = "!IT10:IND";
            name = "Italia";
        } else if (country_code.equals("GR")) {
            country_prime = "!GRK:IND";
            name = "Grecia";
        } else if (country_code.equals("PT")) {
            country_prime = "!PORT10:IND";
            name = "Portugal";
        } else {
            return respuestaJson;
        }

        Float prima_value;
        Float prima_delta = null;
        Float prima_percent = null;

        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.bloomberg.com/quote/" + country_prime).get();
        } catch (IOException ex) {
            Logger.getLogger(GetPrima.class.getName()).log(Level.SEVERE, null, ex);
        }
        Element riskPremium = doc.select(".price").last();
//        System.out.println("Prima: " + riskPremium.text());
        prima_value = Float.valueOf(riskPremium.text().replace(",", "")).floatValue();

        Elements riskPremiumsUp = doc.select(".trending_up");
        Elements riskPremiumsDown = doc.select(".trending_down");
//        System.out.println("Trending: " + riskPremiumsUp.text());
//        System.out.println("Trending: " + riskPremiumsDown.text());

        if (!riskPremiumsUp.text().equals("")) {
            String delta = riskPremiumsUp.text();
            prima_delta = Float.valueOf(delta.substring(0, delta.indexOf(" ")).replace(",", "")).floatValue();
//            System.out.println("Delta: " + prima_delta);

            String percent = riskPremiumsUp.text();
            prima_percent = Float.valueOf(percent.substring(percent.indexOf(" ") + 1, percent.length() - 1)).floatValue();
//            System.out.println("Percent: " + prima_percent);
        } else if (!riskPremiumsDown.text().equals("")) {
            String delta = riskPremiumsDown.text();
            prima_delta = Float.valueOf(delta.substring(0, delta.indexOf(" ")).replace(",", "")).floatValue();
            prima_delta = prima_delta * -1;
//            System.out.println("Delta: " + prima_delta);

            String percent = riskPremiumsDown.text();
            prima_percent = Float.valueOf(percent.substring(percent.indexOf(" ") + 1, percent.length() - 1)).floatValue();
            prima_percent = prima_percent * -1;
//            System.out.println("Percent: " + prima_percent);
        }

        respuestaJson.put("name", name);
        respuestaJson.put("country_code", country_code);
        respuestaJson.put("prima_value", prima_value);
        respuestaJson.put("prima_delta", prima_delta);
        respuestaJson.put("prima_percent", prima_percent);

        return respuestaJson;
    }

    private ArrayList<HashMap<String, Object>> getAllCountries() {
        ArrayList<HashMap<String, Object>> respuestaJson = new ArrayList<HashMap<String, Object>>();
        ArrayList<String> country_codes = new ArrayList<String>();
        country_codes.add("ES");
        country_codes.add("PT");
        country_codes.add("IT");
        country_codes.add("GR");

        for (String country : country_codes) {
            respuestaJson.add(this.getCountry(country));
        }
        return respuestaJson;
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new GetPrima()), "/getPrima");
        context.addServlet(new ServletHolder(new GetMoza()), "/getMoza");
        server.start();
        server.join();
    }
}
