package com.zuehlke.shmack.sparkjobs.base;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Progressable;

public class HdfsUtils {

	private static final String MESOS_HDFS_PREFIX = "hdfs://hdfs";

	/**
	 * @return
	 * @see http://stackoverflow.com/questions/16000840/write-a-file-in-hdfs-
	 *      with-java
	 * @see https://sites.google.com/site/hadoopandhive/home/how-to-write-a-file
	 *      -in-hdfs-using-hadoop
	 * @return the hdfs-path of the written file
	 */
	public static String writeStringToHdfsFile(File file, String fileContent, Charset charset)
			throws IOException, URISyntaxException {
		String hdfsPath = MESOS_HDFS_PREFIX + file.getAbsolutePath();
		Configuration configuration = new Configuration();
		try (FileSystem hdfs = FileSystem.get(new URI(MESOS_HDFS_PREFIX), configuration)) {
			Path path = new Path(hdfsPath);
			if (hdfs.exists(path)) {
				hdfs.delete(path, true);
			}
			OutputStream os = hdfs.create(path, new Progressable() {
				public void progress() {
					// TODO
				}
			});
			try (BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os, charset))) {
				br.write(fileContent);
			}
		}
		return hdfsPath;
	}

	/**
	 * @return 
	 * @return the hdfs-path of the written file
	 */
	public static String writeObjectToHdfsFile(File file, Serializable fileContent, Charset charset)
			throws IOException, URISyntaxException {
		String hdfsPath = MESOS_HDFS_PREFIX + file.getAbsolutePath();
		Configuration configuration = new Configuration();
		try (FileSystem hdfs = FileSystem.get(new URI(MESOS_HDFS_PREFIX), configuration)) {
			Path path = new Path(hdfsPath);
			if (hdfs.exists(path)) {
				hdfs.delete(path, true);
			}
			OutputStream os = hdfs.create(path, new Progressable() {
				public void progress() {
					// TODO
				}
			});
			try (ObjectOutputStream oos = new ObjectOutputStream(os)) {
				oos.writeObject(fileContent);
			}
		}
		return hdfsPath;
	}

	/**
	 * This method is a workaround to access HDFS-Files from within Spark-Jobs.
	 * @see https://github.com/Zuehlke/SHMACK/blob/master/03_analysis_design/Issues/Issue-7%20Spark%20Word%20Count/Hostname-Not-Found-in-Spark-Job.docx
	 */
	public static void pingHdfs() throws IOException, URISyntaxException {
		File textFile = new File("/tmp/ping.txt");
		writeStringToHdfsFile(textFile, "Ping OK", StandardCharsets.UTF_8);
	}

}
