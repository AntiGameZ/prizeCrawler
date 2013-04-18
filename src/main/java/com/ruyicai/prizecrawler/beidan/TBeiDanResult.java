package com.ruyicai.prizecrawler.beidan;

import java.math.BigDecimal;
import java.util.Date;

public class TBeiDanResult {

	//彩种
	private String lotno;
	//期号
	private String batchcode;
	//赛事编号
	private String no;
	//赛事取消（0未取消，1取消）
	private BigDecimal iscancel;
	//半场比分
	private String scorehalf;
	//全场比分
	private String scoreall;
	//赛果
	private String result;
	//赔率
	private String peilu;
	//让分
	private BigDecimal handicap;
	//创建时间
	private Date createtime;
	//审核时间
	private Date audittime;
	//审核状态
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
	public BigDecimal getIscancel() {
		return iscancel;
	}
	public void setIscancel(BigDecimal iscancel) {
		this.iscancel = iscancel;
	}
	public String getScorehalf() {
		return scorehalf;
	}
	public void setScorehalf(String scorehalf) {
		this.scorehalf = scorehalf;
	}
	public String getScoreall() {
		return scoreall;
	}
	public void setScoreall(String scoreall) {
		this.scoreall = scoreall;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getPeilu() {
		return peilu;
	}
	public void setPeilu(String peilu) {
		this.peilu = peilu;
	}
	public BigDecimal getHandicap() {
		return handicap;
	}
	public void setHandicap(BigDecimal handicap) {
		this.handicap = handicap;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getAudittime() {
		return audittime;
	}
	public void setAudittime(Date audittime) {
		this.audittime = audittime;
	}
	public BigDecimal getAudit() {
		return audit;
	}
	public void setAudit(BigDecimal audit) {
		this.audit = audit;
	}
	@Override
	public String toString() {
		return "TBeiDanResult [lotno=" + lotno + ", batchcode=" + batchcode
				+ ", no=" + no + ", iscancel=" + iscancel + ", scorehalf="
				+ scorehalf + ", scoreall=" + scoreall + ", result=" + result
				+ ", peilu=" + peilu + ", handicap=" + handicap
				+ ", createtime=" + createtime + ", audittime=" + audittime
				+ ", audit=" + audit + "]";
	}
	
}
