import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns={"/twitmapsse"})
public class TwitMapAsyncServlet extends TwitMap {

	private static final long serialVersionUID = 1L;

	@Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws
      IOException, ServletException {
    List<TwitterStatus> dummyList = new ArrayList<TwitterStatus>();
    dummyList.add(new TwitterStatus("abc",123, "Hyderabad", "yyyy"));
    dummyList.add(new TwitterStatus("abc",456, "Bangalore", "yyyy"));
    dummyList.add(new TwitterStatus("abc",789, "Chennai", "yyyy"));
    dummyList.add(new TwitterStatus("abc",1011, "Indore", "yyyy"));
    dummyList.add(new TwitterStatus("abc",1213, "Delhi", "yyyy"));
    getTweets.helper.batchInsert(dummyList);

    res.setContentType("text/event-stream");
    res.setCharacterEncoding("UTF-8");

    String msg = req.getParameter("msg");

    PrintWriter writer = res.getWriter();
    writer.write(msg + "\n\n");
    List<String> locations = getTweets.getLocationsFromDB();
    for (String location: locations) {
      writer.write(location + "\n");
    }
  }
}