package com.zuehlke.shmack.sparkjobs.infrastructure;

import java.io.OutputStream;

import org.apache.commons.exec.ExecuteStreamHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class OutputsToStringStreamHandler extends PumpStreamHandler implements ExecuteStreamHandler {

	public OutputsToStringStreamHandler() {
		super(new ByteArrayOutputStream(), new ByteArrayOutputStream());
	}

	public String getStandardOutput() {
		return toString(getOut());
	}

	public String getStandardError() {
		return toString(getErr());
	}

	private String toString(OutputStream out) {
		byte[] bytes = ((ByteArrayOutputStream) out).toByteArray();
		return new String(bytes);
	}

}
