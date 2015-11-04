package com.zuehlke.shmack.twitter.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

import com.google.common.base.Preconditions;

/**
 * <pre>
 * Utility class to create reference data files.
 * Each line in a file consists of a complete JSON string, which represents one tweet.
 * 
 * After this utility is finished, a file is written to {@link #TARGET_TWEETS_JSON_FILE},
 * which can be copies to src/test/resources to serve as reference data for testcases.
 * 
 * Note that there are rate limits: https://dev.twitter.com/rest/public/rate-limits
 * as of 2015-11-03 450 request / 15 minutes are allowed --> 45.000 tweets in 15 minutes.
 * </pre>
 */
public class TwitterDownloader {

    private static final String TARGET_TWEETS_JSON_FILE = "build/tweets.json";

    private static final int MAX_PAGE_SIZE = 100;

    private final Query query;
    private final int numberOfTweetsToDownload;

    public static void main(final String[] args) throws Exception {

        // syntax: https://dev.twitter.com/rest/public/search
        final Query query = new Query("big data");
        final int numberOfTweetsToDownload = 10_000;
        final TwitterDownloader dl = new TwitterDownloader(query, numberOfTweetsToDownload);
        dl.downloadTweetsToFile(TARGET_TWEETS_JSON_FILE);
    }

    public TwitterDownloader(final Query query, final int numberOfTweetsToDownload) {
        this.query = query;
        this.numberOfTweetsToDownload = numberOfTweetsToDownload;
    }

    private void downloadTweetsToFile(final String targetFileName) throws IOException, TwitterException {
        File targetFile = new File(targetFileName);
        FileUtils.forceMkdir(targetFile.getParentFile());
        
        try (final PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetFile),
                StandardCharsets.UTF_8.name()))) {

            query.setCount(Math.min(MAX_PAGE_SIZE, numberOfTweetsToDownload));

            final TweetsIterator tweetsIterator = new TweetsIterator(query);

            for (int i = 0; i < numberOfTweetsToDownload && tweetsIterator.hasNext(); i++) {
                final Status status = tweetsIterator.next();
                // http://stackoverflow.com/questions/11968905/how-to-get-a-tweet-with-its-full-json-in-twitter4j
                final String rawJSON = TwitterObjectFactory.getRawJSON(status);
                final boolean jsonContainsNewline = rawJSON.indexOf('\n') >= 0;
                Preconditions.checkState(!jsonContainsNewline);
                writer.println(rawJSON);
                System.out.println(status.getCreatedAt() + " @" + status.getUser().getScreenName() + ":" + status.getText());
            }
        }
    }
}
