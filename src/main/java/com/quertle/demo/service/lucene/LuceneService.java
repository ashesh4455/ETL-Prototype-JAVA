package com.quertle.demo.service.lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.quertle.demo.dto.SearchDto;
import com.quertle.demo.model.FierceNews;

/**
 * This class controls all the needed things for lucene.
 * 
 * @author ashesh
 *
 */
@Service
public class LuceneService {

	private static final Logger LOG = LoggerFactory.getLogger(LuceneService.class);

	@Value("${lucene.directory}")
	private String INDEX_DIR;

	@Autowired
	private IndexWriter indexWriter;

	@Autowired
	private IndexSearcher indexSearcher;

	/**
	 * This method saves the fierceNews using Lucene.
	 * 
	 * @param generalFierceNews
	 */
	public void save(List<FierceNews> generalFierceNews) {
		List<Document> docs = new ArrayList<>();
		generalFierceNews.forEach(g -> {
			docs.add(g.getLuceneDocument());
		});
		try {
			indexWriter.commit();
			indexWriter.deleteAll();
			indexWriter.addDocuments(docs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create IndexWriter
	 * 
	 * @return
	 * @throws IOException
	 */
	@Bean
	private IndexWriter createWriter() throws IOException {
		FSDirectory dir = FSDirectory.open(Paths.get(INDEX_DIR));
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		IndexWriter writer = new IndexWriter(dir, config);
		return writer;
	}

	/**
	 * This method search the indexed document
	 * 
	 * @param content
	 */
	public SearchDto search(SearchDto searchDto) {
		List<FierceNews> news = new ArrayList<>();
		String content = searchDto.getContent();
		try {
			TopDocs result = search(FierceNews.getFields(), content, FierceNews.getFlags(), indexSearcher, 1);
			for (ScoreDoc sd : result.scoreDocs) {
				Document d = indexSearcher.doc(sd.doc);
				FierceNews fierceNews = FierceNews.getFromDocument(d);
				news.add(fierceNews);
				LOG.info("Search :{}", fierceNews.getAuthor());
				LOG.info("Search :{}", d.get("authors"));
			}
			searchDto.setFierceNews(news);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return searchDto;
	}

	/**
	 * Search Lucene Docs
	 * 
	 * @param keys
	 * @param value
	 * @param flags
	 * @param searcher
	 * @param limit
	 * @return
	 * @throws Exception
	 */
	private TopDocs search(String[] keys, String value, BooleanClause.Occur[] flags, IndexSearcher searcher, int limit)
			throws Exception {
		// QueryParser qp = new QueryParser(key, new StandardAnalyzer());
		Query idQuery = MultiFieldQueryParser.parse(value, keys, flags, new StandardAnalyzer());
		TopDocs hits = searcher.search(idQuery, limit);
		return hits;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	@Bean
	private IndexSearcher createSearcher() throws IOException {
		Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}

}
