package mgit.memes.confessions.Classes;

import java.util.List;

public class favTweets {
    private List<String> tweetIds;

    public favTweets() {
    }

    public favTweets(List<String> tweetIds) {
        this.tweetIds = tweetIds;
    }

    public List<String> getTweetIds() {
        return tweetIds;
    }

    public void setTweetIds(List<String> tweetIds) {
        this.tweetIds = tweetIds;
    }
}
