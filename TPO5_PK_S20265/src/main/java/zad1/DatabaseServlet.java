package zad1;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class DatabaseServlet extends HttpServlet {

    DataSource dataSource;
    private static final String CHARSET = "UTF-8";

    @Override
    public void init() throws ServletException {
        Connection con = null;
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/cardb");

            synchronized (dataSource) {
                con = dataSource.getConnection();
            }

            List<String> rodzajList = new ArrayList<>();
            try (Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT DISTINCT Rodzaj FROM Cars");
                while (rs.next()) {
                    rodzajList.add(rs.getString("Rodzaj"));
                }
            }
            var servletContext = getServletContext();
            servletContext.setAttribute("rodzajList", rodzajList);

        } catch (NamingException e) {
            throw new ServletException("Unable to lookup java:comp/env/jdbc/cardb", e);
        } catch (SQLException e) {
            throw new ServletException("SQL Exception", e);
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // TODO generate a fancy dropdown "rodzaj" selection
    }
}
