package com.zuehlke.shmack.sparkjobs.wordcount;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.spark.api.java.JavaPairRDD;

import scala.Tuple2;

public class SortedCounts<T> implements Iterable<SortedCounts.Entry<T>> {

    public static class Entry<T> implements Comparable<Entry<T>> {

        private final long count;
        private final T value;

        private Entry(final long count, final T value) {
            this.count = count;
            this.value = value;
        }

        public long getCount() {
            return count;
        }

        public T getValue() {
            return value;
        }

        @Override
        public int compareTo(final Entry<T> o) {
            final CompareToBuilder b = new CompareToBuilder();
            b.append(-this.count, -o.count);
            b.append(this.value, o.value);
            return b.toComparison();
        }
    }

    private final List<Entry<T>> sortedEntries;

    private SortedCounts(final SortedSet<Entry<T>> sortedEntries) {
        this.sortedEntries = new ArrayList<>(sortedEntries);
    }

    public static <T, N extends Number> SortedCounts<T> create(final JavaPairRDD<T, N> rdd) {

        final SortedSet<Entry<T>> sortedEntries = new TreeSet<>();
        for (final Tuple2<T, N> tuple : rdd.collect()) {
            sortedEntries.add(new Entry<T>(tuple._2.longValue(), tuple._1));
        }
        final SortedCounts<T> result = new SortedCounts<T>(sortedEntries);

        return result;
    }

    @Override
    public Iterator<Entry<T>> iterator() {
        return sortedEntries.iterator();
    }
    
    public Entry<T> getEntry(int position) {
        return sortedEntries.get(position);
    }

    public int size() {
        return sortedEntries.size();
    }

    public void print(PrintStream out) {
        for (int i = 0; i < sortedEntries.size(); i++) {
            Entry<T> entry = sortedEntries.get(i);
            out.println(i + ".: " + entry.getCount() + ": " + entry.getValue());
        }
    }

}
