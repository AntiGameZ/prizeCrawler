package com.ruyicai.prizecrawler.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.ruyicai.prizecrawler.domain.pk.AgencyPrizePK;

@Entity
@Table(name = "tagencyprizeinfo")
public class AgencyPrizeInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AgencyPrizePK id;

	@Column(length = 400)
	private String wininfo;

	private Date createdate;

	private Date infodate;

	private int checkstate;
	private double weight;

	public AgencyPrizePK getId() {
		return id;
	}

	public void setId(AgencyPrizePK id) {
		this.id = id;
	}

	public String getWininfo() {
		return wininfo;
	}

	public void setWininfo(String wininfo) {
		this.wininfo = wininfo;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public int getCheckstate() {
		return checkstate;
	}

	public void setCheckstate(int checkstate) {
		this.checkstate = checkstate;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Date getInfodate() {
		return infodate;
	}

	public void setInfodate(Date infodate) {
		this.infodate = infodate;
	}

	@Override
	public String toString() {
		return "AgencyPrizeInfo [id=" + id + ", wininfo=" + wininfo
				+ ", createdate=" + createdate + ", infodate=" + infodate
				+ ", checkstate=" + checkstate + ", weight=" + weight + "]";
	}

}
