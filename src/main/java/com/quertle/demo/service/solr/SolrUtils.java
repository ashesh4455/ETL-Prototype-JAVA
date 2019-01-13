package com.quertle.demo.service.solr;

import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SolrUtils {

	@Value("${solr.url}")
	private String SOLR_URL;

	@Bean
	private HttpSolrClient initiliazeSolrClient() {
		HttpSolrClient solr = new HttpSolrClient.Builder(SOLR_URL).build();
		solr.setParser(new XMLResponseParser());
		return solr;
	}

	@Bean
	private ConcurrentUpdateSolrClient initiliazeConcurrentSolrClient() {
		ConcurrentUpdateSolrClient solr = new ConcurrentUpdateSolrClient.Builder(SOLR_URL).build();
		return solr;
	}
}
