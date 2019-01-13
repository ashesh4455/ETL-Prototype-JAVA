package com.quertle.demo.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

@Entity
public class Author {

	private static final String FIRST_NAME = "firstName";

	private static final String ID = "id";

	private static final String LAST_NAME = "lastName";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String firstName;
	private String lastName;

	@ManyToOne
	@JoinColumn
	private FierceNews fierceNews;
	
	

	public Author() {
		super();
	}

	public Author(String firstName, String lastName) {
		// TODO Auto-generated constructor stub
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FierceNews getFierceNews() {
		return fierceNews;
	}

	public void setFierceNews(FierceNews fierceNews) {
		this.fierceNews = fierceNews;
	}

	@Override
	public String toString() {
		return "Author [firstName=" + firstName + ", lastName=" + lastName + "]";
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Document getLuceneDocument() {
		Document document = new Document();
		document.add(new StringField(ID, UUID.randomUUID().toString(), Field.Store.YES));
		document.add(new TextField(FIRST_NAME, firstName, Field.Store.YES));
		document.add(new TextField(LAST_NAME, firstName, Field.Store.YES));
		return document;
	}
}
