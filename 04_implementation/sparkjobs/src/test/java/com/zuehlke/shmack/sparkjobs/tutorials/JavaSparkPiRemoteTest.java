package com.zuehlke.shmack.sparkjobs.tutorials;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.shmack.sparkjobs.base.HdfsUtils;
import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestBase;
import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestRunner;

public class JavaSparkPiRemoteTest extends RemoteSparkTestBase {

	private final static Logger LOGGER = LoggerFactory.getLogger(JavaSparkPiRemoteTest.class);

	@Test
	public void testWordcountRemote_10_Slices() throws Exception {
		testWordcountRemote(10, 1e-2);
	}

	@Test
	public void testWordcountRemote_100_Slices() throws Exception {
		testWordcountRemote(100, 1e-3);
	}

	@Test
	public void testWordcountRemote_200_Slices() throws Exception {
		testWordcountRemote(200, 1e-3);
	}

	private void testWordcountRemote(int nSlices, double allowedDelta) throws Exception {
		RemoteSparkTestRunner runner = createTestRunner(nSlices);
		runner.executeSparkRemote(String.valueOf(nSlices));
		runner.waitForSparkFinished();
		Double result = runner.getRemoteResult();
		runner.writeRemoteResultAsStringToFile(result);
		LOGGER.info("Result of Pi with {} number of slices: {}", nSlices, result);
		LOGGER.info("Difference from real Pi: {} ", Math.abs(Math.PI - result));
		assertEquals(Math.PI, result.doubleValue(), allowedDelta);
	}

	private static RemoteSparkTestRunner createTestRunner(int nSlices) {
		String testcaseId = "WordCount-" + nSlices;
		RemoteSparkTestRunner runner = new RemoteSparkTestRunner(JavaSparkPiRemoteTest.class, testcaseId);
		return runner;
	}

	public static void main(String[] args) throws Exception {
		HdfsUtils.pingHdfs();

		int nSlices = Integer.parseInt(args[0]);

		JavaSparkPi sparkPi = new JavaSparkPi(nSlices);
		RemoteSparkTestRunner runner = createTestRunner(nSlices);
		runner.executeWithStatusTracking(sparkPi);
	}

}
