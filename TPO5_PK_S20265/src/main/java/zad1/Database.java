package zad1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private String url;

    public Database(String url) {
        this.url = url;
    }

    public void create() throws SQLException, IOException {

        // Create database
        String createUrl = url + ";create=true";

        try (var connnection = DriverManager.getConnection(createUrl);
                var statement = connnection.createStatement()) {

            // Drop existing table
            statement.execute("DROP TABLE Cars");

            // Create table
            String createTables = "CREATE TABLE Cars("
                    + "IdCar integer not null generated by default as identity,"
                    + "Rodzaj varchar(15) not null,"
                    + "Marka varchar(20) not null,"
                    + "Model varchar(20) not null,"
                    + "Rok integer not null"
                    + ")";

            statement.execute(createTables);
            // Read data and insert into database
            try (var lineStream = Files.lines(Paths.get("TPO5_PK_S20265/data/car_data.csv"))) {
                lineStream.forEach(line -> {
                    var lineArray = line.split(",");
                    String rodzaj = lineArray[0];
                    String marka = lineArray[1];
                    String model = lineArray[2];
                    String rok = lineArray[3];

                    String insertData = "INSERT INTO Cars(Rodzaj,Marka,Model,Rok) VALUES("
                            + "'" + rodzaj + "','" + marka + "','" + model + "'," + rok + ")";
                    try {
                        statement.execute(insertData);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}