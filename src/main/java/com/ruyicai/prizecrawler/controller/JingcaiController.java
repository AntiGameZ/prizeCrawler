package com.ruyicai.prizecrawler.controller;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.prizecrawler.consts.ErrorCode;
import com.ruyicai.prizecrawler.domain.TjingcaiResult;
import com.ruyicai.prizecrawler.jingcai.JingcaiDuizhenService;
import com.ruyicai.prizecrawler.jingcai.JingcaiLetPointService;
import com.ruyicai.prizecrawler.jingcai.JingcaiPeiluService;
import com.ruyicai.prizecrawler.jingcai.JingcaiResultService;

@Controller
@RequestMapping("/jingcai")
public class JingcaiController {
	
	private Logger logger = LoggerFactory.getLogger(JingcaiController.class);
	
	@Autowired
	private JingcaiDuizhenService jingcaiDuizhenService;

	@Autowired
	private JingcaiResultService jingcaiResultService;
	
	@Autowired
	private JingcaiPeiluService jingcaiPeiluService;
	
	@Autowired
	private JingcaiLetPointService jingcaiLetPointService;
	
	@RequestMapping(value = "/getjingcaipeilu", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getjingcaipeilu() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiPeiluService.getJingcaiPeilu();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getjingcaijsonpeilu", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getjingcaipeilu1(@RequestParam("type") String type,
			@RequestParam("valueType") String valueType) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			rd.setValue(jingcaiPeiluService.getJingcaijsonpeilu(type, valueType));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getjingcaipeiluwithPamater", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getjingcaipeilu(@RequestParam("type") String type,
			@RequestParam("valueType") String valueType) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
		rd.setValue(jingcaiPeiluService.getJingcaipeilu(type, valueType).asXML());
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getjingcairesult", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getjingcairesult(@RequestParam("id") String id) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			TjingcaiResult tjingcaiResult = jingcaiResultService.getJingcaiResult(id);
			if(null == tjingcaiResult) {
				rd.setErrorCode(ErrorCode.RESULT_NOT_EXISTS.value);
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/jingcairesult", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData jingcairesult() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiResultService.init();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/jingcairesultbydaypage", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData jingcairesultbydaypage(@RequestParam("daycount") int daycount,
			@RequestParam("page") int page) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiResultService.init(daycount, page);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getfootballresult", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getfootballresult() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiResultService.getFootballResult(JingcaiResultService.FOOTBALL_URL);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/footprocess", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData footprocess(@RequestParam("url") String url) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiResultService.footProcess(url);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getbasketballresult", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getbasketballresult() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiResultService.getBasketballResult(JingcaiResultService.BASKETBALL_URL);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/bassprocess", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData bassprocess(@RequestParam("url") String url) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiResultService.bassProcess(url);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getfootballmatches", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getfootballmatches() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiDuizhenService.getFootballMatches();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getbasketballmatches", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getbasketballmatches() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiDuizhenService.getBasketballMatches();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/getendtime", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getendtime(@RequestParam("type") int type, @RequestParam("date")  String date,
			@RequestParam("weekid") int weekid) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			Date time = jingcaiDuizhenService.getEndtime(DateUtils.parseDate(date, new String[] {"yyyy-MM-dd HH:mm:ss"}), String.valueOf(type));
			rd.setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/gethtml", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getendtime(@RequestParam("url") String url) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			rd.setValue(jingcaiPeiluService.getHtml(url));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	
	
	@RequestMapping(value = "/updatefootballletpoint", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updatefootballletpoint() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiLetPointService.updateFootBallLetPointService();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	
	@RequestMapping(value = "/updatebasketballletpoint", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updatebasketballletpoint() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiLetPointService.updateBasketBallLetPointService();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	
	
	@RequestMapping(value = "/getJingcaiPeilu", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getJingcaiPeilu() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiPeiluService.getJingcaiPeilu();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	
	@RequestMapping(value = "/getByDownload", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getByDownload() {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			rd.setValue(jingcaiPeiluService.getBydownload());
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	
	@RequestMapping(value = "/setByDownload", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData setByDownload(@RequestParam("downloadstate") int downloadstate) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			jingcaiPeiluService.setBydownload(downloadstate);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
}
