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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Service {

    public static Map<String, String> countryMap;

    private String country;
    private String countryCode;
    private Locale locale;

    public Service(String country) {
        if (Service.countryMap == null)
            populateMap();
        this.country = country;
        Map<String,String> temp = Service.countryMap;
        this.locale = new Locale(Service.countryMap.get(country));
        this.countryCode = locale.getCountry();
    }

    public String getWeather(String city) {
        String s = "";
        try {
            URL weatherCall = new URL(
                "https://api.openweathermap.org/data/2.5/weather?id="
                + city + "," + countryCode
            );
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

    private static void populateMap() {
        countryMap = new HashMap<>();
        // https://stackoverflow.com/a/14155186/17862415
        for (String country : Locale.getISOCountries()) {
            Locale l = new Locale("", country);
            countryMap.put(l.getDisplayCountry(), country);
        }
    }
}
