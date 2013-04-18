package com.ruyicai.prizecrawler.domain;

import java.math.BigDecimal;

public class TjingcaiGYJMatch {

	private long id;

	private String saishi;

	private BigDecimal type;

	private String number;

	private String team;

	private BigDecimal state;

	private String award;

	private String popularityRating;

	private String probability;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getType() {
		return type;
	}

	public void setType(BigDecimal type) {
		this.type = type;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public BigDecimal getState() {
		return state;
	}

	public void setState(BigDecimal state) {
		this.state = state;
	}

	public String getAward() {
		return award;
	}

	public void setAward(String award) {
		this.award = award;
	}

	public String getPopularityRating() {
		return popularityRating;
	}

	public void setPopularityRating(String popularityRating) {
		this.popularityRating = popularityRating;
	}

	public String getProbability() {
		return probability;
	}

	public void setProbability(String probability) {
		this.probability = probability;
	}

	public String getSaishi() {
		return saishi;
	}

	public void setSaishi(String saishi) {
		this.saishi = saishi;
	}

	@Override
	public String toString() {
		return "TjingcaiGYJMatch [saishi=" + saishi + ", type=" + type
				+ ", number=" + number + ", team=" + team + ", state=" + state
				+ ", award=" + award + ", popularityRating=" + popularityRating
				+ ", probability=" + probability + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((award == null) ? 0 : award.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime
				* result
				+ ((popularityRating == null) ? 0 : popularityRating.hashCode());
		result = prime * result
				+ ((probability == null) ? 0 : probability.hashCode());
		result = prime * result + ((saishi == null) ? 0 : saishi.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		TjingcaiGYJMatch other = (TjingcaiGYJMatch) obj;
		if (award == null) {
			if (other.award != null)
				return false;
		} else if (!award.equals(other.award))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (popularityRating == null) {
			if (other.popularityRating != null)
				return false;
		} else if (!popularityRating.equals(other.popularityRating))
			return false;
		if (probability == null) {
			if (other.probability != null)
				return false;
		} else if (!probability.equals(other.probability))
			return false;
		if (saishi == null) {
			if (other.saishi != null)
				return false;
		} else if (!saishi.equals(other.saishi))
			return false;
//		if (state == null) {
//			if (other.state != null)
//				return false;
//		} else if (!state.equals(other.state))
//			return false;
		if (team == null) {
			if (other.team != null)
				return false;
		} else if (!team.equals(other.team))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
