package com.zuehlke.shmack.sparkjobs.infrastructure;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.io.FileUtils;

public class ShmackUtils {
	
	private static String scriptDir;

	public static String determineScriptDir() {
		if (scriptDir == null){
			try {
				String workingDir = runOnLocalhost("/bin/pwd").getStandardOutput();
				int idx = workingDir.indexOf("/04_implementation");
				if (idx > 0) {
					scriptDir = workingDir.substring(0, idx) + "/04_implementation/scripts/";
				} else {
					scriptDir = "";
				}
			} catch (IOException e) {
				e.printStackTrace();
				scriptDir = "";
			}
		}
		return scriptDir;
	}

	public static void syncFolderToMasterAndSlave(File localSrcDir, File targetDirectory)
			throws ExecuteException, IOException {
		syncFolderToMasterAndSlave(localSrcDir, targetDirectory, 0);
	}

	public static void syncFolderToMasterAndSlave(File localSrcDir, File targetDirectory, int slaveIndex)
			throws ExecuteException, IOException {
		runOnLocalhost("/bin/bash", determineScriptDir() + "sync-to-dcos-master-and-slave.sh", localSrcDir.getAbsolutePath() + "/ ",
				targetDirectory.getAbsolutePath() + "/", String.valueOf(slaveIndex));
	}

	public static void syncFolderFromSlave(File remoteSrcDir, File localTargetDir)
			throws ExecuteException, IOException {
		syncFolderFromSlave(remoteSrcDir, localTargetDir, 0);
	}

	public static void syncFolderFromSlave(File remoteSrcDir, File localTargetDir, int slaveIndex)
			throws ExecuteException, IOException {
		runOnLocalhost("/bin/bash", determineScriptDir() + "sync-from-slave-to-local.sh", remoteSrcDir.getAbsolutePath() + "/ ",
				localTargetDir.getAbsolutePath() + "/", String.valueOf(slaveIndex));
	}

	public static void syncFolderToMaster(File localSrcDir, File targetDirectory) throws ExecuteException, IOException {
		createDirectoryOnMasterIfNotExists(targetDirectory);
		runOnLocalhost("/bin/bash", determineScriptDir() + "sync-to-dcos-master.sh", localSrcDir.getAbsolutePath() + "/ ",
				targetDirectory.getAbsolutePath() + "/");
	}

	public static void syncFolderFromMaster(File remoteSrcDir, File localTargetDir)
			throws ExecuteException, IOException {
		runOnLocalhost("/bin/bash", determineScriptDir() + "sync-from-master-to-local.sh", remoteSrcDir.getAbsolutePath() + "/ ",
				localTargetDir.getAbsolutePath() + "/");
	}

	public static ExecuteResult syncFolderToHdfs(File localSrcDir, File hdfsTargetDirectory)
			throws ExecuteException, IOException {
		return runOnLocalhost("/bin/bash", determineScriptDir() + "sync-to-hdfs.sh", localSrcDir.getAbsolutePath() + "/ ",
				hdfsTargetDirectory.getAbsolutePath() + "/");
	}

	public static ExecuteResult syncFolderFromHdfs(File hdfsSrcDirectory, File localTargetDir)
			throws ExecuteException, IOException {
		return runOnLocalhost("/bin/bash", determineScriptDir() + "sync-from-hdfs-to-local.sh", hdfsSrcDirectory.getAbsolutePath() + "/ ",
				localTargetDir.getAbsolutePath() + "/");
	}

	private static void createDirectoryOnMasterIfNotExists(File toCreate) throws ExecuteException, IOException {
		runOnMaster("mkdir", "-p", toCreate.getAbsolutePath());

	}

	public static String deleteInHdfs(File hdfsFileOrFolderToDelete) throws ExecuteException, IOException {
		String hdfsPath = getHdfsURL(hdfsFileOrFolderToDelete);
		runOnMaster("hadoop", "fs", "-rm", "-f", "-r", hdfsPath);
		return hdfsPath;
	}

	public static String getHdfsURL(File hdfsTargetDirectory) {
		return "hdfs://hdfs" + hdfsTargetDirectory.getAbsolutePath();
	}

	public static ExecuteResult runOnMaster(String executable, String... arguments)
			throws ExecuteException, IOException {
		return runOnMaster(ExecExceptionHandling.THROW_EXCEPTION_IF_EXIT_CODE_NOT_0, executable, arguments);
	}

