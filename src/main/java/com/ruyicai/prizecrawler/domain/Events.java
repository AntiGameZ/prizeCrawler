package com.ruyicai.prizecrawler.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tevents")
public class Events {

	@Id
	@GeneratedValue
	private Integer id;
	
	private String shortname;
	
	private String name;
	
	private String level1;
	
	private String level2;
	
	private BigDecimal type;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLevel1() {
		return level1;
	}

	public void setLevel1(String level1) {
		this.level1 = level1;
	}

	public String getLevel2() {
		return level2;
	}

	public void setLevel2(String level2) {
		this.level2 = level2;
	}

	public BigDecimal getType() {
		return type;
	}

	public void setType(BigDecimal type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Events [id=" + id + ", shortname=" + shortname + ", name="
				+ name + ", level1=" + level1 + ", level2=" + level2
				+ ", type=" + type + "]";
	}

	
	
}
