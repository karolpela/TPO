/**
 *
 * @author Peła Karol S20265
 *
 */

package zad1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import com.google.gson.Gson;

import zad1.CurrencyTable.CurrencyTable;
import zad1.CurrencyTable.Rate;

public class Service {
	private static final String API_KEY = "9e3b822f0820bf7d604c31b366029096";

	public static final String URL_ERROR_MESSAGE = "[!] Malformed URL";
	public static final String STREAM_ERROR_MESSAGE = "[!] Error opening stream";


	private Locale locale;
	private Currency currency;

	public Service(String country) {
		Optional<Locale> ol = Arrays.stream(Locale.getAvailableLocales())
			.filter(l -> l.getDisplayCountry().equals(country))
			.findFirst();
		if (ol.isPresent()) {
			this.locale = ol.get();
		}
		currency = Currency.getInstance(locale);
	}

	public Double getRateFor(String currencyCode) {
		return (1 / getNBPRate() * getNBPRate(currencyCode));
	}

	public static String jsonFromUrl(URL url) throws IOException {
		StringBuilder jsonBuilder = new StringBuilder();
		String line;
		try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
			while ((line = in.readLine()) != null) {
				jsonBuilder.append(line);
			}
		}
		return jsonBuilder.toString();
	}

	public static URL getNbpCall(char table) throws MalformedURLException {
		return new URL("http://api.nbp.pl/api/exchangerates/tables/"+ table + "?format=json");
	} 

	public Double getNBPRate() {
		double result = 0.0;
		String code = currency.getCurrencyCode();
		if (code.equals("PLN"))
			return 1.0;
		try {
			URL nbpCall = getNbpCall('A');

			Gson gson = new Gson();
			String json = jsonFromUrl(nbpCall);
			CurrencyTable[] ct = gson.fromJson(json, CurrencyTable[].class);
			Optional<Rate> rate = ct[0].getRates().stream()
					.filter(r -> r.getCode().equals(code))
					.findFirst();
			if (!rate.isPresent()) {
				nbpCall = getNbpCall('B');
				json = jsonFromUrl(nbpCall);
				ct = gson.fromJson(json, CurrencyTable[].class); //table because of misformatted json
				rate = ct[0].getRates().stream()
					.filter(r -> r.getCode().equals(code))
					.findFirst();
			}
			if (rate.isPresent()) {
				result = rate.get().getMid();
			}
					
		} catch (MalformedURLException e) {
			System.out.println(URL_ERROR_MESSAGE);
		} catch (IOException e) {
			System.out.println(STREAM_ERROR_MESSAGE);
		}
		return result;
	}

	public static Double getNBPRate(String code) {
		double result = 0.0;
		if (code.equals("PLN"))
			return 1.0;
		try {
			URL nbpCall = getNbpCall('A');

			Gson gson = new Gson();
			String json = jsonFromUrl(nbpCall);
			CurrencyTable[] ct = gson.fromJson(json, CurrencyTable[].class);
			Optional<Rate> rate = ct[0].getRates().stream()
					.filter(r -> r.getCode().equals(code))
					.findFirst();
			if (!rate.isPresent()) {
				nbpCall = getNbpCall('B');
				json = jsonFromUrl(nbpCall);
				ct = gson.fromJson(json, CurrencyTable[].class);
				rate = ct[0].getRates().stream()
					.filter(r -> r.getCode().equals(code))
					.findFirst();
			}
			if (rate.isPresent()) {
				result = rate.get().getMid();
			}
					
		} catch (MalformedURLException e) {
			System.out.println(URL_ERROR_MESSAGE);
		} catch (IOException e) {
			System.out.println(STREAM_ERROR_MESSAGE);
		}

		if (result != 0) {
			return 1.0 / result;
		} else {
			return 0.0;
		}
	}

	public String getWeather(String city) {
		String weatherJson = "";
		try {
			URL weatherCall = new URL("https://api.openweathermap.org/data/2.5/weather?q="
			+ city + "," + locale.getCountry()
			+ "&units=metric" + "&appid=" + API_KEY);
			weatherJson = jsonFromUrl(weatherCall);
		} catch (MalformedURLException e) {
			System.out.println(URL_ERROR_MESSAGE);
		} catch (IOException e) {
			System.out.println(STREAM_ERROR_MESSAGE);
		}
		return weatherJson;
	}
}
