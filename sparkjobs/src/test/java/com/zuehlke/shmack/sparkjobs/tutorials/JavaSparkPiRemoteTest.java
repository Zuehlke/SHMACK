package com.zuehlke.shmack.sparkjobs.tutorials;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.shmack.sparkjobs.base.HdfsUtils;
import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestExecutionContext;
import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestRunner;

public class JavaSparkPiRemoteTest {

	private final static Logger LOGGER = LoggerFactory.getLogger(JavaSparkPiRemoteTest.class);

	@Test
	public void testSparkPiRemote_10_Slices() throws Exception {
		testSparkPiRemote(10, 1e-2);
	}

	@Test
	public void testSparkPiRemote_100_Slices() throws Exception {
		testSparkPiRemote(100, 1e-3);
	}

	@Test
	public void testSparkPiRemote_200_Slices() throws Exception {
		testSparkPiRemote(200, 1e-3);
	}

	private void testSparkPiRemote(int nSlices, double allowedDelta) throws Exception {
		String testcaseId = "WordCount-" + nSlices;
		RemoteSparkTestRunner runner = new RemoteSparkTestRunner(JavaSparkPiRemoteTest.class, testcaseId);
		runner.executeSparkRemote(String.valueOf(nSlices));
		runner.waitForSparkFinished();
		Double result = runner.getRemoteResult();
		LOGGER.info("Result of Pi with {} number of slices: {}", nSlices, result);
		LOGGER.info("Difference from real Pi: {} ", Math.abs(Math.PI - result));
		assertEquals(Math.PI, result.doubleValue(), allowedDelta);
	}

	public static void main(String[] args) throws Exception {
		HdfsUtils.pingHdfs();

		int nSlices = Integer.parseInt(args[0]);

		JavaSparkPi sparkPi = new JavaSparkPi(nSlices);
		String testcaseId = "WordCount-" + nSlices;
		RemoteSparkTestExecutionContext execution = new RemoteSparkTestExecutionContext(JavaSparkPiRemoteTest.class, testcaseId);
		execution.executeWithStatusTracking(sparkPi);
	}

}
