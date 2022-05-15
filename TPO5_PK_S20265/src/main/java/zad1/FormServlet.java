package zad1;


import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.UnsupportedCharsetException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class FormServlet extends HttpServlet {

    private static final String CHARSET = "UTF-8";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, UnsupportedCharsetException {
        req.setCharacterEncoding(CHARSET);
        resp.setContentType("text/html");

        PrintWriter out = resp.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Request Parameters Example</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h3>Request Parameters Example</h3>");
        out.println("Parameters in this request:<br>");

        String rodzaj = req.getParameter("rodzaj");

        if (rodzaj != null) {
            out.println("Rodzaj:");
            out.println(" = " + rodzaj);
        }

        out.println("<P>");
        out.print("<form action=\"");
        out.print("RequestParamExample\" ");
        out.println("method=POST>");
        out.println("First Name:");
        out.println("<input type=text size=20 name=firstname>");
        out.println("<br>");
        out.println("Last Name:");
        out.println("<input type=text size=20 name=lastname>");
        out.println("<br>");
        out.println("<input type=submit>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

    }
}
