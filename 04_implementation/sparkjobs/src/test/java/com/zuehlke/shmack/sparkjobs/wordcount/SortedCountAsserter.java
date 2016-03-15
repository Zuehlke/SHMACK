package com.zuehlke.shmack.sparkjobs.wordcount;

import static org.junit.Assert.assertEquals;

import com.zuehlke.shmack.sparkjobs.base.SortedCounts;
import com.zuehlke.shmack.sparkjobs.base.SortedCounts.Entry;

public class SortedCountAsserter {
	protected static <T> void assertPosition(final SortedCounts<T> sortedCounts, final int position,
			final int expectedCount, final T expectedValue) {
		final Entry<T> entry = sortedCounts.getEntry(position);
		assertEquals(expectedCount, entry.getCount());
		assertEquals(expectedValue, entry.getValue());
	}
}
