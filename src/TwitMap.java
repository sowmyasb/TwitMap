import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by annapurna on 10/29/14.
 */
public class TwitMap  extends HttpServlet {
  private String message;
  protected TweetGet getTweets;

  public void init() {
   getTweets = new TweetGet();
    message = "Hello!";
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    getTweets = new TweetGet();
    message = "Hello!";
    
    response.setContentType("application/json");

    // Actual logic goes here.
    PrintWriter out = response.getWriter();
    out.println(message);
  }

  public void destroy() {
    // do nothing.
  }

}
