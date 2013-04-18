package com.ruyicai.prizecrawler.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.ruyicai.prizecrawler.domain.pk.AgencyPrizePK;

@Entity
@Table(name = "tagencyprizecode")
public class AgencyPrizeCode implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AgencyPrizePK id;

	@Column(length = 50)
	private String wincode;

	private Date createdate;

	private Date codedate;

	private int checkstate;
	private int crawlstate;
	private int crawltimes;

	private double weight;

	public AgencyPrizePK getId() {
		return id;
	}

	public void setId(AgencyPrizePK id) {
		this.id = id;
	}

	public String getWincode() {
		return wincode;
	}

	public void setWincode(String wincode) {
		this.wincode = wincode;
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

	public int getCrawlstate() {
		return crawlstate;
	}

	public void setCrawlstate(int crawlstate) {
		this.crawlstate = crawlstate;
	}

	public int getCrawltimes() {
		return crawltimes;
	}

	public void setCrawltimes(int crawltimes) {
		this.crawltimes = crawltimes;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Date getCodedate() {
		return codedate;
	}

	public void setCodedate(Date codedate) {
		this.codedate = codedate;
	}

	@Override
	public String toString() {
		return "AgencyPrizeCode [id=" + id + ", wincode=" + wincode
				+ ", createdate=" + createdate + ", codedate=" + codedate
				+ ", checkstate=" + checkstate + ", crawlstate=" + crawlstate
				+ ", crawltimes=" + crawltimes + ", weight=" + weight + "]";
	}

}
