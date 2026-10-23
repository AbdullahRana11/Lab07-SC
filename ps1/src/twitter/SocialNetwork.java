package twitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SocialNetwork provides methods that operate on a social network.
 *
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their lower-case Twitter usernames.
 */
public class SocialNetwork {

    // an @ that is not right after a username character (so emails do not count),
    // followed by the username
    private static final Pattern MENTION =
            Pattern.compile("(?<![A-Za-z0-9_-])@([A-Za-z0-9_-]+)");

    /**
     * Guess who might follow whom, from evidence found in tweets.
     *
     * @param tweets a list of tweets providing the evidence, not modified by
     *               this method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if there is evidence for it in the given list of tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
        Map<String, Set<String>> followsGraph = new HashMap<>();

        for (Tweet tweet : tweets) {
            String author = tweet.getAuthor().toLowerCase();
            Matcher matcher = MENTION.matcher(tweet.getText());

            while (matcher.find()) {
                String followed = matcher.group(1).toLowerCase();
                // a person cannot follow himself, so a self-mention is not evidence
                if (followed.equals(author)) {
                    continue;
                }
                if (!followsGraph.containsKey(author)) {
                    followsGraph.put(author, new HashSet<String>());
                }
                followsGraph.get(author).add(followed);
            }
        }
        return followsGraph;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     *
     * @param followsGraph a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
        // key = username, value = the set of people who follow that user
        final Map<String, Set<String>> followers = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry : followsGraph.entrySet()) {
            String follower = entry.getKey().toLowerCase();
            if (!followers.containsKey(follower)) {
                followers.put(follower, new HashSet<String>());
            }
            for (String followed : entry.getValue()) {
                String user = followed.toLowerCase();
                if (!followers.containsKey(user)) {
                    followers.put(user, new HashSet<String>());
                }
                followers.get(user).add(follower);
            }
        }

        List<String> users = new ArrayList<>(followers.keySet());
        Collections.sort(users, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                int byCount = followers.get(b).size() - followers.get(a).size();
                if (byCount != 0) {
                    return byCount;
                }
                // tie: alphabetical, only to keep the output stable
                return a.compareTo(b);
            }
        });
        return users;
    }
}