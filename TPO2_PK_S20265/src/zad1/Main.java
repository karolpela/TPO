/**
 *
 *  @author Peła Karol S20265
 *
 */

package zad1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import zad1.WeatherInfo.WeatherInfo;

public class Main {
  public static void main(String[] args) {
    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    WeatherInfo wi = gson.fromJson(weatherJson, WeatherInfo.class);
    System.out.println(gson.toJson(wi));
    // Double rate1 = s.getRateFor("USD");
    // Double rate2 = s.getNBPRate();
    // ...
    // część uruchamiająca GUI
  }
}
