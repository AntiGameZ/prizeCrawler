package com.ruyicai.prizecrawler.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tnotification")
public class Notification {

	@Id
	@GeneratedValue
	private int id;
	private String type;//code、info
	private String lotno;
	private String batchcode;
	
	@Column(length=400)
	private String info;
	private String winbasecode;
	private String winspecialcode;
	private double threshold;
	private String agencynos;
	private Date noticedate;
	private int noticetimes;
	private int noticestate;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLotno() {
		return lotno;
	}
	public void setLotno(String lotno) {
		this.lotno = lotno;
	}
	public String getBatchcode() {
		return batchcode;
	}
	public void setBatchcode(String batchcode) {
		this.batchcode = batchcode;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getWinbasecode() {
		return winbasecode;
	}
	public void setWinbasecode(String winbasecode) {
		this.winbasecode = winbasecode;
	}
	public String getWinspecialcode() {
		return winspecialcode;
	}
	public void setWinspecialcode(String winspecialcode) {
		this.winspecialcode = winspecialcode;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	public String getAgencynos() {
		return agencynos;
	}
	public void setAgencynos(String agencynos) {
		this.agencynos = agencynos;
	}
	public Date getNoticedate() {
		return noticedate;
	}
	public void setNoticedate(Date noticedate) {
		this.noticedate = noticedate;
	}
	public int getNoticetimes() {
		return noticetimes;
	}
	public void setNoticetimes(int noticetimes) {
		this.noticetimes = noticetimes;
	}
	public int getNoticestate() {
		return noticestate;
	}
	public void setNoticestate(int noticestate) {
		this.noticestate = noticestate;
	}
	@Override
	public String toString() {
		return "Notification [id=" + id + ", type=" + type + ", lotno=" + lotno
				+ ", batchcode=" + batchcode + ", info=" + info
				+ ", winbasecode=" + winbasecode + ", winspecialcode="
				+ winspecialcode + ", threshold=" + threshold + ", agencynos="
				+ agencynos + ", noticedate=" + noticedate + ", noticetimes="
				+ noticetimes + ", noticestate=" + noticestate + "]";
	}
	
	
	
	
}
