package zad1;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import zad1.WeatherInfo.WeatherInfo;

final class WeatherPanel extends JPanel {
	JLabel cityLabel;
	JLabel conditionsLabel;
	JLabel tempLabel;
	JLabel pressureLabel;
	JLabel humidityLabel;
	JLabel windLabel;

	public WeatherPanel() {
		super();
		this.cityLabel = new JLabel();
		this.conditionsLabel = new JLabel();
		this.tempLabel = new JLabel();
		this.pressureLabel = new JLabel();
		this.humidityLabel = new JLabel();
		this.windLabel = new JLabel();
		add(cityLabel);
		add(conditionsLabel);
		add(tempLabel);
		add(pressureLabel);
		add(humidityLabel);
		add(windLabel);
		setBorder(BorderFactory.createTitledBorder("Weather"));
	}

	public void updateData(WeatherInfo wi) {
		//TODO add icons! 🙂
		cityLabel.setText("City: " 
			+ wi.getName());
		conditionsLabel.setText("Conditions: " 
			+ wi.getWeather().get(0).main);
		tempLabel.setText("Temperature: "
			+ wi.getMain().getTemp() + " °C");
		pressureLabel.setText("Pressure: "
			+ wi.getMain().getPressure() + " hPa");
		humidityLabel.setText("Humidity: "
			+ wi.getMain().getHumidity() + "%");
		windLabel.setText("Wind: " 
			+ wi.getWind().getSpeed() + " km/h");
	}
}