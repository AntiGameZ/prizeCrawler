package com.ruyicai.prizecrawler.domain;

import java.math.BigDecimal;
import java.util.Date;


public class TjingcaiScore {

	private String id;

	private BigDecimal cancel;

	private String result;

	private String firsthalfresult;
	
	private Date createtime;
	
	private Date audittime;
	
	private String auditname;
	
	private BigDecimal audit;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BigDecimal getCancel() {
		return cancel;
	}

	public void setCancel(BigDecimal cancel) {
		this.cancel = cancel;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getFirsthalfresult() {
		return firsthalfresult;
	}

	public void setFirsthalfresult(String firsthalfresult) {
		this.firsthalfresult = firsthalfresult;
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

	public String getAuditname() {
		return auditname;
	}

	public void setAuditname(String auditname) {
		this.auditname = auditname;
	}

	public BigDecimal getAudit() {
		return audit;
	}

	public void setAudit(BigDecimal audit) {
		this.audit = audit;
	}

	@Override
	public String toString() {
		return "TjingcaiScore [id=" + id + ", cancel=" + cancel + ", result="
				+ result + ", firsthalfresult=" + firsthalfresult
				+ ", createtime=" + createtime + ", audittime=" + audittime
				+ ", auditname=" + auditname + ", audit=" + audit + "]";
	}
	
	
	

}
