/**
 * Created by annapurna on 10/29/14.
 */
public class TwitterStatus {
  String userName, location, content;
  long tweetId;

  public TwitterStatus(String userName, long tweetId, String location,
                       String content) {
    this.userName = userName;
    this.tweetId = tweetId;
    this.location = location;
    this.content = content;
  }

  public long getTweetId() {
    return tweetId;
  }

  public String getUserName() {
    return userName;
  }

  public String getLocation() {
    return location;
  }

  public String getContent() {
    return content;
  }
}
