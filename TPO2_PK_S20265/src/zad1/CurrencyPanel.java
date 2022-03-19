package zad1;

import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CurrencyPanel extends JPanel{

    JLabel currencyLabel;

    public CurrencyPanel(String title) {
        super();
        this.currencyLabel = new JLabel();
        add(currencyLabel);
        setBorder(BorderFactory.createTitledBorder(title));
    }

    public void updateData(Double rate) {
        currencyLabel.setText(new DecimalFormat("0.0000").format(rate));
    }
}
