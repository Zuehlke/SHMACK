package com.zuehlke.shmack.sparkjobs.tutorials;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.shmack.sparkjobs.base.HdfsUtils;
import com.zuehlke.shmack.sparkjobs.base.RemoteSparkTestBase;

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
		executeSparkRemote(String.valueOf(nSlices));
		waitForSparkFinished();
		Double result = getRemoteResult();
		writeRemoteResultAsStringToFile(result);
		LOGGER.info("Result of Pi with {} number of slices: {}", nSlices, result);
		LOGGER.info("Difference from real Pi: {} ", Math.abs(Math.PI - result));
		assertEquals(Math.PI, result.doubleValue(), allowedDelta);
	}

	public static void main(String[] args) throws Exception {
		HdfsUtils.pingHdfs();

		int nSlices = Integer.parseInt(args[0]);

		JavaSparkPi sparkPi = new JavaSparkPi(nSlices);
		JavaSparkPiRemoteTest myTest = new JavaSparkPiRemoteTest();
		myTest.executeWithStatusTracking(sparkPi);
	}

}
