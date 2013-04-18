package com.ruyicai.prizecrawler.timer;

import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.prizecrawler.consts.SystemCode;
import com.ruyicai.prizecrawler.domain.Notification;
import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.enums.NoticeStateType;
import com.ruyicai.prizecrawler.enums.NoticeType;
import com.ruyicai.prizecrawler.producer.EmailProducer;
import com.ruyicai.prizecrawler.service.NotificationService;
import com.ruyicai.prizecrawler.service.PrizeInfoService;
import com.ruyicai.prizecrawler.util.HttpTookit;

@Service("noticeTimer")
public class NoticeTime {

	private static Logger logger = LoggerFactory.getLogger(NoticeTime.class);

	@Resource
	private PrizeInfoService prizeInfoService;
	
	@Resource
	private NotificationService notificationService;
	
	@Resource
	private EmailProducer emailProducer;

	/**
	 * 快开开奖通知
	 */
	public void noticeFashOpen() {
		Random rdm = new Random();
		int rd = rdm.nextInt(100000) + 100000;
		logger.info("高频彩开始进行开奖通知random=" + rd + ">>>>>>>>>>");
		List<PrizeInfo> noNotice = prizeInfoService.findALLNoNotice();

		if (noNotice.isEmpty()) {
			logger.info("高频彩没有需要进行的开奖通知random=" + rd + ">>>>>>>>>>");
			return;
		}

		for (PrizeInfo prizeInfo : noNotice) {
			noticeFashOpenCode(prizeInfo);
			emailProducer.send(prizeInfo.getLotno(), prizeInfo.getBatchcode(), prizeInfo.getNoticenum(), SystemCode.TYPE_NOTICE);
		}
		logger.info("高频彩本次开奖通知结束random=" + rd + ">>>>>>>>>>");
	}

	/**
	 * 大盘开奖号码和开奖开奖信息通知
	 */
	public void noticeDaPan() {
		Random rdm = new Random();
		int rd = rdm.nextInt(100000) + 100000;
		logger.info("大盘彩开始进行开奖通知random=" + rd + ">>>>>>>>>>");
		List<Notification> noNotice = notificationService.findNoNotice();

		if (noNotice.isEmpty()) {
			logger.info("大盘彩没有需要进行的开奖通知random=" + rd + ">>>>>>>>>>");
			return;
		}

		for (Notification notification : noNotice) {
			if (notification.getType().equals(NoticeType.PRIZECODE.value)) {
				noticeCode(notification);
			} else if (notification.getType().equals(NoticeType.PRIZEINFO.value)) {
				noticeInfo(notification);
			}

		}
		logger.info("大盘彩本次开奖通知结束random=" + rd + ">>>>>>>>>>");
	}

	private void noticeFashOpenCode(PrizeInfo prizeInfo) {
		logger.info(">>>>>>>>>>高频彩开始通知一条开奖：lotno=" + prizeInfo.getLotno()
				+ " batchcode=" + prizeInfo.getBatchcode() + " winbasecode="
				+ prizeInfo.getWinbasecode() + " winspecialcode="
				+ prizeInfo.getWinspecialcode() + " times="
				+ prizeInfo.getNoticenum());
		if (prizeInfo.getNoticenum() >= SystemCode.NOTICETIME) {
			prizeInfo.setNoticeState(-1);
			prizeInfoService.merge(prizeInfo);
			return;
		}

		if (HttpTookit.notice(prizeInfo)) {
			prizeInfo.setNoticenum(prizeInfo.getNoticenum() + 1);
			prizeInfo.setNoticeState(1);
			prizeInfo.setNoticedate(new Date());
		} else {
			prizeInfo.setNoticenum(prizeInfo.getNoticenum() + 1);
			prizeInfo.setNoticeState(0);
			logger.info(">>>>>>>>>>高频彩通知一条开奖失败");
		}

		prizeInfoService.merge(prizeInfo);

		logger.info(">>>>>>>>>>高频彩通知一条开奖结束：lotno=" + prizeInfo.getLotno()
				+ " batchcode=" + prizeInfo.getBatchcode() + " winbasecode="
				+ prizeInfo.getWinbasecode() + " winspecialcode="
				+ prizeInfo.getWinspecialcode() + " times="
				+ prizeInfo.getNoticenum());

	}
	
	private void noticeCode(Notification notification) {
		logger.info(">>>>>>>>>>大盘彩开始通知一条开奖号码：" + notification.toString());
		if (notification.getNoticetimes() >= SystemCode.NOTICETIME) {
			notification.setNoticestate(NoticeStateType.FAIL.value);
			notificationService.merge(notification);
			return;
		}

		if (HttpTookit.noticeCode(notification.getLotno(),
				notification.getBatchcode(), notification.getWinbasecode(),
				notification.getWinspecialcode())) {
			notification.setNoticetimes(notification.getNoticetimes() + 1);
			notification.setNoticestate(NoticeStateType.SUCCESS.value);
			notification.setNoticedate(new Date());
		} else {
			notification.setNoticetimes(notification.getNoticetimes() + 1);
			notification.setNoticestate(NoticeStateType.WAIT.value);
			logger.info(">>>>>>>>>>大盘彩通知一条开奖号码失败");
		}

		notificationService.merge(notification);

		logger.info(">>>>>>>>>>大盘彩通知一条开奖号码结束：" + notification.toString());

	}

	private void noticeInfo(Notification notification) {
		logger.info(">>>>>>>>>>大盘彩开始通知一条开奖详细信息：" + notification.toString());
		if (notification.getNoticetimes() >= SystemCode.NOTICETIME) {
			notification.setNoticestate(NoticeStateType.FAIL.value);
			notificationService.merge(notification);
			return;
		}

		if (HttpTookit.noticeInfo(notification.getLotno(), notification.getBatchcode(), notification.getInfo())) {
			notification.setNoticetimes(notification.getNoticetimes() + 1);
			notification.setNoticestate(NoticeStateType.SUCCESS.value);
			notification.setNoticedate(new Date());
		} else {
			notification.setNoticetimes(notification.getNoticetimes() + 1);
			notification.setNoticestate(NoticeStateType.WAIT.value);
			logger.info(">>>>>>>>>>大盘彩通知一条开奖详细信息失败");
		}

		notificationService.merge(notification);

		logger.info(">>>>>>>>>>大盘彩通知一条开奖详细信息结束：" + notification.toString());

	}
}
