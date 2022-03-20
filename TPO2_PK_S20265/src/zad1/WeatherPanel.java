package zad1;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import zad1.WeatherInfo.WeatherInfo;

final class WeatherPanel extends JPanel {
	JLabel icon;
	JLabel cityLabel;
	JLabel conditionsLabel;
	JLabel tempLabel;
	JLabel pressureLabel;
	JLabel humidityLabel;
	JLabel windLabel;

	public WeatherPanel() {
		super();

		this.cityLabel = new JLabel();
		cityLabel.setFont(
			new Font(cityLabel.getFont().getName(), Font.BOLD, 18)
		);

		this.icon = new JLabel();
		
		this.tempLabel = new JLabel();
		tempLabel.setFont(
			new Font(cityLabel.getFont().getName(), Font.BOLD, 16)
		);

		this.conditionsLabel = new JLabel();
		conditionsLabel.setFont(
			new Font(cityLabel.getFont().getName(), Font.BOLD, 16)
		);

		this.pressureLabel = new JLabel();
		this.humidityLabel = new JLabel();
		this.windLabel = new JLabel();
		add(cityLabel);
		add(icon);
		add(tempLabel);
		add(conditionsLabel);
		add(pressureLabel);
		add(humidityLabel);
		add(windLabel);
		setBorder(BorderFactory.createTitledBorder("Weather"));
	}

	public void updateData(WeatherInfo wi) {

		try {
			URL iconURL = new URL("http://openweathermap.org/img/wn/"
			+ wi.getWeather().get(0).getIcon() + "@2x.png");
			icon.setIcon(new ImageIcon(iconURL));
			icon.setBackground(Color.BLACK);
		} catch (MalformedURLException e) {
			System.out.println("[!] Malformed URL");
		}


		cityLabel.setText(wi.getName());

		conditionsLabel.setText(wi.getWeather().get(0).main);
		tempLabel.setText(wi.getMain().getTemp() + "°C");

		pressureLabel.setText("Pressure: "
			+ wi.getMain().getPressure() + " hPa");
		humidityLabel.setText("Humidity: "
			+ wi.getMain().getHumidity() + "%");
		windLabel.setText("Wind: " 
			+ wi.getWind().getSpeed() + " km/h");
	}
}