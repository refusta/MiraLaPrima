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
        Float prima_delta;
        Float prima_percent;
        
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
