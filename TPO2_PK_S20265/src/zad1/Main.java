/**
 *
 * @author Peła Karol S20265
 *
 */

package zad1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import com.google.gson.Gson;

import zad1.WeatherInfo.WeatherInfo;

public class Main {
	public static void main(String[] args) {
		// Service s = new Service("Canada");
		// String weatherJson = s.getWeather("Toronto");
		// Gson gson = new Gson();
		// WeatherInfo wi = gson.fromJson(weatherJson, WeatherInfo.class);
		// Double rate1 = s.getRateFor("USD");
		// Double rate2 = s.getNBPRate();
		// część uruchamiająca GUI

		SwingUtilities.invokeLater(() -> createGUI());
	}

	public static void createGUI() {
		JFrame jf = new JFrame("City info");

		JPanel infoPanel = new JPanel(new GridLayout(0, 1));
		WeatherPanel weatherPanel = new WeatherPanel();
		CurrencyPanel ratePanel = new CurrencyPanel("Currency rate");
		CurrencyPanel NBPPanel = new CurrencyPanel("PLN rate");

		Service s = new Service("Canada");
		String weatherJson = s.getWeather("Toronto");
		Gson gson = new Gson();
		WeatherInfo wi = gson.fromJson(weatherJson, WeatherInfo.class);

		InputPanel inputPanel = new InputPanel();
		inputPanel.setWeatherPanel(weatherPanel);
		inputPanel.setRatePanel(ratePanel);
		inputPanel.setNBPPanel(NBPPanel);
		
		infoPanel.add(inputPanel);
		infoPanel.add(weatherPanel);
		infoPanel.add(ratePanel);
		infoPanel.add(NBPPanel);

		infoPanel.setPreferredSize(new Dimension(150, 0));
		jf.add(infoPanel, BorderLayout.LINE_START);
		weatherPanel.updateData(wi);
		ratePanel.updateData(s.getRateFor("USD"));
		NBPPanel.updateData(s.getNBPRate());

		JFXPanel fxPanel = new JFXPanel();
		jf.add(fxPanel, BorderLayout.CENTER);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				initFX(fxPanel);
				inputPanel.setWebEngine(webEngine);
			}
		});
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(1280, 720);
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
	}

	private static WebEngine webEngine;

	private static void initFX(JFXPanel fxPanel) {
		WebView browser = new WebView();
		Scene scene = new Scene(browser);
		fxPanel.setScene(scene);
		webEngine = browser.getEngine();
	}
}
