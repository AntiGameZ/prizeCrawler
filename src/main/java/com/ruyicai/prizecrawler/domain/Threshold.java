package com.ruyicai.prizecrawler.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="tthreshold",uniqueConstraints={@UniqueConstraint(columnNames={"lotno","type"})})
public class Threshold {

	@Id
	@GeneratedValue
	private int id;
	
	@Column(length=10,name="lotno")
	private String lotno;
	
	@Column(name="type",length=10)
	private String type;
	
	@Column(unique=true)
	private double threshold;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	
}
