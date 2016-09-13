package com.zuehlke.shmack.twitter.download;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.google.common.collect.AbstractIterator;

public class TweetsIterator extends AbstractIterator<Status> {

    private Query nextQuery;
    private final Twitter twitter;
    private Iterator<Status> tweets;

    public TweetsIterator(final Query query) {
        this.nextQuery = query;
        final ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setJSONStoreEnabled(true);
        twitter = new TwitterFactory(cb.build()).getInstance();
    }

    @Override
    protected Status computeNext() {
        if (tweets == null || !tweets.hasNext()) {
            tweets = getNextTweets().iterator();
        }
        return tweets.hasNext() ? tweets.next() : endOfData();
    }

    private List<Status> getNextTweets() {
        if (nextQuery == null) {
            return Collections.emptyList();
        }
        try {
            final QueryResult queryResult = twitter.search(nextQuery);
            nextQuery = queryResult.nextQuery();
            return queryResult.getTweets();
        } catch (final TwitterException e) {
            throw new RuntimeException("Twitter search failed", e);
        }
    }

}
