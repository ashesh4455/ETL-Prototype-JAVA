package com.quertle.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quertle.demo.dto.Address;
import com.quertle.demo.model.General;
import com.quertle.demo.service.FetchService;
import com.quertle.demo.service.GeneralService;
import com.quertle.demo.service.QuestionService;
import com.quertle.demo.service.lucene.LuceneService;

@RestController
public class GeneralController {

	private static final Logger LOG = LoggerFactory.getLogger(GeneralController.class);

	@Autowired
	private QuestionService questionService;

	// @Autowired
	private GeneralService generalService;

	@Autowired
	private FetchService fetchService;

	@Autowired
	private LuceneService luceneService;

	public GeneralController(GeneralService generalService) {
		this.generalService = generalService;
	}

	@RequestMapping("/load-xml")
	public void loadXML() {
		fetchService.saveFromXML();
	}

	@RequestMapping("/")
	public General getGeneral() {
		LOG.info("hello {} (false)", generalService == null);
		return generalService.getGeneral();
	}

	@PostMapping("/location")
	public Address receiveAddress(@RequestBody Address address) {
		LOG.info("Address: {}", address.getLocation());
		address.setLocation(address.getLocation() + "   Location12345");
		return address;
	}

	/**
	 * This method call the methods to index the url content
	 */
	@RequestMapping("/load-url")
	public void loadURL() {
		questionService.parseURLContent();
	}

}
