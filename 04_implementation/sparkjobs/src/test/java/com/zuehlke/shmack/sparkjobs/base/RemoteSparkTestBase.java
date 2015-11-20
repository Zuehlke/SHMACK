package com.zuehlke.shmack.sparkjobs.base;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.exec.ExecuteException;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zuehlke.shmack.sparkjobs.infrastructure.ShmackUtils;

public class RemoteSparkTestBase extends ShmackTestBase {

	private final static Logger LOGGER = LoggerFactory.getLogger(RemoteSparkTestBase.class);

	private static final File HDFS_RESSOURCES_DIRECTORY = new File(RemoteSparkTestRunner.SPARK_TESTS_HDFS_FOLDER,
			"resources");

	protected static JavaSparkContext createSparkContext(String appName) {
		SparkConf sparkConf = new SparkConf().setAppName(appName);
		JavaSparkContext jsc = new JavaSparkContext(sparkConf);
		return jsc;
	}

	private static boolean ressourcesAlreadyInSync = false;

	protected final void executeWithStatusTracking(RemoteSparkTestRunner runner,
			TestableSparkJob<? extends Serializable> job) throws Exception {
		runner.executeWithStatusTracking(job);
	}

	protected void syncTestRessourcesToHdfs() throws ExecuteException, IOException {
		if (ressourcesAlreadyInSync) {
			LOGGER.info("SKIPPING copy test resources to HDFS as already done before.");
			return;
		}
		LOGGER.info("Copying test resources to HDFS: " + ShmackUtils.getHdfsURL( HDFS_RESSOURCES_DIRECTORY)) ;
		ShmackUtils.syncFolderToHdfs(new File("src/test/resources"), HDFS_RESSOURCES_DIRECTORY);
		ressourcesAlreadyInSync = true;
	}

	protected static String getHdfsTestRessourcePath(String relativeRessourcePath) {
		return "hdfs://hdfs" + HDFS_RESSOURCES_DIRECTORY.getAbsolutePath() + "/" + relativeRessourcePath;
	}

}
