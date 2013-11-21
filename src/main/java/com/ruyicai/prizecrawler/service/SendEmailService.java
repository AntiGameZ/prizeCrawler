package com.ruyicai.prizecrawler.service;

import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.EmailNotice;
import com.ruyicai.prizecrawler.util.SendMailUtil;

@Service("sendEmailService")
@Transactional
public class SendEmailService {
	
	private static Logger logger = LoggerFactory.getLogger(SendEmailService.class);
	
	private static final String STATION = ResourceBundle.getBundle("station")
			.getString("mail.station");

	@PersistenceContext
	private EntityManager em;
	
	
	public void sendEmail(String lotno,int times,String batchcode,String type) {
		try {
			EmailNotice notice = findEmailNotice(lotno);
			if(notice==null) {
				return ;
			}
			
			if(notice.getIsmail()==SystemCode.MAILOPEN) {
				if(type.equals(SystemCode.TYPE_CRAWL)&&times>=notice.getCrawltimes()&&(times-notice.getCrawltimes()<=1)) {
					logger.info("sendemail"+"prizeCrawler crawl:"+lotno+"_"+batchcode+"_"+times);
					SendMailUtil.sendMail(STATION+" prizeCrawler crawl:"+lotno+"_"+batchcode+"_"+times, "prizeCrawler crawl");
				}else if(type.equals(SystemCode.TYPE_NOTICE)&&times>=notice.getNoticetimes()&&(times-notice.getNoticetimes()<=1)) {
					logger.info("sendemail"+"prizeCrawler notice:"+lotno+"_"+batchcode+"_"+times);
					SendMailUtil.sendMail(STATION+" prizeCrawler notice:"+lotno+"_"+batchcode+"_"+times, "prizeCrawler notice");
				}
			}
			
		}catch(Exception e) {
			logger.info("send mail err",e);
		}
	}
	
	
	
	@Transactional(readOnly = true)
	private EmailNotice findEmailNotice(String lotno) {
		TypedQuery<EmailNotice> query = em
				.createQuery(
						"select o from EmailNotice o where o.lotno=?",
						EmailNotice.class).setParameter(1, lotno);
		if(query.getResultList().size()!=1) {
			return null;
		}
		return query.getSingleResult();
	}
	
	
	public boolean isCrawl(String lotno) {
		try {
			EmailNotice notice = findEmailNotice(lotno);
			if(notice.getIscrawl()==SystemCode.CRAWL_CLOSE) {
				return false;
			}
		}catch(Exception e) {
			logger.info("isCrawl",e);
		}
		return true;
	}
}
