package com.quertle.demo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.quertle.demo.model.Question;
import com.quertle.demo.service.solr.SolrService;
import com.quertle.demo.utils.Paginate;

@Service
public class QuestionService {

	private final static Logger LOG = LoggerFactory.getLogger(QuestionService.class);

	@Autowired
	private SolrService solrService;

	@Autowired
	private TaskExecutor executor;

	/**
	 * This method parse the url content
	 * 
	 * @param paginate
	 *            TODO
	 * 
	 * @return
	 */
	public List<Question> getData(Paginate paginate) {
		validatePaginate(paginate);
		List<Question> allQuestions = Collections.synchronizedList(new ArrayList<>());
		for (int i = 1; i <= paginate.getPageLimit(); i++) {
			LOG.info("Initialized: No of Questions received: {}", allQuestions.size());
			executor.execute(getRunnable(i, paginate, allQuestions));
			LOG.info("Executed: No of Questions received: {}", allQuestions.size());
		}
		return allQuestions;
	}

	private Runnable getRunnable(int i, Paginate paginate, List<Question> allQuestions) {
		Runnable aRunnable = new Runnable() {
			public void run() {
				Paginate paginatePage = new Paginate(i, paginate.getPageSize());
				List<Question> limitedQuestions = getLimitedQuestions(paginatePage);
				// allQuestions.addAll(limitedQuestions);
				solrService.saveQuestions(limitedQuestions);
			}
		};
		return aRunnable;
	}

	private List<Question> getLimitedQuestions(Paginate paginate) {
		List<Question> questions = new ArrayList<>();
		StringBuilder params = new StringBuilder();
		params.append("?sort=newest");
		params.append("&page=").append(paginate.getPageLimit());
		params.append("&pagesize=").append(paginate.getPageSize());
		try {
			String fetchingURL = "https://stackoverflow.com/questions/tagged/laravel-5" + params.toString();
			LOG.info("Now fetching URL: {}", fetchingURL);
			org.jsoup.nodes.Document doc = Jsoup.connect(fetchingURL).get();
			Elements elements = doc.getElementsByClass("summary");

			for (Element element : elements) {
				String questionTitle = element.getElementsByClass("question-hyperlink").html();
				String questionBodyLink = element.getElementsByAttribute("href").get(0).absUrl("href");

				Question question = new Question();
				question.setTitle(questionTitle);
				question.setQuestionBodyLink(questionBodyLink);
				questions.add(question);
			}

			questions.forEach(q -> {
				org.jsoup.nodes.Document questionBodyLink;
				try {
					questionBodyLink = Jsoup.connect(q.getQuestionBodyLink()).get();
					Elements questionBodyElements = questionBodyLink.getElementsByClass("post-text");
					String questionBody = Jsoup.parse(questionBodyElements.get(0).html()).text();
					q.setQuestionBody(questionBody);

					List<String> answers = new ArrayList<>();
					for (int i = 1; i < questionBodyElements.size(); i++) {
						String answer = Jsoup.parse(questionBodyElements.get(i).html()).text();
						answers.add(answer);
					}
					q.setAnswers(answers);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.info("Questions: {}", questions.size());
		return questions;
	}

	private Paginate validatePaginate(Paginate paginate) {
		Paginate defaultPaginate = new Paginate(1, 50);
		if (paginate != null) {
			defaultPaginate = paginate.getPageLimit() != null && paginate.getPageLimit() < 1 ? paginate.setPageLimit(1)
					: paginate;

			defaultPaginate = defaultPaginate.getPageSize() != null
					&& (defaultPaginate.getPageSize() > 50 || defaultPaginate.getPageSize() < 1)
							? defaultPaginate.setPageSize(50) : defaultPaginate;

			return defaultPaginate;
		}
		return defaultPaginate;
	}

	/**
	 * This method parse the url content and calls the solr for indexing
	 */
	public void parseURLContent() {
		List<Question> questions = getData(new Paginate(1, 50));
		// questions.forEach(System.out::println);
		loadQuestionsToSolr(questions);

	}

	/**
	 * This method call the method to index the content using solr
	 * 
	 * @param questions
	 */
	private void loadQuestionsToSolr(List<Question> questions) {
		solrService.saveQuestions(questions);
	}
}
