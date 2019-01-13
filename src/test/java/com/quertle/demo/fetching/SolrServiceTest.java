package com.quertle.demo.fetching;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.quertle.demo.dto.QuestionSearchDto;
import com.quertle.demo.service.QuestionService;
import com.quertle.demo.service.solr.SolrService;
import com.quertle.demo.utils.Paginate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SolrServiceTest {

	@Autowired
	private QuestionService questionService;

	@Autowired
	private HttpSolrClient solr;

	@Autowired
	private SolrService solrService;

	@Before
	public void init() {
		deleteAllQuestion();
	}

	/**
	 * This method test the parsing the url content and index it
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void indexQuestionTest() throws InterruptedException {
		questionService.getData(new Paginate(3, 20));
		Thread.sleep(10000);
	}

	@Test
	public void searchQuestion() {
		// index

		QuestionSearchDto questionSearchDto = new QuestionSearchDto();
		questionSearchDto.setContent("Data");
		questionSearchDto = solrService.searchQuestion(questionSearchDto);
		System.out.println(questionSearchDto);

		// delete .ID
	}

	private void deleteAllQuestion() {
		try {
			solr.deleteByQuery("*:*");
			solr.commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
