package com.ruyicai.prizecrawler.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="emailstate")
public class EmailNotice {

	@Id
	private String lotno;
	private int ismail;
	private int crawltimes;
	private int noticetimes;
	public String getLotno() {
		return lotno;
	}
	public void setLotno(String lotno) {
		this.lotno = lotno;
	}
	public int getIsmail() {
		return ismail;
	}
	public void setIsmail(int ismail) {
		this.ismail = ismail;
	}
	public int getCrawltimes() {
		return crawltimes;
	}
	public void setCrawltimes(int crawltimes) {
		this.crawltimes = crawltimes;
	}
	public int getNoticetimes() {
		return noticetimes;
	}
	public void setNoticetimes(int noticetimes) {
		this.noticetimes = noticetimes;
	}
	
}
