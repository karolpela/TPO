package zad1;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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

            // Get all the possible "Rodzaj" values
            List<String> rodzajList = new ArrayList<>();
            try (Statement stmt = con.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT DISTINCT RODZAJ FROM CARS");
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
        // if (req.getDispatcherType() != DispatcherType.INCLUDE)
        // return;
        try {
            req.setCharacterEncoding(CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        resp.setContentType("text/html; charset=utf-8");
        PrintWriter out = resp.getWriter();
        Connection con = null;
        try {
            synchronized (dataSource) {
                con = dataSource.getConnection();
            }
            try (Statement stmt = con.createStatement()) {
                String reqRodzaj = req.getParameter("rodzaj");
                // Get all cars of requested type
                ResultSet rs =
                        stmt.executeQuery("SELECT * FROM Cars WHERE Rodzaj=\'" + reqRodzaj + "\'");

                // Get metadata to create table dynamically
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                List<String[]> tableData = new ArrayList<>();

                // Create header row
                List<String> tableHeaders = new ArrayList<>();

                for (int i = 0; i < columnCount; i++) {
                    tableHeaders.add(rsmd.getColumnName(i + 1));
                }
                req.setAttribute("tableHeaders", tableHeaders);

                // Create table row for each row in result set
                while (rs.next()) {
                    String[] row = new String[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        row[i] = rs.getString(i + 1);
                    }
                    tableData.add(row);
                }
                req.setAttribute("tableData", tableData);
            }
        } catch (Exception e) {
            out.println(e.getMessage());
        } finally {
            try {
                if (con != null)
                    con.close();
            } catch (SQLException sqle) {
                out.println(sqle.getMessage());
            }
        }
        // out.close(); don't because this is an "include" and other servlets need it!
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            service(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            service(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
