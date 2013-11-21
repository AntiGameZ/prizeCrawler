package com.ruyicai.prizecrawler.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.prizecrawler.beidan.BeiDanDuizhenService;
import com.ruyicai.prizecrawler.beidan.BeiDanPeiluService;
import com.ruyicai.prizecrawler.beidan.BeiDanResultService;
import com.ruyicai.prizecrawler.consts.ErrorCode;

@Controller
@RequestMapping("/beidan")
public class BeiDanController {

	private Logger logger = LoggerFactory.getLogger(BeiDanController.class);
	
	@Autowired
	private BeiDanDuizhenService beiDanDuizhenService;
	@Autowired
	private BeiDanPeiluService beiDanPeiluService;
	@Autowired
	private BeiDanResultService beiDanResultService;
	
	
	
	@RequestMapping(value = "/getbeidanpeilv", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getjingcaipeilu(@RequestParam("lotno") String lotno,
			@RequestParam("batchcode") String batchcode) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			rd.setValue(beiDanPeiluService.getPeilvXMl(lotno, batchcode));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue("");
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	
	
	@RequestMapping(value = "/crawlduizhen", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData crawlduizhen() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			beiDanDuizhenService.duizhenTimer();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	
	@RequestMapping(value = "/crawlpeilv", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData crawlpeilv() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			beiDanPeiluService.peilvTimer();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	
	@RequestMapping(value = "/crawlresult", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData crawlresult() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			beiDanResultService.resultTimer();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	
	@RequestMapping(value = "/crawlresultByParamater", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData crawlresultByParamater(@RequestParam("lotno") String lotno,
			@RequestParam("year") String year,@RequestParam("batchcode") String batchcode) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			beiDanResultService.crawResultPage(lotno, year, batchcode);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	
	@RequestMapping(value = "/crawlresultFromMatch", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData crawlresultByParamater(@RequestParam("lotno") String lotno) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			beiDanResultService.crawResultMatchPage(lotno);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	
	
}
