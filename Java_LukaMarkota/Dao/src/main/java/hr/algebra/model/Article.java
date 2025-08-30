/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author daniel.bele
 */
@XmlAccessorType(XmlAccessType.FIELD)
public final class Article {
    
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private int id;
    private String title;
    private String link;
    private String description;
    @XmlElement(name = "publisheddate")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime publishedDate;
    private Person creator;
    @XmlElementWrapper
    @XmlElement(name = "contributors")
    private List<Person> contributors;
    @XmlElement(name = "picturepath")
    private String picturePath;
    private String content;

    public List<Person> getContributors() {
        return contributors;
    }

    public void setContributors(List<Person> contributors) {
        this.contributors = contributors;
    }

    public Article(String title, String link, String description, LocalDateTime publishedDate, String picturePath, String content) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.publishedDate = publishedDate;
        this.picturePath = picturePath;
        this.content = content;
    }
    
    public Article(int id, String title, String link, String description, LocalDateTime publishedDate, Person creator, String picturePath, String content) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.publishedDate = publishedDate;
        this.creator = creator;
        this.picturePath = picturePath;
        this.content = content;
    }

    public Article(String title, String link, String description, LocalDateTime publishedDate, Person creator, String picturePath, String content) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.publishedDate = publishedDate;
        this.creator = creator;
        this.picturePath = picturePath;
        this.content = content;
    }

    public Article() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "id=" + id + ", title=" + title ;
    }
    

    
    
}

