package com.quertle.demo.dto;

import java.util.List;

import com.quertle.demo.model.Question;

public class QuestionSearchDto {

	private String content;

	private List<Question> question;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Question> getQuestion() {
		return question;
	}

	public void setQuestion(List<Question> question) {
		this.question = question;
	}

	@Override
	public String toString() {
		return "QuestionSearchDto [content=" + content + ", question=" + question + "]";
	}

}
