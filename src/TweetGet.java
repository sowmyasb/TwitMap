import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;
import java.util.ArrayList;
import java.util.List;

public final class TweetGet {
  private List<TwitterStatus> twitterStatusList = new
      ArrayList<TwitterStatus>();
  private static final DataBaseHelper helper = new DataBaseHelper();

  public TweetGet() {
    getTweets();
  }

  private void getTweets() {
    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true)
        .setOAuthConsumerKey("o7pdF4Z2Ip9jgWOYPdvcdCSrN")
        .setOAuthConsumerSecret("vpqKS8MoJiiuGz9hjI0d6VXdnc8MLwMvUEmhxlsFWp3CLkWKuG")
        .setOAuthAccessToken("2850527037-T3nmJhuNAQixoEdbaFLQIzdCivDG4aAnG2WPheT")
        .setOAuthAccessTokenSecret("JsoGCmBJzOT0RkgRQMewZS7JuETeJaDz9LtqAUGyxpxin");

    TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
    StatusListener listener = new StatusListener() {
      @Override
      public void onStatus(Status status) {
        String username = status.getUser().getScreenName();
        String profileLocation = status.getUser().getLocation();
        long tweetId = status.getId();
        String content = status.getText();
        TwitterStatus newStatus = new TwitterStatus(username, tweetId,
            profileLocation, content);
        twitterStatusList.add(newStatus);
        if (twitterStatusList.size() == 1000) {
          updateDB();
          twitterStatusList.clear();
        }
      }

      @Override
      public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
      }

      @Override
      public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
      }

      @Override
      public void onScrubGeo(long userId, long upToStatusId) {
        System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
      }

      @Override
      public void onStallWarning(StallWarning warning) {
        System.out.println("Got stall warning:" + warning);
      }

      @Override
      public void onException(Exception ex) {
        ex.printStackTrace();
      }
    };
    twitterStream.addListener(listener);
    twitterStream.sample();
  }

  private void updateDB() {
    while (!helper.batchInsert(twitterStatusList)) {
      System.out.println("Update failed, Retrying");
    }
    System.out.println("Update successful");
  }

  public List<String> getLocationsFromDB() {
    return helper.getLocations();
  }
}