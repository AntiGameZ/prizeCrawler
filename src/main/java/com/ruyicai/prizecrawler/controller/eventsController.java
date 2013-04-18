package com.ruyicai.prizecrawler.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.prizecrawler.consts.ErrorCode;
import com.ruyicai.prizecrawler.domain.Events;
import com.ruyicai.prizecrawler.service.EventsService;

@Controller
@RequestMapping("/events")
public class eventsController {

	private Logger logger = LoggerFactory.getLogger(eventsController.class);

	@Autowired
	private EventsService eventsService;

	@RequestMapping(value = "/saveEvents", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData saveEvents(@RequestParam("type") BigDecimal type,
			@RequestParam("name") String name,
			@RequestParam("shortname") String shortname) {
		ResponseData rd = new ResponseData();
		rd.setErrorCode(ErrorCode.OK.value);
		try {
			Events events = new Events();
			events.setName(name);
			events.setShortname(shortname);
			events.setType(type);
			eventsService.saveEvents(events);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
}
