package zad1;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;

import com.google.gson.Gson;

import javafx.application.Platform;
import javafx.scene.web.WebEngine;
import zad1.WeatherInfo.WeatherInfo;

public class InputPanel extends JPanel{

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
        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.BOTH;
        c1.weightx = 1;
        c1.weighty = 1;
        c1.gridx= 0;
        c1.gridy = GridBagConstraints.RELATIVE;
        add(new JLabel("Country:"), c1);
        add(countryField = new JTextField(), c1);
        add(new JLabel("City:"), c1);
        add(cityField = new JTextField(), c1);
        add(new JLabel("Currency:"), c1);
        add(currencyField = new JTextField(), c1);
        add(new JLabel(" "), c1);
        JButton updateButton = new JButton("Update");
        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.BOTH;
        c2.weightx = 1;
        c2.weighty = 1;
        c2.gridx = 0;
        c2.gridy = GridBagConstraints.RELATIVE;
        c2.ipady = 10;
        add(updateButton, c2);
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
