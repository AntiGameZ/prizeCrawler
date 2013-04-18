package com.ruyicai.prizecrawler.exception;

import com.ruyicai.prizecrawler.consts.ErrorCode;

public class PrizeInfoNullException extends Exception{

	private static final long serialVersionUID = 1L;

	private ErrorCode errorCode;
	
	public PrizeInfoNullException(String msg) {
		super(msg);
	}
	
	public PrizeInfoNullException(String msg, Throwable e) {
		super(msg, e);
	}
	
	public PrizeInfoNullException(ErrorCode errorCode) {
		super(errorCode.memo);
		this.errorCode = errorCode;
	}
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
}
