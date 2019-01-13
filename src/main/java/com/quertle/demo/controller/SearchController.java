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

import com.quertle.demo.dto.SearchDto;
import com.quertle.demo.service.lucene.LuceneService;

@Controller
@RequestMapping("lucene")
public class SearchController {

	private static final Logger LOG = LoggerFactory.getLogger(GeneralController.class);

	@Autowired
	private LuceneService luceneService;

	@PostMapping("/search")
	public String search(@ModelAttribute SearchDto searchDto, Model model) {
		LOG.info("Search :{}", searchDto.getContent());
		model.addAttribute("title", "Lucene Search");
		model.addAttribute("search", luceneService.search(searchDto));
		return "result";
	}

	@GetMapping("/search")
	public String getUIForSearch(Model model) {
		model.addAttribute("title", "Lucene Search");
		model.addAttribute("search", new SearchDto());
		return "search";
	}

}
