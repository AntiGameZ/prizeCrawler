package com.ruyicai.prizecrawler.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.prizecrawler.consts.ErrorCode;
import com.ruyicai.prizecrawler.domain.PrizeInfo;
import com.ruyicai.prizecrawler.jingcaiguanyajun.GuanYaJunOlympicService;
import com.ruyicai.prizecrawler.jingcaiguanyajun.GuanYaJun01Service;
import com.ruyicai.prizecrawler.producer.AgencyDrawLotteryProducer;
import com.ruyicai.prizecrawler.producer.CheckCodeProducer;
import com.ruyicai.prizecrawler.producer.CheckInfoProducer;
import com.ruyicai.prizecrawler.producer.IssueEndProducer;
import com.ruyicai.prizecrawler.producer.PrizeInfoProducer;
import com.ruyicai.prizecrawler.producer.SMSProducer;
import com.ruyicai.prizecrawler.service.LotCheckSwitchService;

@Controller
@RequestMapping("/system")
public class SystemController {

	private static Logger logger = LoggerFactory
			.getLogger(SystemController.class);

	@Resource
	private PrizeInfoProducer prizeInfoProducer;

	@Resource
	private AgencyDrawLotteryProducer agencyDrawLotteryProducer;

	@Resource
	private IssueEndProducer issueEndProducer;

	@Resource
	private CheckCodeProducer checkCodeProducer;

	@Resource
	private CheckInfoProducer checkInfoProducer;
	
	@Resource
	private LotCheckSwitchService lotCheckSwitchService;

	@Resource
	private SMSProducer smsProducer;
	
	@Resource
	private GuanYaJun01Service guanyajun01service;
	
	@Resource
	private GuanYaJunOlympicService olympicService;

	@RequestMapping(value = "/goMissvalue")
	public String goMissvalue() {
		return "missvalue/main";
	}

	@RequestMapping(value = "/goBetPartition")
	public String goBetPartition() {
		return "betpartition/main";
	}

	@RequestMapping(value = "/sendprize", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData sendwindata(@RequestParam("lotno") String lotno,
			@RequestParam("batchcode") String batchcode) {
		ResponseData rd = new ResponseData();
		try {
			rd.setErrorCode(ErrorCode.OK.value);
			PrizeInfo p = new PrizeInfo();
			p.setLotno(lotno);
			p.setBatchcode(batchcode);
			p.setCreatedate(new Date());
			prizeInfoProducer.send(p);

		} catch (Exception e) {
			rd.setErrorCode(ErrorCode.ERROR.value);
			logger.info("SystemController err", e);
		}
		return rd;
	}

	@RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData sendMsg(@RequestParam("type") String type,
			@RequestParam("msg") String msg) {
		ResponseData rd = new ResponseData();
		try {
			rd.setErrorCode(ErrorCode.OK.value);
			smsProducer.send(type, msg);
		} catch (Exception e) {
			rd.setErrorCode(ErrorCode.ERROR.value);
			logger.info("SystemController err", e);
		}
		return rd;
	}
	
	
	
	
	@RequestMapping(value = "/endIssue", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData endIssue(@RequestParam("lotno") String lotno,
			@RequestParam("batchcode") String batchcode) {
		ResponseData rd = new ResponseData();
		try {
			rd.setErrorCode(ErrorCode.OK.value);
			issueEndProducer.send(lotno, batchcode);
		} catch (Exception e) {
			rd.setErrorCode(ErrorCode.ERROR.value);
			logger.info("SystemController err", e);
		}
		return rd;
	}
	
	
	
	
	@RequestMapping(value = "/drawLottery", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData drawLottery(@RequestParam("lotno") String lotno,
			@RequestParam("batchcode") String batchcode,@RequestParam("agencyno") String agencyno,
			@RequestParam("info") String info,@RequestParam("winbasecode") String winbasecode,
			@RequestParam("winspecialcode") String winspecialcode) {
		ResponseData rd = new ResponseData();
		try {
			rd.setErrorCode(ErrorCode.OK.value);
			agencyDrawLotteryProducer.send(agencyno, lotno, batchcode, winbasecode,winspecialcode,info);
		} catch (Exception e) {
			rd.setErrorCode(ErrorCode.ERROR.value);
			logger.info("SystemController err", e);
		}
		return rd;
	}
	
	
	@RequestMapping(value = "/checkCode", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData checkCode(@RequestParam("lotno") String lotno,
			@RequestParam("batchcode") String batchcode,@RequestParam("agencyno") String agencyno,
			@RequestParam("wincode") String wincode) {
		ResponseData rd = new ResponseData();
		try {
			rd.setErrorCode(ErrorCode.OK.value);
			checkCodeProducer.send(agencyno, lotno, batchcode, wincode);
		} catch (Exception e) {
			rd.setErrorCode(ErrorCode.ERROR.value);
			logger.info("SystemController err", e);
		}
		return rd;
	}
	
	
	
	
	@RequestMapping(value = "/checkInfo", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData checkInfo(@RequestParam("lotno") String lotno,
			@RequestParam("batchcode") String batchcode,@RequestParam("agencyno") String agencyno,
			@RequestParam("info") String info) {
		ResponseData rd = new ResponseData();
		try {
			rd.setErrorCode(ErrorCode.OK.value);
			checkInfoProducer.send(agencyno, lotno, batchcode, info);
		} catch (Exception e) {
			rd.setErrorCode(ErrorCode.ERROR.value);
			logger.info("SystemController err", e);
		}
		return rd;
	}
	
	
	@RequestMapping(value = "/updateLotCheckState", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateLotCheckState(@RequestParam("lotno") String lotno,
			@RequestParam("state") int state) {
		ResponseData rd = new ResponseData();
		try {
			rd.setErrorCode(ErrorCode.OK.value);
			lotCheckSwitchService.updateState(lotno, state);
		} catch (Exception e) {
			rd.setErrorCode(ErrorCode.ERROR.value);
			logger.info("SystemController err", e);
		}
		return rd;
	}
	
	
	@RequestMapping(value = "/saveGYJMatch", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData saveGYJMatch() {
		ResponseData rd = new ResponseData();
		try {
			rd.setErrorCode(ErrorCode.OK.value);
			guanyajun01service.saveMatches();
		} catch (Exception e) {
			rd.setErrorCode(ErrorCode.ERROR.value);
			logger.info("SystemController err", e);
		}
		return rd;
	}
	
	
	@RequestMapping(value = "/saveOlympicMatch", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData saveOlympicMatch() {
		ResponseData rd = new ResponseData();
		try {
			rd.setErrorCode(ErrorCode.OK.value);
			olympicService.saveMatches();
		} catch (Exception e) {
			rd.setErrorCode(ErrorCode.ERROR.value);
			logger.info("SystemController err", e);
		}
		return rd;
	}
	
	

}
