package zad1;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
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


        // String reqRodzaj = req.getParameter("rodzaj");

        // Get table headers
        var dbDispatcher = getServletContext().getRequestDispatcher("/database");
        dbDispatcher.include(req, resp);

        @SuppressWarnings("unchecked")
        List<String> tableHeaders = (List<String>) getServletContext().getAttribute("tableHeaders");
        @SuppressWarnings("unchecked")
        List<String[]> tableData = (List<String[]>) getServletContext().getAttribute("tableData");


        out.println("<table>");

        // Create header row
        out.println("<tr>");
        for (String header : tableHeaders) {
            out.println("<th>" + header + "</th>");

        }
        out.println("</tr>");

        // Create table row for each row in result set
        for (String[] row : tableData) {
            out.println("<tr>");
            for (int i = 0; i < row.length; i++) {
                out.println("<td>" + row[i] + "</td>");
            }
            out.println("</tr>");
        }

        out.println("</table>");


        // Connection con = null;
        // try {
        // synchronized (dataSource) {
        // con = dataSource.getConnection();
        // }
        // try (Statement stmt = con.createStatement()) {
        // String reqRodzaj = req.getParameter("rodzaj");

        // // Get all cars of requested type
        // ResultSet rs = stmt.executeQuery(
        // "SELECT * FROM Cars WHERE Rodzaj=\'" + reqRodzaj + "\'");

        // // Get metadata to create table dynamically
        // ResultSetMetaData rsmd = rs.getMetaData();
        // int columnCount = rsmd.getColumnCount();
        // out.println("<table>");

        // // Create header row
        // out.println("<tr>");
        // for (int i = 0; i < columnCount; i++) {
        // out.println("<th>" + rsmd.getColumnName(i + 1) + "</th>");
        // }
        // out.println("</tr>");

        // // Create table row for each row in result set
        // while (rs.next()) {
        // out.println("<tr>");
        // for (int i = 0; i < columnCount; i++) {
        // out.println("<td>" + rs.getString(i + 1) + "</td>");
        // }
        // out.println("</tr>");
        // }
        // out.println("</table>");

        // }

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
