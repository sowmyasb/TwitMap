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
		// getTweets = new TweetGet();
		//    message = "Hello!";
    List<TwitterStatus> dummyList = new ArrayList<TwitterStatus>();
    res.setContentType("text/event-stream");
    res.setCharacterEncoding("UTF-8");

    String keyword = req.getParameter("msg");

    PrintWriter writer = res.getWriter();
    
    List<String> locations = getTweets.getLocationsFromDB();
    String locationList="";
    for (String location: locations) {
      locationList+="||"+location;
    }
    System.out.println(locationList);
    writer.write(locationList+" \n\n");
  }
}