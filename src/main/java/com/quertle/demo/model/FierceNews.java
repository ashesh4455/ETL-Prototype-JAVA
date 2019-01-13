package com.quertle.demo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

@Entity
public class FierceNews {

	private static final String AUTHORS = "authors";
	/**
	 * Title - title
	 */
	private static final String TITLE = "title";
	private static final String ID = "id";
	private static final String DATE_PUBLISHED = "datePublished";
	private static final String FULL_TEXT = "fullText";
	private static final String URL_LINK = "urlLink";

	private static String[] fields;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String title;

	@OneToMany(mappedBy = "fierceNews", fetch = FetchType.EAGER)
	/*
	 * For many to many case , its not needed for this scenario.
	 * 
	 * @JoinTable(name = "fierce_author", joinColumns = @JoinColumn(name =
	 * "fierce_id", referencedColumnName = "ID"), inverseJoinColumns
	 * = @JoinColumn(name = "author_id", referencedColumnName = "ID"))
	 */
	private List<Author> author;

	private Date datePublished;
	private String abstractContent;

	@Column(columnDefinition = "text")
	private String fullText;
	private String urlLink;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Author> getAuthor() {
		return author;
	}

	public void setAuthor(List<Author> author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "FierceNews [title=" + title + ", author=" + author + ", datePublished=" + datePublished
				+ ", abstractContent=" + abstractContent + ", fullText=" + fullText + ", urlLink=" + urlLink + "]";
	}

	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}

	public String getAbstractContent() {
		return abstractContent;
	}

	public void setAbstractContent(String abstractContent) {
		this.abstractContent = abstractContent;
	}

	public String getFullText() {
		return fullText;
	}

	public void setFullText(String fullText) {
		this.fullText = fullText;
	}

	public String getUrlLink() {
		return urlLink;
	}

	public void setUrlLink(String urlLink) {
		this.urlLink = urlLink;
	}

	/**
	 * This creates a Lucene document.
	 * 
	 * @return
	 */
	public Document getLuceneDocument() {
		Document document = new Document();
		document.add(new StringField(ID, UUID.randomUUID().toString(), Field.Store.YES));
		document.add(new TextField(TITLE, title, Field.Store.YES));
		document.add(new TextField(DATE_PUBLISHED, datePublished.toString(), Field.Store.YES));
		document.add(new TextField(FULL_TEXT, fullText, Field.Store.YES));
		document.add(new TextField(URL_LINK, urlLink, Field.Store.YES));
		if (author != null && author.size() > 0) {
			final StringBuilder authors = new StringBuilder();
			author.forEach(a -> {
				authors.append(a.getFirstName() + " " + a.getLastName() + " ,");
			});
			document.add(new TextField(AUTHORS, authors.toString(), Field.Store.YES));
		}
		return document;
	}

	public static String[] getFields() {
		String[] s = { AUTHORS, FULL_TEXT, TITLE, DATE_PUBLISHED, URL_LINK };
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

	public static Occur[] getFlags() {
		BooleanClause.Occur[] flags = { BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD,
				BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD };
		return flags;
	}

	/**
	 * 
	 * @param d
	 * @return
	 */
	public static FierceNews getFromDocument(Document d) {
		FierceNews fierceNews = new FierceNews();
		fierceNews.setTitle(d.get(TITLE));
		fierceNews.setUrlLink(d.get(URL_LINK));
		fierceNews.setFullText(d.get(FULL_TEXT));
		String auths = d.get(AUTHORS);

		if (auths != null && !auths.isEmpty()) {
			String[] authors = auths.split(",");
			List<Author> authorList = new ArrayList<>();
			for (String auth : authors) {
				if (!auth.trim().isEmpty()) {
					String[] names = auth.split(" ");
					Author author = new Author(names[0], names[1]);
					authorList.add(author);
				}
			}
			if (!authorList.isEmpty())
				fierceNews.setAuthor(authorList);
		}
		return fierceNews;
	}

	/**
	 * This creates a solr document.
	 * 
	 * @return
	 */
	public SolrInputDocument getSolrDocument() {
		SolrInputDocument document = new SolrInputDocument();
		document.addField(ID, UUID.randomUUID().toString());
		document.addField(TITLE, title);
		document.addField(DATE_PUBLISHED, datePublished.toString());
		document.addField(FULL_TEXT, fullText);
		document.addField(URL_LINK, urlLink);

		if (author != null && author.size() > 0) {
			final StringBuilder authors = new StringBuilder();
			author.forEach(a -> {
				authors.append(a.getFirstName() + " " + a.getLastName() + " ,");
			});
			document.addField(AUTHORS, authors.toString());
		}
		return document;
	}

	/**
	 * This method get the content from solr document
	 * 
	 * @param d
	 * @return
	 */
	public static FierceNews getFromDocument(SolrDocument d) {
		FierceNews fierceNews = new FierceNews();
		fierceNews.setTitle(d.getFieldValue(TITLE).toString());
		fierceNews.setUrlLink(d.getFieldValue(URL_LINK).toString());
		fierceNews.setFullText(d.getFieldValue(FULL_TEXT).toString());
		String auths = d.getFieldValue(AUTHORS).toString();

		if (auths != null && !auths.isEmpty()) {
			String[] authors = auths.split(",");
			List<Author> authorList = new ArrayList<>();
			for (String auth : authors) {
				if (!auth.trim().isEmpty()) {
					String[] names = auth.split(" ");
					Author author = new Author(names[0], names[1]);
					authorList.add(author);
				}
			}
			if (!authorList.isEmpty())
				fierceNews.setAuthor(authorList);
		}
		return fierceNews;
	}

}
