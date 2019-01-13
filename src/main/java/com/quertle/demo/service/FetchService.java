package com.quertle.demo.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.quertle.demo.exceptions.SomethingWrongException;
import com.quertle.demo.model.Author;
import com.quertle.demo.model.FierceNews;
import com.quertle.demo.repository.AuthorRepository;
import com.quertle.demo.repository.FierceNewsRepository;
import com.quertle.demo.service.lucene.LuceneService;
import com.quertle.demo.service.solr.SolrService;
import com.quertle.demo.utils.DateUtils;

@Service
public class FetchService {
	private static final Logger LOG = LoggerFactory.getLogger(FetchService.class);

	@Autowired
	private FierceNewsRepository fierceNewsRepository;

	@Autowired
	public AuthorRepository authorRepository;

	@Value("${fierceNews.dataSource}")
	private String dataSource;

	@Autowired
	private LuceneService luceneService;

	@Autowired
	private SolrService solrService;

	/**
	 * This method prints xml if data and source exists
	 * 
	 * @param dataSource
	 */
	public void saveIfExists(String dataSource) {
		File file = new File("a.xml");

		if (file.exists()) {
			file.delete();
		}

		byte[] fileContent = readFileContent(file, dataSource);

		List<FierceNews> generalFierceNews = parseXML(fileContent);
		setFullTextContent(generalFierceNews);

		LOG.info("News Content: {}", generalFierceNews);

//		loadFierceNewsToDatabase(generalFierceNews);
		// loadFierceNewsToLucene(generalFierceNews);
		 loadFierceNewsToSolr(generalFierceNews);

	}

	public byte[] readFileContent(File file, String dataSource) {
		try {
			FileUtils.copyURLToFile(new URL(dataSource), file, 60000, 10000);
			return Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SomethingWrongException(e.getMessage(), "Printing of XML is not working.");
		}
	}

	private void loadFierceNewsToSolr(List<FierceNews> generalFierceNews) {
		solrService.save(generalFierceNews);
	}

	/**
	 * This method saves the data to lucene.
	 * 
	 * @param generalFierceNews
	 */
	private void loadFierceNewsToLucene(List<FierceNews> generalFierceNews) {
		// TODO Auto-generated method stub
		luceneService.save(generalFierceNews);
	}

	/**
	 * This method saves the fierce news and author details. First save the
	 * Author to the Author Repository and save the FierceNews collection to
	 * repository.
	 * 
	 * @param generalFierceNews
	 */
	public void loadFierceNewsToDatabase(List<FierceNews> generalFierceNews) {
		fierceNewsRepository.saveAll(generalFierceNews);
		List<Author> authorLists = new ArrayList<>();
		generalFierceNews.stream().forEach(f -> {
			f.getAuthor().forEach(a -> {
				a.setFierceNews(f);
			});
			authorLists.addAll(f.getAuthor());
		});
		authorRepository.saveAll(authorLists);
	}

	/**
	 * This method sets the full text from the url
	 * 
	 * @param generalFierceNews
	 */
	public void setFullTextContent(List<FierceNews> generalFierceNews) {
		// TODO Auto-generated method stub
		generalFierceNews.parallelStream().filter(n -> n.getUrlLink() != null).forEach(n -> {
			n.setFullText(getContent(n.getUrlLink()));
		});
	}

