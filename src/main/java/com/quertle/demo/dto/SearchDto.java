package com.quertle.demo.dto;

import java.util.List;

import com.quertle.demo.model.FierceNews;

public class SearchDto {

	private String content;

	private List<FierceNews> fierceNews;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<FierceNews> getFierceNews() {
		return fierceNews;
	}

	public void setFierceNews(List<FierceNews> fierceNews) {
		this.fierceNews = fierceNews;
	}

}
