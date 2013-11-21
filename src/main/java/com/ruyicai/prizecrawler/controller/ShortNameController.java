package com.ruyicai.prizecrawler.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.prizecrawler.consts.ErrorCode;
import com.ruyicai.prizecrawler.shortname.ShortNames;
import com.ruyicai.prizecrawler.shortname.dao.TShortNameDao;

@Controller
@RequestMapping("/shortname")
public class ShortNameController {

	private Logger logger = LoggerFactory.getLogger(ShortNameController.class);
	
	@Autowired
	private TShortNameDao shortNameDao;
	
	@RequestMapping(value = "/findshortname", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getjingcaipeilu(@RequestParam("name") String name,
			@RequestParam("type") int type) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			rd.setValue(shortNameDao.findShortName(name, type));
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue("");
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	@RequestMapping(value = "/saveshortname", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getjingcaipeilu(@RequestParam("name") String name,@RequestParam("shortname") String shortname,
			@RequestParam("type") int type) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			if(shortNameDao.findShortName(name, type)==null) {
				ShortNames forshort = new ShortNames();
				forshort.setName(name);
				forshort.setShortname(shortname);
				forshort.setType(type);
				shortNameDao.persist(forshort);
			}else {
				rd.setErrorCode(ErrorCode.EXIST.value);
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue("");
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	

}
