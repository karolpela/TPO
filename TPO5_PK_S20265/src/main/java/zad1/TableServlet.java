package zad1;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class TableServlet extends HttpServlet {
    DataSource dataSource;
    private static final String CHARSET = "UTF-8";

    @Override
    public void init() throws ServletException {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/cardb");
        } catch (NamingException e) {
            throw new ServletException("Unable to lookup java:comp/env/jdbc/cardb", e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            req.setCharacterEncoding(CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        resp.setContentType("text/html; charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("<h2> Car database </h2>");

        Connection con = null;
        try {
            synchronized (dataSource) {
                con = dataSource.getConnection();
            }
            try (Statement stmt = con.createStatement()) {
                String reqRodzaj = req.getParameter("rodzaj");
                ResultSet rs = stmt.executeQuery(
                        "SELECT * FROM Cars WHERE Rodzaj=\'" + reqRodzaj + "\'");
                out.println("<ul>");
                while (rs.next()) {
                    int id = rs.getInt("IdCar");
                    String rodzaj = rs.getString("Rodzaj");
                    String marka = rs.getString("Marka");
                    String model = rs.getString("Model");
                    int rok = rs.getInt("Rok");
                    out.println("<li>"
                            + id + " " + rodzaj + " " + marka + " " + model + " " + rok
                            + "</li>");
                }
                out.println("</ul>");
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
        out.close();
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
