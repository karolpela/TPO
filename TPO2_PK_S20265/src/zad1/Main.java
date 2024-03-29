/**
 *
 * @author Peła Karol S20265
 *
 */

package zad1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Main {
	public static void main(String[] args) {
		// Lines below are for SDKP tests 

		//Service s = new Service("Poland");
		//String weatherJson = s.getWeather("Warsaw");
		//Double rate1 = s.getRateFor("USD");
		//Double rate2 = s.getNBPRate();

		
		// ...
		// część uruchamiająca GUI

		SwingUtilities.invokeLater(Main::createGUI);
	}

	public static void createGUI() {
		JFrame jf = new JFrame("City info");

		JPanel infoPanel = new JPanel(new GridBagLayout());
		WeatherPanel weatherPanel = new WeatherPanel();
		CurrencyPanel ratePanel = new CurrencyPanel("Currency rate");
		CurrencyPanel nbpPanel = new CurrencyPanel("PLN rate");

		InputPanel inputPanel = new InputPanel();
		inputPanel.setWeatherPanel(weatherPanel);
		inputPanel.setRatePanel(ratePanel);
		inputPanel.setNBPPanel(nbpPanel);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.gridx = 0;
		infoPanel.add(inputPanel, c);
		c.gridheight = 5;
		c.weighty = 1;
		infoPanel.add(weatherPanel, c);
		c.weighty = 0;
		c.gridheight = 1;
		infoPanel.add(ratePanel, c);
		infoPanel.add(nbpPanel, c);

		infoPanel.setPreferredSize(new Dimension(150, 0));
		jf.add(infoPanel, BorderLayout.LINE_START);

		JFXPanel fxPanel = new JFXPanel();
		jf.add(fxPanel, BorderLayout.CENTER);

		Platform.runLater(() -> {
				initFX(fxPanel);
				inputPanel.setWebEngine(webEngine);
			}
		);

		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