	/**
	 * This method return the html content from provided url.
	 * 
	 * @param url
	 * @return
	 */
	private String getContent(String url) {
		org.jsoup.nodes.Document doc;
		try {
			doc = Jsoup.connect(url).get();
			return doc.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 
	 * @param content
	 */
	public List<FierceNews> parseXML(byte[] content) {
		List<FierceNews> fierceNewsList = new ArrayList<>();
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			ByteArrayInputStream input = new ByteArrayInputStream(content);
			Document doc = builder.parse(input);
			Element root = doc.getDocumentElement();
			LOG.info("Root Name: {}", root.getNodeName());
			NodeList nodeList = doc.getElementsByTagName("doc");
			LOG.info("No of docs: {}", nodeList.getLength());

			for (int i = 0; i < nodeList.getLength(); i++) {
				FierceNews fierceNews = new FierceNews();
				Node node = nodeList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) node;
					setTitle(fierceNews, eElement);
					setAuthor(fierceNews, eElement);
					setDatePublished(fierceNews, eElement);
					setUrlLink(fierceNews, eElement);

				}
				fierceNewsList.add(fierceNews);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// LOG.info("News lists: {}",fierceNewsList);
		return fierceNewsList;
	}

	/**
	 * set url link from xml
	 * 
	 * @param fierceNews
	 * @param eElement
	 */
	private void setUrlLink(FierceNews fierceNews, Element eElement) {
		// TODO Auto-generated method stub
		NodeList urlLinks = eElement.getElementsByTagName("ext-link");
		LOG.info("No. of urlLink: {}", urlLinks.getLength());
		if (urlLinks.getLength() >= 1) {
			Node urlNode = urlLinks.item(0);
			String urlLink = urlNode.getTextContent();
			LOG.info("Url Link Value: {}", urlNode.getTextContent());
			fierceNews.setUrlLink(urlLink);
		}
	}

	/**
	 * Set date from xml
	 * 
	 * @param fierceNews
	 * @param eElement
	 */
	private void setDatePublished(FierceNews fierceNews, Element eElement) {
		// TODO Auto-generated method stub
		NodeList datePublished = eElement.getElementsByTagName("date-published");
		for (int i = 0; i < datePublished.getLength(); i++) {
			Node datePublishedNode = datePublished.item(i);
			String year = datePublishedNode.getFirstChild().getTextContent();
			String month = datePublishedNode.getFirstChild().getNextSibling().getTextContent();
			String day = datePublishedNode.getLastChild().getTextContent();
			LOG.info("Date Published: {}-{}-{}", year, month, day);
			fierceNews.setDatePublished(DateUtils.getDate(year, month, day));
		}

		// String year = datePublishedNode.getFirstChild().getTextContent();
		// String month = datePublishedNode.getTextContent();
		LOG.info("Date Published: {}", fierceNews);
	}

	/**
	 * Set authors from xml
	 * 
	 * @param fierceNews
	 * @param eElement
	 */
	private void setAuthor(FierceNews fierceNews, Element eElement) {
		// TODO Auto-generated method stub
		NodeList authors = eElement.getElementsByTagName("author");
		LOG.info("No of Authors: {}", authors.getLength());
		List<Author> authorList = new ArrayList<>();

		if (authors.getLength() >= 1) {
			// multiple author in the data source is expected
			for (int i = 0; i < authors.getLength(); i++) {
				Node authorNode = authors.item(i);
				String lastName = authorNode.getFirstChild().getTextContent();
				String firstName = authorNode.getLastChild().getTextContent();
				LOG.info("Single Author First Name: {}", authorNode.getFirstChild().getTextContent());
				LOG.info("Single Author Last Name: {}", authorNode.getLastChild().getTextContent());

				authorList.add(new Author(firstName, lastName));

			}
			fierceNews.setAuthor(authorList);
		}
	}

	/**
	 * Set title from xml
	 * 
	 * @param fierceNews
	 * @param eElement
	 */
	private void setTitle(FierceNews fierceNews, Element eElement) {
		// TODO Auto-generated method stub
		NodeList titles = eElement.getElementsByTagName("title");
		LOG.info("No of titles: {}", titles.getLength());
		if (titles.getLength() >= 1) {
			Node titleNode = titles.item(0);
			String title = titleNode.getTextContent();
			LOG.info("Title value: {}", titleNode.getTextContent());
			fierceNews.setTitle(title);
		}
	}

	/**
	 * This methods loads XML from data source to database. This Async separated
	 * the process format the request one which runs in the background . The
	 * return method for Async method should be void.
	 */
	@Async
	public void saveFromXML() {
		saveIfExists(dataSource);

	}
}
