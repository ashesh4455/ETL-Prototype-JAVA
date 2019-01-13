package com.quertle.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.quertle.demo.dto.QuestionSearchDto;
import com.quertle.demo.dto.SearchDto;
import com.quertle.demo.service.solr.SolrService;

@Controller
@RequestMapping("solr")
public class SolrController {
	private static final Logger LOG = LoggerFactory.getLogger(GeneralController.class);

	@Autowired
	private SolrService solrService;

	@PostMapping("/search")
	public String search(@ModelAttribute SearchDto searchDto, Model model) {
		LOG.info("Search :{}", searchDto.getContent());
		model.addAttribute("title", "Solr Search");
		model.addAttribute("search", solrService.search(searchDto));
		return "result";
	}

	@GetMapping("/search")
	public String getUIForSearch(Model model) {
		model.addAttribute("title", "Solr Search");
		model.addAttribute("search", new SearchDto());
		return "search";
	}
	
	@PostMapping("/question/search")
	public String searchAnswer(@ModelAttribute QuestionSearchDto questionSearchDto, Model model) {
		LOG.info("Search :{}", questionSearchDto.getContent());
		model.addAttribute("title", "Solr Answer Search");
		model.addAttribute("questionSearch", solrService.searchQuestion(questionSearchDto));
		return "resultAnswer";
	}

	@GetMapping("/question/search")
	public String getUIForAnswerSearch(Model model) {
		model.addAttribute("title", "Solr Answer Search");
		model.addAttribute("questionSearch", new QuestionSearchDto());
		return "searchAnswer";
	}

}
