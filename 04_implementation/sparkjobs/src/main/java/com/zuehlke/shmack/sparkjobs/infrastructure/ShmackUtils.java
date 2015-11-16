package com.zuehlke.shmack.sparkjobs.infrastructure;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;

public class ShmackUtils {

	public static void syncFolderToMasterAndSlave(File localSrcDir, File targetDirectory)
			throws ExecuteException, IOException {
		syncFolderToMasterAndSlave(localSrcDir, targetDirectory, 0);
	}

	public static void syncFolderToMasterAndSlave(File localSrcDir, File targetDirectory, int slaveIndex)
			throws ExecuteException, IOException {
		runOnLocalhost("/bin/bash", "sync-to-dcos-master-and-slave.sh", localSrcDir.getAbsolutePath() + "/ ",
				targetDirectory.getAbsolutePath() + "/", String.valueOf(slaveIndex));
	}

	public static void syncFolderFromSlave(File remoteSrcDir, File localTargetDir)
			throws ExecuteException, IOException {
		syncFolderFromSlave(remoteSrcDir, localTargetDir, 0);
	}

	public static void syncFolderFromSlave(File remoteSrcDir, File localTargetDir, int slaveIndex)
			throws ExecuteException, IOException {
		runOnLocalhost("/bin/bash", "sync-from-slave-to-local.sh", remoteSrcDir.getAbsolutePath() + "/ ",
				localTargetDir.getAbsolutePath() + "/", String.valueOf(slaveIndex));
	}

	public static void syncFolderToMaster(File localSrcDir, File targetDirectory) throws ExecuteException, IOException {
		createDirectoryOnMasterIfNotExists(targetDirectory);
		runOnLocalhost("/bin/bash", "sync-to-dcos-master.sh", localSrcDir.getAbsolutePath() + "/ ",
				targetDirectory.getAbsolutePath() + "/");
	}

	public static void syncFolderFromMaster(File remoteSrcDir, File localTargetDir)
			throws ExecuteException, IOException {
		runOnLocalhost("/bin/bash", "sync-from-master-to-local.sh", remoteSrcDir.getAbsolutePath() + "/ ",
				localTargetDir.getAbsolutePath() + "/");
	}

	public static void syncFolderToHdfs(File localSrcDir, File hdfsTargetDirectory)
			throws ExecuteException, IOException {
		runOnLocalhost("/bin/bash", "sync-to-hdfs.sh", localSrcDir.getAbsolutePath() + "/ ",
				hdfsTargetDirectory.getAbsolutePath() + "/");
	}

	public static void syncFolderFromHdfs(File hdfsSrcDirectory, File localTargetDir)
			throws ExecuteException, IOException {
		runOnLocalhost("/bin/bash", "sync-from-hdfs-to-local.sh", hdfsSrcDirectory.getAbsolutePath() + "/ ",
				localTargetDir.getAbsolutePath() + "/");
	}

	/**
	 * @return the full HDFS-Path
	 */
	public static void copyFolderToHdfs(File localSrcDir, File hdfsTargetDirectory)
			throws ExecuteException, IOException {
		runOnLocalhost("/bin/bash", "copy-to-hdfs.sh", localSrcDir.getAbsolutePath() + "/ ",
				hdfsTargetDirectory.getAbsolutePath() + "/");
	}

	private static void createDirectoryOnMasterIfNotExists(File toCreate) throws ExecuteException, IOException {
		runOnMaster("mkdir", "-p", toCreate.getAbsolutePath());

	}

	public static String deleteFolderInHdfs(File hdfsTargetDirectory) throws ExecuteException, IOException {
		String hdfsPath = getHdfsPath(hdfsTargetDirectory);
		runOnMaster("hadoop", "fs", "-rm", "-f", "-r", hdfsPath);
		return hdfsPath;
	}

	public static String getHdfsPath(File hdfsTargetDirectory) {
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
		cmdLineOnLocalhost.addArgument("run-on-dcos-master.sh");
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

	private static ExecuteResult runOnLocalhost(ExecExceptionHandling exceptionHandling, CommandLine cmdLineOnLocalhost)
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
}
