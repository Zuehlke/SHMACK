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
		String line = "/bin/bash sync-to-dcos-master-and-slave.sh " + localSrcDir.getAbsolutePath() + "/ "
				+ targetDirectory.getAbsolutePath() + "/ " + slaveIndex;
		CommandLine cmdLine = CommandLine.parse(line);
		execute(cmdLine);
	}

	public static void syncFolderFromSlave(File remoteSrcDir, File localTargetDir)
			throws ExecuteException, IOException {
		syncFolderFromSlave(remoteSrcDir, localTargetDir, 0);
	}

	public static void syncFolderFromSlave(File remoteSrcDir, File localTargetDir, int slaveIndex)
			throws ExecuteException, IOException {
		String line = "/bin/bash sync-from-slave-to-local.sh " + remoteSrcDir.getAbsolutePath() + "/ "
				+ localTargetDir.getAbsolutePath() + "/ " + slaveIndex;
		CommandLine cmdLine = CommandLine.parse(line);
		execute(cmdLine);
	}

	public static void syncFolderToMaster(File localSrcDir, File targetDirectory) throws ExecuteException, IOException {
		String line = "/bin/bash sync-to-dcos-master.sh " + localSrcDir.getAbsolutePath() + "/ "
				+ targetDirectory.getAbsolutePath() + "/";
		CommandLine cmdLine = CommandLine.parse(line);
		execute(cmdLine);
	}

	public static void syncFolderFromMaster(File remoteSrcDir, File localTargetDir)
			throws ExecuteException, IOException {
		String line = "/bin/bash sync-from-master-to-local.sh " + remoteSrcDir.getAbsolutePath() + "/ "
				+ localTargetDir.getAbsolutePath() + "/";
		CommandLine cmdLine = CommandLine.parse(line);
		execute(cmdLine);
	}

	/**
	 * @return the full HDFS-Path
	 */
	public static String copyFolderToHdfs(File localSrcDir, File hdfsTargetDirectory)
			throws ExecuteException, IOException {
		File intermediateDirOnMaster = new File("/tmp/hdfs-xchange" + hdfsTargetDirectory.getAbsolutePath());
		syncFolderToMaster(localSrcDir, intermediateDirOnMaster);
		String hdfsPath = "hdfs://hdfs" + hdfsTargetDirectory.getAbsolutePath();
		runOnMaster("hadoop", "fs", "-copyFromLocal", "-f", "-p", intermediateDirOnMaster.getAbsolutePath(), hdfsPath);
		return hdfsPath;
	}

	public static ExecuteResult runOnMaster(String executable, String... arguments)
			throws ExecuteException, IOException {
		return runOnMaster(ExecExceptionHandling.THROW_EXCEPTION_IF_EXIT_CODE_NOT_0, executable, arguments);
	}

	public static ExecuteResult runOnMaster(ExecExceptionHandling exceptionHandling, String executable,
			String... arguments) throws ExecuteException, IOException {
		CommandLine cmdLine = new CommandLine("/bin/bash");
		cmdLine.addArgument("run-on-dcos-master.sh");
		cmdLine.addArgument(executable);
		cmdLine.addArguments(arguments);
		ExecuteResult result = execute(cmdLine, exceptionHandling);
		return result;
	}

	private static ExecuteResult execute(CommandLine cmdLine) throws ExecuteException, IOException {
		return execute(cmdLine, ExecExceptionHandling.THROW_EXCEPTION_IF_EXIT_CODE_NOT_0);
	}

	private static ExecuteResult execute(CommandLine cmdLine, ExecExceptionHandling exceptionHandling)
			throws ExecuteException, IOException {
		DefaultExecutor executor = new DefaultExecutor();
		OutputsToStringStreamHandler streamHandler = new OutputsToStringStreamHandler();
		executor.setStreamHandler(streamHandler);
		executor.setExitValues(null);
		int exitValue = executor.execute(cmdLine);
		ExecuteResult result = new ExecuteResult(streamHandler.getStandardOutput(), streamHandler.getStandardError(),
				exitValue);
		switch (exceptionHandling) {
		case RETURN_EXIT_CODE_WITHOUT_THROWING_EXCEPTION:
			break;
		case THROW_EXCEPTION_IF_EXIT_CODE_NOT_0:
			if (exitValue != 0) {
				throw new ExecuteException("Failed to execute " + cmdLine + " - Output: " + result, exitValue);
			}
			break;
		default:
			break;
		}
		return result;
	}
}
