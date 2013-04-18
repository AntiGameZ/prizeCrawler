package com.ruyicai.prizecrawler.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.domain.AgencyPrizeInfo;
import com.ruyicai.prizecrawler.domain.CheckResult;
import com.ruyicai.prizecrawler.domain.Notification;
import com.ruyicai.prizecrawler.domain.pk.AgencyPrizePK;
import com.ruyicai.prizecrawler.enums.CheckStateType;
import com.ruyicai.prizecrawler.enums.NoticeStateType;
import com.ruyicai.prizecrawler.enums.NoticeType;
import com.ruyicai.prizecrawler.enums.WeightType;
import com.ruyicai.prizecrawler.producer.SMSProducer;
import com.ruyicai.prizecrawler.timer.NoticeTime;
import com.ruyicai.prizecrawler.util.CommonUtil;

@Service("prizeInfoCheckService")
public class PrizeInfoCheckService {
	
	private static Logger logger = LoggerFactory.getLogger(PrizeInfoCheckService.class);
	
	@Resource
	private ThresholdService thresholdService;
	@Resource
	private AgencyPrizeInfoService agencyPrizeInfoService;
	@Resource
	private NotificationService notificationService;
	@Resource
	private SMSProducer smsProducer;
	
	@Resource
	private NoticeTime noticeTimer;
	
	public void checkPrizeCode(String agencyno,String lotno,String batchcode,String wininfo) {
		
		logger.info("开始进行比对过程"+"agencyno="+agencyno+" lotno="+lotno+" batchcode="+batchcode+" wininfo="+wininfo);
		//获取当前agencyPrizeInfo列表
		logger.info("获取彩种"+lotno+"期号"+batchcode+"所有的AgencyPrizeInfo");
		List<AgencyPrizeInfo> agencyPrizeInfos = agencyPrizeInfoService.find(lotno, batchcode);
		logger.info("获取彩种"+lotno+"期号"+batchcode+"所有的AgencyPrizeInfo完成");
		
		
		//根据已经存在的记录的审核情况判断本条记录的审核情况
		logger.info("根据已经存在的记录的审核情况判断本条记录的审核情况");
		CheckStateType checkstate = changePrizeInfoCheckStateThroughExistRecord(wininfo, agencyPrizeInfos);
		logger.info("根据已经存在的记录的审核情况判断本条记录的审核情况为"+checkstate.memo);
		
		logger.info("更新本条记录审核情况");
		agencyPrizeInfoService.updateCheckState(new AgencyPrizePK(agencyno, lotno, batchcode), checkstate);
		
		//获取审核后的agencyPrizeInfo列表
		logger.info("获取彩种"+lotno+"期号"+batchcode+"审核后的所有的AgencyPrizeInfo");
		List<AgencyPrizeInfo> agencyPrizeInfosNew = agencyPrizeInfoService.find(lotno, batchcode);
		logger.info("获取彩种"+lotno+"期号"+batchcode+"审核后的所有的AgencyPrizeInfo完成");
		
		
		logger.info("判断开奖详情是否出现潜在危险，是否发送警报短信");
		if(isWarning(agencyPrizeInfosNew)) {
			logger.info("Attention:开奖信息出错", "彩种"+lotno+" 期号"+batchcode+" 渠道"+agencyno+"开奖信息警报");
			smsProducer.send("开奖信息WARNING", "彩种"+lotno+" 期号"+batchcode+" 渠道"+agencyno+"开奖信息警报");
		}
		
		
		//检查开奖详情是否出错(等待审核和审核成功的记录中存在不相同的开奖结果，发报警短信，并且方法返回，不进行阈值计算)
		logger.info("判断开奖详情是否出错，是否需要发送出错短信");
		if(isError(agencyPrizeInfosNew)) {
			logger.info("Attention:开奖信息出错", "彩种"+lotno+" 期号"+batchcode+" 渠道"+agencyno+"开奖信息出错");
			smsProducer.send("开奖信息ERROR", "彩种"+lotno+" 期号"+batchcode+" 渠道"+agencyno+"开奖信息出错");
			return;
		}
		logger.info("无需发送警出错短信");
		
		//计算阈值,判断是否开奖
		logger.info("开始获取彩种"+lotno+"开奖详情的阈值");
		Double threshold = thresholdService.find(lotno,WeightType.PRIZEINFO).getThreshold();
		logger.info("获取彩种"+lotno+"开奖详情的阈值为"+threshold);
		
		logger.info("开始根据权重计算是否可以进行开奖详情的通知");
		CheckResult cr = calThresholdPrizeInfo(agencyPrizeInfosNew, threshold);
		logger.info("根据权重计算是否可以进行开奖详情的通知,结果为"+cr.toString());
		if(cr.isIspass()) {
			//自动审核为成功
			logger.info("计算阈值为"+cr.getThreshold()+" 设定阈值为"+threshold+"可以进行开奖详情的通知");
			logger.info("开始审核所有通过的开奖详情为审核通过");
			agencyPrizeInfoService.checkWininfoSuccess(lotno, batchcode, cr.getWinPrizeInfo());
			logger.info("审核所有通过的开奖详情为审核通过完成");
			
			
			logger.info("开始创建并保存开奖详情信息");
			saveNotification(lotno, batchcode, cr);
			logger.info("创建并保存开奖详情信息成功");
			
			logger.info("开始通知");
			noticeTimer.noticeDaPan();
			logger.info("通知结束");
			
		}
		
	}


