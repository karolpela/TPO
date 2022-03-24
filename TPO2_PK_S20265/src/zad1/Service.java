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
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

import com.google.gson.Gson;

import zad1.CurrencyInfo.CurrencyInfo;

public class Service {
	private static final String API_KEY = "9e3b822f0820bf7d604c31b366029096";

	private Locale locale;
	private Currency currency;

	public Service(String country) {
		this.locale = Arrays.stream(Locale.getAvailableLocales())
				.filter(l -> l.getDisplayCountry().equals(country)).findFirst().get();
		currency = Currency.getInstance(locale);
	}

	public Double getRateFor(String currencyCode) {
		return (1 / getNBPRate() * getNBPRate(currencyCode));
	}

	public Double getNBPRate() {
		if (currency.getCurrencyCode().equals("PLN"))
			return 1.0;
		String nbpJson = "";
		try {
			URL nbpCall = new URL("http://api.nbp.pl/api/exchangerates/rates/a/"
					+ currency.getCurrencyCode() + "/?format=json");
			BufferedReader in = new BufferedReader(
					new InputStreamReader(nbpCall.openStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null)
				nbpJson += line;
		} catch (MalformedURLException e) {
			System.out.println("[!] Malformed URL");
		} catch (IOException e) {
			System.out.println("[!] Error opening stream");
		} finally {
			URL nbpCall;
			try {
				nbpCall = new URL("http://api.nbp.pl/api/exchangerates/rates/b/" 
						+ currency.getCurrencyCode() + "/?format=json");
				BufferedReader in = new BufferedReader(new InputStreamReader(nbpCall.openStream(), "UTF-8"));
				nbpJson = "";
				String line;
				while ((line = in.readLine()) != null)
					nbpJson += line;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Gson gson = new Gson();
		CurrencyInfo ci = gson.fromJson(nbpJson, CurrencyInfo.class);
		return 1.0 / ci.getRates().get(0).getMid();
	}

	public static Double getNBPRate(String currencyCode) {
		if (currencyCode.equals("PLN"))
			return 1.0;
		String nbpJson = "";
		try {
			URL nbpCall = new URL("http://api.nbp.pl/api/exchangerates/rates/a/" 
				+ currencyCode + "/?format=json");
			BufferedReader in = new BufferedReader(new InputStreamReader(nbpCall.openStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null)
				nbpJson += line;
		} catch (MalformedURLException e) {
			System.out.println("[!] Malformed URL");
		} catch (IOException e) {
			System.out.println("[!] Error opening stream");
		} finally {
			URL nbpCall;
			try {
				nbpCall = new URL("http://api.nbp.pl/api/exchangerates/rates/b/" 
						+ currencyCode + "/?format=json");
				BufferedReader in = new BufferedReader(new InputStreamReader(nbpCall.openStream(), "UTF-8"));
				nbpJson = "";
				String line;
				while ((line = in.readLine()) != null)
					nbpJson += line;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Gson gson = new Gson();
		CurrencyInfo ci = gson.fromJson(nbpJson, CurrencyInfo.class);
		return 1.0 / ci.getRates().get(0).getMid();
	}

	public String getWeather(String city) {
		String weatherJson = "";
		try {
			URL weatherCall = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city
					+ "," + locale.getCountry()
					+ "&units=metric" + "&appid=" + API_KEY);
			BufferedReader in = new BufferedReader(new InputStreamReader(weatherCall.openStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null)
				weatherJson += line;
		} catch (MalformedURLException e) {
			System.out.println("[!] Malformed URL");
		} catch (IOException e) {
			System.out.println("[!] Error opening stream");
		}
		return weatherJson;
	}
}
