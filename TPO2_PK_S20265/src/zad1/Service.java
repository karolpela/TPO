/**
 *
 *  @author Peła Karol S20265
 *
 */

package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class Service {
    private static final String API_KEY = "9e3b822f0820bf7d604c31b366029096";

    private String country;
    private Locale locale;

    public Service(String country) {
        this.country = country;
    }

    public String getWeather(String city) {
        String s = "";
        try {
            URL weatherCall = new URL(
                "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&units=metric" + "&appid=" + API_KEY
            );
            System.out.println(weatherCall);
            BufferedReader in = new BufferedReader(new InputStreamReader(weatherCall.openStream(), "UTF-8"));
            String line;
            while((line = in.readLine()) != null) 
                s += line;
        } catch (MalformedURLException e) {
            System.out.println("[!] Malformed URL");
        } catch (IOException e) {
            System.out.println("[!] Error opening stream");
        }
        System.out.println(s);
        return s;    
    }
}
