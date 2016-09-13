package com.zuehlke.shmack.sparkjobs.infrastructure;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ExecuteResult {

	private String standardOutput;
	private String standardError;
	private int exitValue;

	public ExecuteResult(String standardOutput, String standardError, int exitValue) {
		super();
		this.standardOutput = standardOutput;
		this.standardError = standardError;
		this.exitValue = exitValue;
	}

	public String getStandardError() {
		return standardError;
	}

	public String getStandardOutput() {
		return standardOutput;
	}

	public int getExitValue() {
		return exitValue;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
