package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * Testing strategy
 *
 * guessFollowsGraph(tweets):
 *   number of tweets:        0, 1, > 1
 *   mentions in a tweet:     0, 1, > 1
 *   tweets by one author:    1, > 1
 *   authors:                 1, > 1 (two authors mention the same person)
 *   case of the usernames:   same case, different case
 *   output:                  empty graph, one follower, many followers
 *
 * influencers(graph):
 *   number of users:         0, 1, > 1
 *   followers of a user:     0, 1, > 1
 *   follower counts:         all different, some equal (tie)
 *   output:                  empty list, one name, many names, no repeated name
 *
 * The postcondition of guessFollowsGraph() is underdetermined (other kinds of
 * evidence are allowed, and the case of the names is not fixed), so the tests
 * only ask "does Ernie follow Bert", never "is the graph exactly equal to this".
 */
public class SocialNetworkTest {

    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    private static final Instant d3 = Instant.parse("2016-02-17T12:00:00Z");

    // ---------------------------------------------------------------- helpers

    private static Set<String> set(String... names) {
        return new HashSet<String>(Arrays.asList(names));
    }

    /** Copy of the graph in which every username is lower case. */
    private static Map<String, Set<String>> normalize(
            Map<String, Set<String>> graph) {
        Map<String, Set<String>> result = new HashMap<String, Set<String>>();
        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            String user = entry.getKey().toLowerCase();
            if (!result.containsKey(user)) {
                result.put(user, new HashSet<String>());
            }
            for (String followed : entry.getValue()) {
                result.get(user).add(followed.toLowerCase());
            }
        }
        return result;
    }

    private static boolean follows(Map<String, Set<String>> graph,
            String follower, String followed) {
        Set<String> followedByUser = normalize(graph).get(follower);
        return followedByUser != null && followedByUser.contains(followed);
    }

    /** True if nobody follows anybody (an author with an empty set is fine). */
    private static boolean nobodyFollowsAnybody(Map<String, Set<String>> graph) {
        for (Set<String> followed : graph.values()) {
            if (!followed.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static Map<String, Set<String>> guess(Tweet... tweets) {
        return SocialNetwork.guessFollowsGraph(Arrays.asList(tweets));
    }

    private static List<String> lower(List<String> names) {
        List<String> result = new ArrayList<String>();
        for (String name : names) {
            result.add(name.toLowerCase());
        }
        return result;
    }

    // ------------------------------------------------- guessFollowsGraph tests

    @Test(expected = AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }

    // covers: 0 tweets
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> graph =
                SocialNetwork.guessFollowsGraph(new ArrayList<Tweet>());
        assertTrue("expected empty graph", graph.isEmpty());
    }

    // covers: 1 tweet, 0 mentions
    @Test
    public void testGuessFollowsGraphNoMentions() {
        Tweet tweet = new Tweet(1, "alyssa", "is it reasonable to talk?", d1);
        Map<String, Set<String>> graph = guess(tweet);
        assertTrue("nobody should follow anybody", nobodyFollowsAnybody(graph));
    }

    // covers: 1 tweet, 1 mention
    @Test
    public void testGuessFollowsGraphSingleMention() {
        Tweet tweet = new Tweet(2, "alyssa", "@bbitdiddle your talk was great", d1);
        Map<String, Set<String>> graph = guess(tweet);
        assertTrue("alyssa mentions bbitdiddle",
                follows(graph, "alyssa", "bbitdiddle"));
    }

    // covers: 1 tweet, > 1 mentions
    @Test
    public void testGuessFollowsGraphMultipleMentions() {
        Tweet tweet = new Tweet(3, "alyssa", "@bbitdiddle @carol meet @dan-smith", d1);
        Map<String, Set<String>> graph = guess(tweet);
        assertTrue(follows(graph, "alyssa", "bbitdiddle"));
        assertTrue(follows(graph, "alyssa", "carol"));
        assertTrue(follows(graph, "alyssa", "dan-smith"));
    }

    // covers: > 1 tweets by one author, the same person mentioned twice
    @Test
    public void testGuessFollowsGraphMultipleTweetsFromOneUser() {
        Tweet tweet1 = new Tweet(4, "alyssa", "@bbitdiddle nice slides", d1);
        Tweet tweet2 = new Tweet(5, "alyssa", "thanks again @carol", d2);
        Tweet tweet3 = new Tweet(6, "alyssa", "@bbitdiddle see you tomorrow", d3);
        Map<String, Set<String>> graph = guess(tweet1, tweet2, tweet3);
        assertTrue(follows(graph, "alyssa", "bbitdiddle"));
        assertTrue(follows(graph, "alyssa", "carol"));
    }

    // covers: > 1 authors, same person mentioned by both
    @Test
    public void testGuessFollowsGraphTwoAuthorsMentionSamePerson() {
        Tweet tweet1 = new Tweet(7, "alyssa", "@carol did you see this?", d1);
        Tweet tweet2 = new Tweet(8, "bbitdiddle", "agree with @carol", d2);
        Map<String, Set<String>> graph = guess(tweet1, tweet2);
        assertTrue(follows(graph, "alyssa", "carol"));
        assertTrue(follows(graph, "bbitdiddle", "carol"));
    }

    // covers: different case of the usernames
    @Test
    public void testGuessFollowsGraphUsernamesAreCaseInsensitive() {
        Tweet tweet = new Tweet(9, "Alyssa", "hello @BBitDiddle", d1);
        Map<String, Set<String>> graph = guess(tweet);
        assertTrue(follows(graph, "alyssa", "bbitdiddle"));
    }

    // covers: spec rule "all usernames must be authors or @-mentions"
    @Test
    public void testGuessFollowsGraphOnlyAuthorsAndMentionedUsers() {
        Tweet tweet1 = new Tweet(10, "alyssa", "@bbitdiddle @carol hi", d1);
        Tweet tweet2 = new Tweet(11, "dan", "no mention here", d2);
        Map<String, Set<String>> graph = guess(tweet1, tweet2);
        Set<String> allowed = set("alyssa", "bbitdiddle", "carol", "dan");
        for (Map.Entry<String, Set<String>> entry : normalize(graph).entrySet()) {
            String user = entry.getKey();
            assertTrue("unknown user " + user, allowed.contains(user));
            for (String followed : entry.getValue()) {
                assertTrue("unknown user " + followed, allowed.contains(followed));
            }
        }
    }

    // ------------------------------------------------------ influencers tests

    // covers: 0 users
    @Test
    public void testInfluencersEmptyGraph() {
        Map<String, Set<String>> graph = new HashMap<String, Set<String>>();
        assertTrue("expected empty list", SocialNetwork.influencers(graph).isEmpty());
    }

    // covers: 1 user, 0 followers
    @Test
    public void testInfluencersSingleUserWithoutFollowers() {
        Map<String, Set<String>> graph = new HashMap<String, Set<String>>();
        graph.put("alyssa", new HashSet<String>());
        List<String> influencers = lower(SocialNetwork.influencers(graph));
        // the spec lists every username of the graph, so alyssa may appear,
        // but nobody else
        assertTrue("nobody has a follower",
                influencers.isEmpty() || influencers.equals(Arrays.asList("alyssa")));
    }

    // covers: 1 user with followers, > 1 users in the list
    @Test
    public void testInfluencersSingleInfluencer() {
        Map<String, Set<String>> graph = new HashMap<String, Set<String>>();
        graph.put("alyssa", set("bbitdiddle"));
        List<String> influencers = lower(SocialNetwork.influencers(graph));
        assertFalse(influencers.isEmpty());
        assertEquals("bbitdiddle", influencers.get(0));
    }

    // covers: > 1 users, all follower counts different
    @Test
    public void testInfluencersMultipleInfluencers() {
        Map<String, Set<String>> graph = new HashMap<String, Set<String>>();
        graph.put("alyssa", set("bbitdiddle", "carol", "dan"));
        graph.put("bbitdiddle", set("carol", "dan"));
        graph.put("carol", set("dan"));
        // followers: dan = 3, carol = 2, bbitdiddle = 1, alyssa = 0
        List<String> influencers = lower(SocialNetwork.influencers(graph));
        assertEquals(Arrays.asList("dan", "carol", "bbitdiddle", "alyssa"),
                influencers);
    }

    // covers: some follower counts equal (tie), the order of a tie is not fixed
    @Test
    public void testInfluencersTiedInfluence() {
        Map<String, Set<String>> graph = new HashMap<String, Set<String>>();
        graph.put("alyssa", set("carol", "dan", "eve"));
        graph.put("bbitdiddle", set("carol", "dan"));
        // followers: carol = 2, dan = 2, eve = 1, alyssa = 0, bbitdiddle = 0
        List<String> influencers = lower(SocialNetwork.influencers(graph));
        assertEquals(5, influencers.size());
        assertEquals(set("carol", "dan"),
                new HashSet<String>(influencers.subList(0, 2)));
        assertEquals("eve", influencers.get(2));
        assertEquals(set("alyssa", "bbitdiddle"),
                new HashSet<String>(influencers.subList(3, 5)));
    }

    // covers: output has no repeated username
    @Test
    public void testInfluencersEveryUserListedOnce() {
        Map<String, Set<String>> graph = new HashMap<String, Set<String>>();
        graph.put("alyssa", set("bbitdiddle", "carol"));
        graph.put("bbitdiddle", set("carol"));
        List<String> influencers = lower(SocialNetwork.influencers(graph));
        assertEquals(3, influencers.size());
        assertEquals(set("alyssa", "bbitdiddle", "carol"),
                new HashSet<String>(influencers));
    }
}