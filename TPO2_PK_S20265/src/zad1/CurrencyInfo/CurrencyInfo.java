package zad1.CurrencyInfo;

import java.util.ArrayList;

public class CurrencyInfo {
    public String table;
    public String currency;
    public String code;
    public ArrayList<Rate> rates;

    public String getTable() {
        return table;
    }
    public void setTable(String table) {
        this.table = table;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public ArrayList<Rate> getRates() {
        return rates;
    }
    public void setRates(ArrayList<Rate> rates) {
        this.rates = rates;
    }

}
