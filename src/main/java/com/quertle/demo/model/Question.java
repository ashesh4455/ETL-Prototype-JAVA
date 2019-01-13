package com.quertle.demo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

public class Question {
	private static final String ANSWERS = "answers";

	private static final String QUESTION_BODY = "questionBody";

	private static final String QUESTION_BODY_LINK = "questionBodyLink";

	private static final String TITLE = "title";

	private static final String ID = "id";

	private String title;

	private String questionBodyLink;

	private String questionBody;

	private List<String> answers;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getQuestionBodyLink() {
		return questionBodyLink;
	}

	public void setQuestionBodyLink(String questionBodyLink) {
		this.questionBodyLink = questionBodyLink;
	}

	public String getQuestionBody() {
		return questionBody;
	}

	public void setQuestionBody(String questionBody) {
		this.questionBody = questionBody;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	@Override
	public String toString() {
		return title + " " + questionBody;
	}

	/**
	 * This method add data to our index
	 * 
	 * @return
	 */
	public SolrInputDocument getSolrDocument() {
		SolrInputDocument document = new SolrInputDocument();
		document.addField(ID, UUID.randomUUID().toString());
		document.addField(TITLE, title);
		document.addField(QUESTION_BODY_LINK, questionBodyLink);
		document.addField(QUESTION_BODY, questionBody);

		if (answers != null && answers.size() > 0) {
			answers.forEach(a -> {
				document.addField(ANSWERS, a);
			});
		}
		return document;
	}

	public static String[] getFields() {
		String[] s = { QUESTION_BODY, TITLE };
		return s;
	}

	public static String getQuery(String query) {
		StringBuffer fullQuery = new StringBuffer();
		String[] fields = getFields();
		for (String f : fields) {
			fullQuery.append(f).append(":").append(query).append(" or ");
		}
		String tempQuery = fullQuery.toString();
		tempQuery = tempQuery.substring(0, tempQuery.lastIndexOf("or"));
		return tempQuery;
	}

	/**
	 * This method get the content from solr document
	 * 
	 * @param d
	 * @return
	 */
	public static Question getFromDocument(SolrDocument d) {
		Question question = new Question();
		question.setTitle(d.getFieldValue(TITLE).toString());
		question.setQuestionBody(d.getFieldValue(QUESTION_BODY).toString());
		List<String> answers = new ArrayList<>();
		if (d.getFieldValues(ANSWERS) != null) {
			for (Object o : d.getFieldValues(ANSWERS)) {
				answers.add(o.toString());
			}
			question.setAnswers(answers);
		}

		return question;
	}
}
