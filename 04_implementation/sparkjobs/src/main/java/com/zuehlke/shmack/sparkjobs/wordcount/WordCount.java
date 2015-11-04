package com.zuehlke.shmack.sparkjobs.wordcount;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

/**
 * @see http://spark.apache.org/examples.html
 */
public class WordCount extends TestableSparkJob<JavaPairRDD<String, Integer>> implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String inputFile;

    public WordCount(final String inputFile) {
        this.inputFile = inputFile;
    }

    @SuppressWarnings("serial")
    @Override
    public JavaPairRDD<String, Integer> execute(final JavaSparkContext spark) {
        final JavaRDD<String> textFile = spark.textFile(inputFile);
        final JavaRDD<String> words = textFile.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterable<String> call(final String rawJSON) throws TwitterException {
                final Status tweet = TwitterObjectFactory.createStatus(rawJSON);
                String text = tweet.getText();
                return Arrays.asList(text.split(" "));
            }
        });
        final JavaPairRDD<String, Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(final String s) {
                return new Tuple2<String, Integer>(s.toLowerCase(), 1);
            }
        });
        final JavaPairRDD<String, Integer> counts = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(final Integer a, final Integer b) {
                return a + b;
            }
        });
        return counts;
    }

}
