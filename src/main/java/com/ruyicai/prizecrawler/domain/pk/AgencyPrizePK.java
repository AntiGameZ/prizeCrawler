package com.ruyicai.prizecrawler.domain.pk;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AgencyPrizePK implements Serializable{


	private static final long serialVersionUID = 1L;

	@Column(length=15)
	private String agencyno;
	
	@Column(length=10)
	private String lotno;
	
	@Column(length=15)
	private String batchcode;

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

	public String getBatchcode() {
		return batchcode;
	}

	public void setBatchcode(String batchcode) {
		this.batchcode = batchcode;
	}

	public AgencyPrizePK(String agencyno, String lotno, String batchcode) {
		super();
		this.agencyno = agencyno;
		this.lotno = lotno;
		this.batchcode = batchcode;
	}

	public AgencyPrizePK() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((agencyno == null) ? 0 : agencyno.hashCode());
		result = prime * result
				+ ((batchcode == null) ? 0 : batchcode.hashCode());
		result = prime * result + ((lotno == null) ? 0 : lotno.hashCode());
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
		AgencyPrizePK other = (AgencyPrizePK) obj;
		if (agencyno == null) {
			if (other.agencyno != null)
				return false;
		} else if (!agencyno.equals(other.agencyno))
			return false;
		if (batchcode == null) {
			if (other.batchcode != null)
				return false;
		} else if (!batchcode.equals(other.batchcode))
			return false;
		if (lotno == null) {
			if (other.lotno != null)
				return false;
		} else if (!lotno.equals(other.lotno))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AgencyPrizePK [agencyno=" + agencyno + ", lotno=" + lotno
				+ ", batchcode=" + batchcode + "]";
	}
	
	
	
}
