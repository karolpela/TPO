package zad1;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.servlet.RequestDispatcher;
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

            out.println("<center><h1>Car database interface</h1></center>");
            out.print("<form method = \"POST\""
                    + "form action=\"http://localhost:8080/TPO5/form\">");
            out.println("Rodzaj:");
            out.println("<input type=text size=20 name=rodzaj>");
            out.println("<br>");
            out.println("<input type=submit>");

            String rodzaj = req.getParameter("rodzaj");

            if (rodzaj == null) {
                printEndTag(out);
                return;
            }

            out.println("<p>Request parameter: ");
            out.println(rodzaj);
            out.println("</p>");

            RequestDispatcher disp = getServletContext().getRequestDispatcher("/table");
            disp.include(req, resp);

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