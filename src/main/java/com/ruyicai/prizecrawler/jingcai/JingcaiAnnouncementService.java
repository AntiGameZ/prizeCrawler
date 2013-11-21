package com.ruyicai.prizecrawler.jingcai;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.jingcai.dao.TjingcaiDao;
import com.ruyicai.prizecrawler.util.SendSMS;

@Service
public class JingcaiAnnouncementService {

	private Logger logger = LoggerFactory.getLogger(JingcaiAnnouncementService.class);

	private static final String URL = "http://www.sporttery.cn/list/ann/list.html";
	
	private int timeout = 30000;
	
	@Autowired
	private TjingcaiDao tjingcaiDao;
	
	@Autowired
	private SendSMS sendSMS;
	
	public void process() {
		logger.info("开始获取竞彩公告");
		try {
			Document doc = getJsoupDocument(URL);
			List<Element> elements = doc.select(".SaleTxtBox");
			for(Element element : elements) {
				String title = element.select(".stitle").get(0).text();
				if(tjingcaiDao.findAnnouncement(title) > 0) {
					continue;
				} else {
					String text = element.select(".stext").get(0).text();
					logger.info("抓到新公告, title:{}, text:{}", new String[] {title, text});
					tjingcaiDao.persistAnnouncement(title, text);
					sendSMS.sendSMS(text);
				}
			}
		} catch (Exception e) {
			logger.error("获取竞彩公告出错");
		}
	}
	
	private Document getJsoupDocument(String url) throws Exception {
		return Jsoup.connect(url).timeout(timeout).get();
	}
}
