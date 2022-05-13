package zad1;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        var database = new Database("jdbc:derby://localhost/traveldb");
        database.create();
    }
}
