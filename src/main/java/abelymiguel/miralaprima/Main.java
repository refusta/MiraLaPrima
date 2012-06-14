/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package abelymiguel.miralaprima;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author refusta
 */
public class Main {

    public static void main(String args[]) throws IOException {
        Float prima_value;
        Float prima_delta = null;
        Float prima_percent = null;

        Document doc = Jsoup.connect("http://www.bloomberg.com/quote/!SPN:IND").get();
        Element riskPremium = doc.select(".price").last();
        System.out.println("Prima: " + riskPremium.text());
        prima_value = Float.valueOf(riskPremium.text().replace(",", "")).floatValue();

        Elements riskPremiumsUp = doc.select(".trending_up");
        Elements riskPremiumsDown = doc.select(".trending_down");
        System.out.println("Trending: " + riskPremiumsUp.text());
        System.out.println("Trending: " + riskPremiumsDown.text());

        if (!riskPremiumsUp.text().equals("")) {
            String delta = riskPremiumsUp.text();
            prima_delta = Float.valueOf(delta.substring(0, delta.indexOf(" ")).replace(",", "")).floatValue();
            System.out.println("Delta: " + prima_delta);

            String percent = riskPremiumsUp.text();
            prima_percent = Float.valueOf(percent.substring(percent.indexOf(" ") + 1, percent.length() - 1)).floatValue();
            System.out.println("Percent: " + prima_percent);
        } else if (!riskPremiumsDown.text().equals("")) {
            String delta = riskPremiumsDown.text();
            prima_delta = Float.valueOf(delta.substring(0, delta.indexOf(" ")).replace(",", "")).floatValue();
            prima_delta = prima_delta * -1;
            System.out.println("Delta: " + prima_delta);

            String percent = riskPremiumsDown.text();
            prima_percent = Float.valueOf(percent.substring(percent.indexOf(" ") + 1, percent.length() - 1)).floatValue();
            prima_percent = prima_percent * -1;
            System.out.println("Percent: " + prima_percent);
        }
    }
}
