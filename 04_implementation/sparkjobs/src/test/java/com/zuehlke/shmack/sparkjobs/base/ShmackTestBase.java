package com.zuehlke.shmack.sparkjobs.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.zuehlke.shmack.sparkjobs.base.SortedCounts.Entry;
import com.zuehlke.shmack.sparkjobs.infrastructure.ExecuteResult;

public class ShmackTestBase {

	protected static <T> void assertPosition(final SortedCounts<T> sortedCounts, final int position,
			final int expectedCount, final T expectedValue) {
		final Entry<T> entry = sortedCounts.getEntry(position);
		assertEquals(expectedCount, entry.getCount());
		assertEquals(expectedValue, entry.getValue());
	}

	protected static void assertExecuteResultStandardOutputContains(String expectedSubstring, ExecuteResult executeResult) {
		if (executeResult.getStandardOutput().contains(expectedSubstring)) {
			return;
		}
		fail("output of process does not contain '" + expectedSubstring + "': " + executeResult);
	}
}