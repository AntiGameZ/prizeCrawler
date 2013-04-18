package com.ruyicai.prizecrawler.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tprizeinfo")
public class PrizeInfo {

	@Id
	@GeneratedValue
	private int id;
	
	@Column(length=20)
	private String lotno;
	
	@Column(length=20)
	private String batchcode;
	
	@Column(length=50)
	private String winbasecode;
	
	@Column(length=20)
	private String winspecialcode;
	
	private Date createdate;
	private Date crawldate;
	private Date noticedate;
	private int noticeState;//0创建，1成功，-1失败
	private int noticenum;//次数，0创建
	private int crawlState;//0创建，1成功，-1失败
	private int crawlnum;//次数，0创建
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public Date getCreatedate() {
		return createdate;
	}
	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}
	public int getNoticeState() {
		return noticeState;
	}
	public void setNoticeState(int noticeState) {
		this.noticeState = noticeState;
	}
	public int getNoticenum() {
		return noticenum;
	}
	public void setNoticenum(int noticenum) {
		this.noticenum = noticenum;
	}
	public int getCrawlState() {
		return crawlState;
	}
	public void setCrawlState(int crawlState) {
		this.crawlState = crawlState;
	}
	public int getCrawlnum() {
		return crawlnum;
	}
	public void setCrawlnum(int crawlnum) {
		this.crawlnum = crawlnum;
	}
	public Date getCrawldate() {
		return crawldate;
	}
	public void setCrawldate(Date crawldate) {
		this.crawldate = crawldate;
	}
	public Date getNoticedate() {
		return noticedate;
	}
	public void setNoticedate(Date noticedate) {
		this.noticedate = noticedate;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((batchcode == null) ? 0 : batchcode.hashCode());
		result = prime * result + crawlState;
		result = prime * result + crawlnum;
		result = prime * result
				+ ((createdate == null) ? 0 : createdate.hashCode());
		result = prime * result + id;
		result = prime * result + ((lotno == null) ? 0 : lotno.hashCode());
		result = prime * result + noticeState;
		result = prime * result + noticenum;
		result = prime * result
				+ ((winbasecode == null) ? 0 : winbasecode.hashCode());
		result = prime * result
				+ ((winspecialcode == null) ? 0 : winspecialcode.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrizeInfo other = (PrizeInfo) obj;
		if (batchcode == null||other.batchcode==null) {
			return false;
		} else if (!batchcode.equals(other.batchcode))
			return false;
		if (lotno == null||other.lotno==null) {
			return false;
		} else if (!lotno.equals(other.lotno))
			return false;
		if (winbasecode == null||other.winbasecode==null) {
			return false;
		} else if (winbasecode.trim().length()==0||other.winbasecode.trim().length()==0) {
			return false;
		} else if (!winbasecode.equals(other.winbasecode)) {
			return false;
		}
		if (winspecialcode == null||other.winspecialcode==null) {
			return false;
		} else if (!winspecialcode.equals(other.winspecialcode))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "lotno="+lotno+" batchcode="+batchcode+" winbasecode="+winbasecode+" winspecialcode="+winspecialcode;
	}
	
	
	
	


	
	
	
	
}