	public static ExecuteResult runOnMaster(ExecExceptionHandling exceptionHandling, String executable,
			String... arguments) throws ExecuteException, IOException {
		CommandLine cmdLine = new CommandLine(executable);
		cmdLine.addArguments(arguments);
		ExecuteResult result = runOnMaster(cmdLine, exceptionHandling);
		return result;
	}

	private static ExecuteResult runOnMaster(CommandLine cmdLineOnMaster, ExecExceptionHandling exceptionHandling)
			throws ExecuteException, IOException {

		CommandLine cmdLineOnLocalhost = new CommandLine("/bin/bash");
		cmdLineOnLocalhost.addArgument(determineScriptDir() + "run-on-dcos-master.sh");
		cmdLineOnLocalhost.addArgument(cmdLineOnMaster.getExecutable());
		cmdLineOnLocalhost.addArguments(cmdLineOnMaster.getArguments());

		return runOnLocalhost(exceptionHandling, cmdLineOnLocalhost);
	}

	public static ExecuteResult runOnLocalhost(String executable, String... arguments)
			throws ExecuteException, IOException {
		return runOnLocalhost(ExecExceptionHandling.THROW_EXCEPTION_IF_EXIT_CODE_NOT_0, executable, arguments);
	}

	public static ExecuteResult runOnLocalhost(ExecExceptionHandling exceptionHandling, String executable,
			String... arguments) throws ExecuteException, IOException {
		CommandLine cmdLine = new CommandLine(executable);
		cmdLine.addArguments(arguments);
		ExecuteResult result = runOnLocalhost(exceptionHandling, cmdLine);
		return result;

	}

	public static ExecuteResult runOnLocalhost(ExecExceptionHandling exceptionHandling, CommandLine cmdLineOnLocalhost)
			throws ExecuteException, IOException {
		DefaultExecutor executor = new DefaultExecutor();
		OutputsToStringStreamHandler streamHandler = new OutputsToStringStreamHandler();
		executor.setStreamHandler(streamHandler);
		executor.setExitValues(null);
		int exitValue = executor.execute(cmdLineOnLocalhost);
		ExecuteResult result = new ExecuteResult(streamHandler.getStandardOutput(), streamHandler.getStandardError(),
				exitValue);
		switch (exceptionHandling) {
		case RETURN_EXIT_CODE_WITHOUT_THROWING_EXCEPTION:
			break;
		case THROW_EXCEPTION_IF_EXIT_CODE_NOT_0:
			if (exitValue != 0) {
				throw new ExecuteException("Failed to execute " + cmdLineOnLocalhost + " - Output: " + result,
						exitValue);
			}
			break;
		default:
			break;
		}
		return result;
	}

	public static void copyToHdfs(File localSrcFile, File hdfsTargetFile) throws ExecuteException, IOException {
		runOnLocalhost("/bin/bash", determineScriptDir() + "copy-to-hdfs.sh", localSrcFile.getAbsolutePath(),
				hdfsTargetFile.getAbsolutePath());
	}

	public static void copyFromHdfs(File hdfsSrcFile, File localTargetFile) throws ExecuteException, IOException {
		runOnLocalhost("/bin/bash", determineScriptDir() + "copy-from-hdfs.sh", hdfsSrcFile.getAbsolutePath(),
				localTargetFile.getAbsolutePath());
	}

	public static void writeStringToHdfs(File targetFile, String fileContent) throws IOException {
		File tmpFile = File.createTempFile("copy-to-hdfs", null);
		FileUtils.writeStringToFile(tmpFile, fileContent, StandardCharsets.UTF_8);
		copyToHdfs(tmpFile, targetFile);
		FileUtils.deleteQuietly(tmpFile);
	}

	public static String readStringFromHdfs(File srcFile) throws IOException {
		byte[] bytes = readByteArrayFromHdfs(srcFile);
		return new String( bytes, StandardCharsets.UTF_8);
	}

	public static byte[] readByteArrayFromHdfs(File srcFile) throws IOException {
		File tmpFile = File.createTempFile("read-from-hdfs", null);
		try {
			copyFromHdfs(srcFile, tmpFile);
			return FileUtils.readFileToByteArray(tmpFile);
		} finally {
			FileUtils.deleteQuietly(tmpFile);
		}
	}

}
