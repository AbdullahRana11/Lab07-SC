/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the main program.
 *
 * The original version downloaded a sample of tweets from the MIT course
 * server, but that server does not return the tweet file any more (TweetReader
 * gets a web page instead of JSON). So this version uses a small list of
 * tweets written by hand. The rest of the program is the same: it infers the
 * follows graph with guessFollowsGraph() and prints the top 10 users with
 * influencers().
 */
public class Main {

    /**
     * Main method of the program. Builds a follows graph from a sample of
     * tweets and prints the top influencers.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            assert false;
            throw new Error("Always run main and tests with assertions enabled");
        } catch (AssertionError ae) { }

        final Instant t1 = Instant.parse("2026-10-23T10:00:00Z");
        final Instant t2 = Instant.parse("2026-10-23T10:30:00Z");
        final Instant t3 = Instant.parse("2026-10-23T11:00:00Z");

        final List<Tweet> tweets = Arrays.asList(
                new Tweet(1, "alyssa", "@bbitdiddle great talk today, thanks @carol", t1),
                new Tweet(2, "bbitdiddle", "thanks @carol and @dan for the help", t1),
                new Tweet(3, "dan", "@carol can you share the slides? cc @bbitdiddle", t2),
                new Tweet(4, "eve", "@carol @alyssa see you at the lab", t2),
                new Tweet(5, "frank", "@carol @bbitdiddle @dan nice work", t2),
                new Tweet(6, "carol", "thanks everyone, @alyssa special thanks to you", t3),
                new Tweet(7, "grace", "@alyssa @dan lab report is due tonight", t3),
                new Tweet(8, "heidi", "@bbitdiddle @alyssa what time is the viva?", t3));

        // display some characteristics about the tweets
        System.err.println("using " + tweets.size() + " tweets");

        // infer the follows graph
        final Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        System.err.println("follows graph has " + followsGraph.size() + " nodes");

        // print the top-N influencers
        final int count = 10;
        final List<String> influencers = SocialNetwork.influencers(followsGraph);
        for (String username : influencers.subList(0, Math.min(count, influencers.size()))) {
            System.out.println(username);
        }
    }

}