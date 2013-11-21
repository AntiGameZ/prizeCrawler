package com.ruyicai.prizecrawler.beidan;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.beidan.dao.TBeidanDao;
import com.ruyicai.prizecrawler.util.SendSMS;

@Service
public class BeidanAnnouncementService {
	
	
	private Logger logger = LoggerFactory.getLogger(BeidanAnnouncementService.class);

	private static final String URL = "http://www.bjlot.com/List/List_100_1.shtml";
	
	@Autowired
	private TBeidanDao beidandao;
	
	@Autowired
	private SendSMS sendSMS;
	
	@Value("${msg.station}")
	private String msgstation;
	
	public void announcementTimer(){
		try {
			logger.info("开始抓取北单官方通知");
			Document doc = Jsoup.connect(URL).timeout(5000).get();
			
			Element announcement = doc.select(".main-height").get(0);
			
			Elements lis = announcement.select("div>ul>li");
			
			for(Element li:lis) {
				String content = li.text().trim();
				if(beidandao.findBeidanAnnouncement(content)==null) {
					logger.info("通知不存在{},保存并发送短信",new String[]{content});
					beidandao.persist(new TBeidanAnnouncement(content));
					sendSMS.sendSMS(msgstation+content);
				}else {
					logger.info("通知已经存在{}",new String[]{content});
				}
			}
		}catch(Exception e) {
			logger.info("抓取北单官方通知异常",e);
		}
	}
}
