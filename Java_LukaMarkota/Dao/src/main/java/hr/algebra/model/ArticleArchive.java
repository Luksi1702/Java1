/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.model;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author filip
 */
@XmlRootElement(name = "articlearchive")
public final class ArticleArchive {
    @XmlElementWrapper
    @XmlElement(name = "article")
    List<Article> articles;

    public ArticleArchive() {
    }

    public ArticleArchive(List<Article> articles) {
        this.articles = articles;
    }

    public List<Article> getArticles() {
        return articles;
    }
    
}
