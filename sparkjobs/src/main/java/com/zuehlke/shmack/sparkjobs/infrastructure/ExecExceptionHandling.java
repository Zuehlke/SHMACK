package com.zuehlke.shmack.sparkjobs.infrastructure;

public enum ExecExceptionHandling {
	THROW_EXCEPTION_IF_EXIT_CODE_NOT_0,
	RETURN_EXIT_CODE_WITHOUT_THROWING_EXCEPTION
}
