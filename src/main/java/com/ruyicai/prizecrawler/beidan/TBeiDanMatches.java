package com.ruyicai.prizecrawler.beidan;

import java.math.BigDecimal;
import java.util.Date;

public class TBeiDanMatches {

	// 彩种
	private String lotno;
	// 期号
	private String batchcode;
	// 场次编号
	private String no;
	// 赛事名称
	private String leaguename;
	// 主队简称
	private String host;
	// 客队简称
	private String guest;
	// 获取时间
	private Date createtime;
	// 截止时间
	private Date endtime;
	// 让球数
	private BigDecimal handicap;
	// 场次状态（正常、截止、开奖、算奖结束）
	private BigDecimal state;
	// 场次销售状态
	private BigDecimal salestate;
	
	private BigDecimal audit;
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
	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getLeaguename() {
		return leaguename;
	}
	public void setLeaguename(String leaguename) {
		this.leaguename = leaguename;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getGuest() {
		return guest;
	}
	public void setGuest(String guest) {
		this.guest = guest;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getEndtime() {
		return endtime;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	public BigDecimal getHandicap() {
		return handicap;
	}
	public void setHandicap(BigDecimal handicap) {
		this.handicap = handicap;
	}
	public BigDecimal getState() {
		return state;
	}
	public void setState(BigDecimal state) {
		this.state = state;
	}
	
	public BigDecimal getSalestate() {
		return salestate;
	}
	public void setSalestate(BigDecimal salestate) {
		this.salestate = salestate;
	}
	public BigDecimal getAudit() {
		return audit;
	}
	public void setAudit(BigDecimal audit) {
		this.audit = audit;
	}
	@Override
	public String toString() {
		return "TBeiDanMatches [lotno=" + lotno + ", batchcode=" + batchcode
				+ ", no=" + no + ", leaguename=" + leaguename + ", host="
				+ host + ", guest=" + guest + ", createtime=" + createtime
				+ ", endtime=" + endtime + ", handicap=" + handicap
				+ ", state=" + state + ", salestate=" + salestate + ", audit="
				+ audit + "]";
	}
	
	
	
	
	
}
