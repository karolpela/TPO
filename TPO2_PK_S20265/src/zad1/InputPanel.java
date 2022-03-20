package zad1;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import zad1.WeatherInfo.WeatherInfo;

public class InputPanel extends JPanel {

    JTextField countryField;
    JTextField cityField;
    JTextField currencyField;
    WeatherPanel weatherPanel;
    CurrencyPanel ratePanel;
    CurrencyPanel NBPPanel;
    WebEngine webEngine;

    public InputPanel() {
        super();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = GridBagConstraints.RELATIVE;
        add(new JLabel("Country:"), c);
        add(countryField = new JTextField(), c);
        add(new JLabel("City:"), c);
        add(cityField = new JTextField(), c);
        add(new JLabel("Currency:"), c);
        add(currencyField = new JTextField(), c);
        JButton updateButton = new JButton("Update");
        c.insets = new Insets(10,0,0,0);
        c.ipady = 10;
        add(updateButton, c);
        updateButton.addActionListener(l -> {
            Service s = new Service(countryField.getText());
            Gson gson = new Gson();
            Platform.runLater(() -> {
                weatherPanel.updateData(gson.fromJson(s.getWeather(cityField.getText()), WeatherInfo.class));
                ratePanel.updateData(s.getRateFor(currencyField.getText()));
                NBPPanel.updateData(s.getNBPRate());
                webEngine.load("https://en.wikipedia.org/wiki/" + cityField.getText());
            });
        });
        setBorder(BorderFactory.createTitledBorder("Input"));
    }

    public CurrencyPanel getNBPPanel() {
        return NBPPanel;
    }

    public void setNBPPanel(CurrencyPanel NBPPanel) {
        this.NBPPanel = NBPPanel;
    }

    public CurrencyPanel getRatePanel() {
        return ratePanel;
    }

    public void setRatePanel(CurrencyPanel ratePanel) {
        this.ratePanel = ratePanel;
    }

    public WeatherPanel getWeatherPanel() {
        return weatherPanel;
    }

    public void setWeatherPanel(WeatherPanel weatherPanel) {
        this.weatherPanel = weatherPanel;
    }

    public WebEngine getWebEngine() {
        return webEngine;
    }

    public void setWebEngine(WebEngine webEngine) {
        this.webEngine = webEngine;
    }
}
