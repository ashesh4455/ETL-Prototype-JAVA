package com.quertle.demo.service.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quertle.demo.dto.QuestionSearchDto;
import com.quertle.demo.dto.SearchDto;
import com.quertle.demo.model.FierceNews;
import com.quertle.demo.model.Question;
import com.quertle.demo.repository.FierceNewsRepository;
import com.quertle.demo.service.lucene.LuceneService;

/**
 * This class controls all the things needed for solr.
 * 
 * @author ashesh
 *
 */
@Service
public class SolrService {

	private static final Logger LOG = LoggerFactory.getLogger(LuceneService.class);

	@Autowired
	private ConcurrentUpdateSolrClient solrClient;

	@Autowired
	private FierceNewsRepository fierceNewsRepository;

	/**
	 * This method search the indexed document from solr
	 * 
	 * @param content
	 */
	public SearchDto search(SearchDto searchDto) {
		List<FierceNews> solr_news = new ArrayList<>();
		String content = searchDto.getContent();
		LOG.info("Content :{}", content);

		SolrQuery query = new SolrQuery();
		query.set("q", FierceNews.getQuery(content));
		// query.set("q", "date_published:" + content);
		// query.set("q", "full_text:" + content);
		// query.set("q", "url_link:" + content);
		// query.set("q", "authors:" + content);

		LOG.info("Query :{}", query);
		QueryResponse response;
		try {
			response = solrClient.query(query);
			SolrDocumentList docList = response.getResults();

			for (SolrDocument doc : docList) {
				FierceNews fierceNews = FierceNews.getFromDocument(doc);
				solr_news.add(fierceNews);
			}
			searchDto.setFierceNews(solr_news);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return searchDto;
	}

	/**
	 * This method saves the fierceNews using Solr.
	 * 
	 * @param generalFierceNews
	 */
	public void save(List<FierceNews> generalFierceNews) {
		List<SolrInputDocument> solrDocs = new ArrayList<>();
		generalFierceNews.forEach(g -> {
			solrDocs.add(g.getSolrDocument());
		});

		try {
			solrClient.add(solrDocs);
			solrClient.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * This method index the questions using solr
	 * 
	 * @param questions
	 */
	public void saveQuestions(List<Question> questions) {
		List<SolrInputDocument> solrDocs = new ArrayList<>();
		questions.forEach(q -> {
			solrDocs.add(q.getSolrDocument());
		});
		if (solrDocs.size() > 0) {
			try {
				solrClient.add(solrDocs);
				solrClient.commit();
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			LOG.debug("No questions to INDEX.");
		}
	}

	public QuestionSearchDto searchQuestion(QuestionSearchDto questionSearchDto) {
		LOG.info("Search Content:{}", questionSearchDto.getContent());

		List<Question> solr_answer = new ArrayList<>();
		String content = questionSearchDto.getContent();

		SolrQuery query = new SolrQuery();
		query.set("q", Question.getQuery(content));

		LOG.debug("Query :{}", query);
		// System.exit(-1);
		QueryResponse response;
		try {
			response = solrClient.query(query);
			SolrDocumentList docList = response.getResults();

			for (SolrDocument doc : docList) {
				Question question = Question.getFromDocument(doc);
				solr_answer.add(question);
			}
			questionSearchDto.setQuestion(solr_answer);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return questionSearchDto;
	}

}