	private void saveNotification(String lotno, String batchcode, CheckResult cr) {
		Notification notification = new Notification();
		notification.setType(NoticeType.PRIZEINFO.value);
		notification.setAgencynos(cr.getAgencynos());
		notification.setBatchcode(batchcode);
		notification.setLotno(lotno);
		notification.setInfo(cr.getWinPrizeInfo());
		notification.setNoticedate(new Date());
		notification.setNoticestate(NoticeStateType.WAIT.value);
		notification.setNoticetimes(0);
		notification.setThreshold(cr.getThreshold());
		
		notificationService.merge(notification);
	}


	/**
	 * 通过原有记录修改状态
	 * 如果原有记录里有审核通过的，那么如果这个和审核通过的相同，设为通过，不同，设为不通过
	 * 如果原有记录里有审核未通过的，那么如果这个和审核未通过记录相同设为未通过
	 * 如果原有记录全为未审核，那么本条记录的审核状态不变
	 * @param agencyPrizeCode
	 * @param agencyPrizeCodes
	 * @return
	 */
	private CheckStateType changePrizeInfoCheckStateThroughExistRecord(String wininfo,List<AgencyPrizeInfo> agencyPrizeInfos) {
		
		CheckStateType checkstate = CheckStateType.WAIT;
		
		if(findSuccessWininfo(agencyPrizeInfos)!=null) {
			if(findSuccessWininfo(agencyPrizeInfos).equals(wininfo)) {
				checkstate = CheckStateType.PASS;
			}else {
				checkstate = CheckStateType.NOTPASS;
			}
		}else if(findFailWininfo(agencyPrizeInfos).size()>0) {
			for(String failinfo:findFailWininfo(agencyPrizeInfos)) {
				if(failinfo.equals(wininfo)) {
					checkstate = CheckStateType.NOTPASS;
				}
			}
		}
		return checkstate;
	}
	
	
	/**
	 * 判断是不是发送出错信息
	 * 当所有未审核和审核成功的开奖中存在不同的记录，发送出错信息
	 * @param agencyPrizeCodes
	 * @return
	 */
	private boolean isError(List<AgencyPrizeInfo> agencyPrizeInfos) {
		Set<String> set = new HashSet<String>();
		for(AgencyPrizeInfo a:agencyPrizeInfos) {
			if(a.getCheckstate()!=CheckStateType.NOTPASS.value&&a.getCheckstate()!=CheckStateType.CREATE.value) {
				set.add(a.getWininfo());
			}
		}
		if(set.size()>1) {
			logger.info("开奖详情存在不同的结果"+CommonUtil.setToString(set));
			return true;
		}
		logger.info("开奖详情结果相同"+CommonUtil.setToString(set));
		return false;
	}
	
	
	
	
	/**
	 * 判断是不是发送警告信息
	 * 当所有开奖中(状态为初始创建不包括)存在不同的记录，发送警告信息
	 * @param agencyPrizeCodes
	 * @return
	 */
	private boolean isWarning(List<AgencyPrizeInfo> agencyPrizeInfos) {
		Set<String> set = new HashSet<String>();
		for(AgencyPrizeInfo a:agencyPrizeInfos) {
			if(a.getCheckstate()!=CheckStateType.CREATE.value) {
				set.add(a.getWininfo());
			}
		}
		if(set.size()>1) {
			logger.info("开奖详情存在不同的结果"+CommonUtil.setToString(set));
			return true;
		}
		logger.info("开奖详情结果相同"+CommonUtil.setToString(set));
		return false;
	}
	
	
	/**
	 * 判断阈值
	 * 取到搜有审核成功和未审核的开奖号码记录，计算阈值
	 * 超过或等于设定阈值，返回true
	 * 否则返回false
	 * @param agencyPrizeCodes
	 * @param threshold
	 * @return
	 */
	private CheckResult calThresholdPrizeInfo(List<AgencyPrizeInfo> agencyPrizeInfos,double threshold) {
		CheckResult cr = new CheckResult(0.0, "", "", false);
		String wininfo = "";
		StringBuilder agencynos = new StringBuilder();
		for(AgencyPrizeInfo a:agencyPrizeInfos) {
			if(a.getCheckstate()!=CheckStateType.NOTPASS.value&&a.getCheckstate()!=CheckStateType.CREATE.value) {
				cr.setThreshold(cr.getThreshold()+a.getWeight());
				wininfo = a.getWininfo();
				agencynos.append(a.getId().getAgencyno()).append("|").append(a.getWeight()).append(";");
			}
		}
		if(cr.getThreshold()>=threshold) {
			cr.setIspass(true);
			cr.setWinPrizeInfo(wininfo);
			cr.setAgencynos(agencynos.toString());
		}
		return cr;
	}
	
	
	/**
	 * 获取审核为成功的开机号码
	 * @param agencyPrizeCodes
	 * @return
	 */
	private String findSuccessWininfo(List<AgencyPrizeInfo> agencyPrizeInfos) {
		for(AgencyPrizeInfo a:agencyPrizeInfos) {
			if(a.getCheckstate()==CheckStateType.PASS.value) {
				return a.getWininfo();
			}
		}
		return null;
	}
	
	
	/**
	 * 获取审核为失败的开机号码
	 * @param agencyPrizeCodes
	 * @return
	 */
	private List<String> findFailWininfo(List<AgencyPrizeInfo> agencyPrizeInfos) {
		List<String> list = new ArrayList<String>();
		for(AgencyPrizeInfo a:agencyPrizeInfos) {
			if(a.getCheckstate()==CheckStateType.NOTPASS.value) {
				list.add(a.getWininfo());
			}
		}
		return list;
	}
	

}
