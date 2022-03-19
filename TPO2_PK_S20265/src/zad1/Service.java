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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Service {
    private static final String API_KEY = "9e3b822f0820bf7d604c31b366029096";
    
    private Locale locale;
    private Currency currency;

    public Service(String country) {
        this.locale = Arrays.stream(Locale.getAvailableLocales())
            .filter(l -> l.getDisplayCountry().equals(country))
            .findFirst()
            .get();
        System.out.println(locale);
        currency = Currency.getInstance(locale);
        System.out.println(currency.getCurrencyCode());
    }

    public String getWeather(String city) {
        String weatherJson = "";
        try {
            URL weatherCall = new URL(
                "https://api.openweathermap.org/data/2.5/weather?q="
                + city + "&units=metric" + "&appid=" + API_KEY
            );
            BufferedReader in = new BufferedReader(new InputStreamReader(weatherCall.openStream(), "UTF-8"));
            String line;
            while((line = in.readLine()) != null) 
                weatherJson += line;
        } catch (MalformedURLException e) {
            System.out.println("[!] Malformed URL");
        } catch (IOException e) {
            System.out.println("[!] Error opening stream");
        }
        return weatherJson;    
    }
}
