package com.ruyicai.prizecrawler.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "tagency", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"agencyno", "lotno", "type" }) })
public class Agency {

	@Id
	@GeneratedValue
	private int id;

	@Column(length = 15)
	private String agencyno;

	@Column(length = 10)
	private String lotno;

	@Column(name = "type", length = 10)
	private String type;

	@Column(name = "iscrawl")
	private int iscrawl;

	@Column(length = 40)
	private String agencyname;

	private double weight;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAgencyno() {
		return agencyno;
	}

	public void setAgencyno(String agencyno) {
		this.agencyno = agencyno;
	}

	public String getLotno() {
		return lotno;
	}

	public void setLotno(String lotno) {
		this.lotno = lotno;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAgencyname() {
		return agencyname;
	}

	public void setAgencyname(String agencyname) {
		this.agencyname = agencyname;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public int getIscrawl() {
		return iscrawl;
	}

	public void setIscrawl(int iscrawl) {
		this.iscrawl = iscrawl;
	}

}
