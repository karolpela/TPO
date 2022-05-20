package zad1;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class FormServlet extends HttpServlet {

    private static final String CHARSET = "UTF-8";

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setCharacterEncoding(CHARSET);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        resp.setContentType("text/html; charset=utf-8");

        try (PrintWriter out = resp.getWriter()) {

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Car database interface</title>");
            out.println("</head>");

            out.println("<body>");

            out.println("<h1>Car database interface</h1>");

            var servletContext = getServletContext();

            // Get a list of possible "Rodzaj" selections
            @SuppressWarnings("unchecked")
            List<String> rodzajList = (List<String>) servletContext.getAttribute("rodzajList");

            // Create a drop-down selector
            out.print("<form method = \"POST\""
                    + " action=\"http://localhost:8080/TPO5/form\"");
            out.println("<label for=\"rodzaj\"> Wybierz rodzaj: </label>");
            out.println("<select name=\"rodzaj\" id=\"rodzaj\">");

            // Add options to the selector
            for (String rodzaj : rodzajList) {
                out.println("<option value=\"" + rodzaj + "\">" + rodzaj + "</option>");
            }

            out.println("</select>");
            out.println("<input type=submit value=\"Wyślij\"/>");
            out.println("</form>");

            String reqRodzaj = req.getParameter("rodzaj");
            if (reqRodzaj == null) {
                printEndTag(out);
                return;
            }

            // Get table headers
            var tableDispatcher = getServletContext().getRequestDispatcher("/table");
            tableDispatcher.include(req, resp);

            printEndTag(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printEndTag(PrintWriter out) {
        out.println("</body>");
        out.println("</html>");
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
