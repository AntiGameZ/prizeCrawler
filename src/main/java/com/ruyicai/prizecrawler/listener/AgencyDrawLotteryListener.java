package com.ruyicai.prizecrawler.listener;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.Lottype;
import com.ruyicai.prizecrawler.domain.AgencyPrizeCode;
import com.ruyicai.prizecrawler.domain.AgencyPrizeInfo;
import com.ruyicai.prizecrawler.domain.pk.AgencyPrizePK;
import com.ruyicai.prizecrawler.enums.CheckStateType;
import com.ruyicai.prizecrawler.enums.CrawkStateType;
import com.ruyicai.prizecrawler.producer.CheckCodeProducer;
import com.ruyicai.prizecrawler.producer.CheckInfoProducer;
import com.ruyicai.prizecrawler.service.AgencyPrizeCodeService;
import com.ruyicai.prizecrawler.service.AgencyPrizeInfoService;
import com.ruyicai.prizecrawler.util.HttpTookit;

@Service("agencyDrawLotteryListener")
public class AgencyDrawLotteryListener implements MessageListener{
	private static Logger logger = LoggerFactory.getLogger(AgencyDrawLotteryListener.class);
	
	@Autowired
	private Map<String,Lottype> lottypes;
	
	@Resource
	private AgencyPrizeCodeService agencyPrizeCodeService;
	
	@Resource
	private AgencyPrizeInfoService agencyPrizeInfoService;
	
	@Resource
	private CheckCodeProducer checkCodeProducer;
	
	@Resource
	private CheckInfoProducer checkInfoProducer;

	@Override
	public void onMessage(Message message) {
		
			logger.info("reciveQueue-agencyDrawLottery");
			Map<String, Object> map = null;
			try {
				map = ((ActiveMQMessage)message).getProperties();
			} catch (Exception e) {
				logger.error("agencyDrawLottery转换message出错",e);
			}
			
			String lotno = map.get("lotno")==null?"":map.get("lotno").toString();
			logger.debug("lotno:"+lotno);
			String batchcode = map.get("batchcode")==null?"":map.get("batchcode").toString();
			logger.debug("batchcode:"+batchcode);
			String agencyno = map.get("agencyno")==null?"":map.get("agencyno").toString();
			logger.debug("agencyno:"+agencyno);
			String winbasecode = map.get("winbasecode")==null?"":map.get("winbasecode").toString();
			logger.debug("winbasecode:"+winbasecode);
			String winspecialcode = map.get("winspecialcode")==null?"":map.get("winspecialcode").toString();
			logger.debug("winspecialcode:"+winspecialcode);
			String info = map.get("info")==null?"":map.get("info").toString();
			logger.debug("info:"+info);
			String wincode = "";
			
			logger.info("agencyDrawLottery lotno="+lotno+" batchcode="+batchcode+" agencyno="+agencyno+" winbasecode="+winbasecode
					+" winspecialcode="+winspecialcode+" info="+info);
			
			
			if(lottypes.get(lotno)!=null) {
				wincode = lottypes.get(lotno).getWincode(winbasecode, winspecialcode);
			}
			
			
			
			if(lotno==null||"".equals(lotno)||batchcode==null||"".equals(batchcode)||winbasecode==null||"".equals(winbasecode)) {
				logger.info("重要信息为空，返回");
				return;
			}
			
			
			if(lottypes.get(lotno)!=null&&lottypes.get(lotno).isCheck()) {
				AgencyPrizePK agencyPrizePk = new AgencyPrizePK(agencyno, lotno, batchcode);
				
				AgencyPrizeCode agencyPrizeCode = new AgencyPrizeCode();
				agencyPrizeCode.setId(agencyPrizePk);
				agencyPrizeCode.setCheckstate(CheckStateType.WAIT.value);
				agencyPrizeCode.setCrawlstate(CrawkStateType.SUCCESS.value);
				agencyPrizeCode.setCrawltimes(0);
				agencyPrizeCode.setCreatedate(new Date());
				agencyPrizeCode.setWincode(wincode);
				agencyPrizeCode.setCodedate(new Date());
				
				logger.info("保存本条开奖号码信息");
				boolean codeFlag = agencyPrizeCodeService.mergeWithWeight(agencyPrizeCode);
				if(codeFlag) {
					logger.info("发送开奖号码到checkCodeQueue");
					checkCodeProducer.send(agencyno, lotno, batchcode, wincode);
				}
				
				
				if(!info.trim().equals("")) {
					logger.info("开奖详情信息不为空");
					AgencyPrizeInfo agencyPrizeInfo = new AgencyPrizeInfo();
					agencyPrizeInfo.setId(agencyPrizePk);
					agencyPrizeInfo.setCheckstate(CheckStateType.WAIT.value);
					agencyPrizeInfo.setCreatedate(new Date());
					agencyPrizeInfo.setWininfo(info);
					agencyPrizeInfo.setInfodate(new Date());
					
					logger.info("保存本条开奖详情信息");
					boolean infoFlag = agencyPrizeInfoService.mergeWithWeight(agencyPrizeInfo);
					if(infoFlag) {
						logger.info("发送开奖详情到checkInfoQueue");
						checkInfoProducer.send(agencyno, lotno, batchcode, info);
					}
				}
			}else {
				logger.info("通知其他彩种开奖开始lotno="+lotno +" batchcode="+batchcode + " winbasecode="+winbasecode +" winspecialcode="+winspecialcode + " info="+info);
				int total = 0;
				boolean noticecode = false;
				boolean noticeinfo = false;
				
				do{
					noticecode = false;
					noticeinfo = false;
					logger.info("开始通知第"+(total+1)+"次");
					noticecode = HttpTookit.noticeCode(lotno, batchcode, winbasecode, winspecialcode);
					logger.info("通知开奖号码结果为"+noticecode);
					noticeinfo = HttpTookit.noticeInfo(lotno, batchcode, info);
					logger.info("通知开奖详情结果为"+noticeinfo);
					
					if(noticecode&&noticeinfo) {
						logger.info("通知第"+(total+1)+"次成功");
					}else {
						logger.info("通知第"+(total+1)+"次失败");
					}
					
					total = total + 1;
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}while (total<10&&(!(noticecode&&noticeinfo)));
				
				logger.info("通知完成，退出通知");

			}
	}
	

}
