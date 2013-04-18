package com.ruyicai.prizecrawler.domain;

public class CheckResult {

	private double threshold;
	private String wincode;
	private String winPrizeInfo;
	private boolean ispass;
	private String agencynos;
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public String getWincode() {
		return wincode;
	}
	public void setWincode(String wincode) {
		this.wincode = wincode;
	}
	public String getWinPrizeInfo() {
		return winPrizeInfo;
	}
	public void setWinPrizeInfo(String winPrizeInfo) {
		this.winPrizeInfo = winPrizeInfo;
	}
	public boolean isIspass() {
		return ispass;
	}
	public void setIspass(boolean ispass) {
		this.ispass = ispass;
	}
	public String getAgencynos() {
		return agencynos;
	}
	public void setAgencynos(String agencynos) {
		this.agencynos = agencynos;
	}
	public CheckResult(double threshold, String wincode, String winPrizeInfo,
			boolean ispass) {
		super();
		this.threshold = threshold;
		this.wincode = wincode;
		this.winPrizeInfo = winPrizeInfo;
		this.ispass = ispass;
	}
	@Override
	public String toString() {
		return "CheckResult [threshold=" + threshold + ", wincode=" + wincode
				+ ", winPrizeInfo=" + winPrizeInfo + ", ispass=" + ispass
				+ ", agencynos=" + agencynos + "]";
	}
	
	
	
	
	
	
}
