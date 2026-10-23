# Lab 07: Test First Programming - II (Social Network Analysis)

**Course:** SE-314 Software Construction
**Class:** BESE-15AB
**Student:** Muhammad Abdullah Rana (CMS 508388)
**Instructor:** Dr. Sidra Sultana
**Lab Engineer:** Mr. Aftab Farooq

## About this lab

This lab is about test-first programming. For the given specifications, I first wrote JUnit tests and then implemented the methods so that the tests pass. The work is based on Problem 3 of problem set 1 from MIT 6.005 (Software Construction).

The goal of the problem set is to build a small toolbox that extracts information from a set of tweets. In this lab I worked on the social network part:

- `guessFollowsGraph()` infers who follows whom, based on @-mentions in tweets.
- `influencers()` returns the users sorted by how many followers they have.

## Project structure
Lab 7/
├── ps1/ Eclipse project (problem set 1)
│ └── src/twitter/
│ ├── SocialNetwork.java implementation
│ ├── SocialNetworkTest.java JUnit 4 tests
│ ├── Main.java runs the program on sample tweets
│ └── ... other files from the problem set
└── __MACOSX/ folder created by the zip file (not needed)


## Testing strategy

The tests follow input/output space partitioning. For `guessFollowsGraph()` the input is partitioned on the number of tweets, the number of mentions, the number of authors, and the case of usernames. For `influencers()` the input is partitioned on the number of users, the number of followers, and whether follower counts are equal. Every test is small and uses hand-written tweets, so a failure is easy to understand.

The tests check only what the specification guarantees. The graph is underdetermined, so the tests do not compare the whole graph with one fixed answer, and the order of tied users is not checked.

## How to run

1. Open the `ps1` folder as an Eclipse project (File, Import, Existing Projects into Workspace).
2. Run `SocialNetworkTest` as a JUnit 4 test. All 15 tests should pass.
3. Run `Main` as a Java application with the VM argument `-ea`. It prints the top 10 most-followed users from a sample of tweets.

Assertions must be enabled (`-ea`) for both the tests and `Main`.

## Notes

- `Main.java` uses a small list of hand-written tweets, because the course server that used to provide the sample tweets no longer returns the file.
- `TweetReader.java` is part of the original problem set and is not changed.
- The `bin/` folder (compiled output) is not pushed, see `.gitignore`.

## Author

Muhammad Abdullah Rana (CMS 508388), BESE 15-B, NUST SEECS
