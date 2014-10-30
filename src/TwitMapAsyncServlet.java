import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(urlPatterns={"/twitmap"})
public class TwitMapAsyncServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {
        res.setContentType("text/event-stream");
        res.setCharacterEncoding("UTF-8");

        String msg = req.getParameter("msg");

        PrintWriter writer = res.getWriter();

        // send SSE
        writer.write("data: " + msg + "\n\n");
    }
}