package com.zuehlke.shmack.sparkjobs.base;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.Progressable;

public class HdfsUtils {

	private static final String MESOS_HDFS_PREFIX = "hdfs://hdfs";

	/**
	 * @see http://stackoverflow.com/questions/16000840/write-a-file-in-hdfs-
	 *      with-java
	 * @see https://sites.google.com/site/hadoopandhive/home/how-to-write-a-file
	 *      -in-hdfs-using-hadoop
	 */
	public static void writeStringToHdfsFile(File file, String fileContent, Charset charset)
			throws IOException, URISyntaxException {
		Configuration configuration = new Configuration();
		try (FileSystem hdfs = FileSystem.get(new URI(MESOS_HDFS_PREFIX), configuration)) {
			Path path = new Path(MESOS_HDFS_PREFIX + file.getAbsolutePath());
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
	}

}
