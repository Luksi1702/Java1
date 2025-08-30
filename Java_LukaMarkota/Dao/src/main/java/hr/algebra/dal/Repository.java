/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.dal;

import hr.algebra.model.Article;
import hr.algebra.model.Person;
import hr.algebra.model.User;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author daniel.bele
 */
public interface Repository {

    /*Article crud*/
    int createArticle(Article article) throws Exception;

    Map<Integer, List<Integer>> createArticles(List<Article> articles) throws Exception;

    void updateArticle(int id, Article data) throws Exception;

    void deleteArticle(int id) throws Exception;

    Optional<Article> selectArticle(int id) throws Exception;

    List<Article> selectArticles() throws Exception;
    
    /*Person crud*/
    int createPerson(Person person) throws Exception;
    
    List<Integer> createPeople(List<Person> people) throws Exception;
    
    void updatePerson(int id, Person data) throws Exception;
    
    void deletePerson(int id) throws Exception;
    
    Optional<Person> selectPerson(int id) throws Exception;

    List<Person> selectPeople() throws Exception;
    
    /*User crud*/
    int createUser(User user) throws Exception;
    
    void createUsers(List<User> users) throws Exception;
    
    void updateUser(int id, User data) throws Exception;
    
    void deleteUser(int id) throws Exception;
    
    int checkUser(User user) throws Exception;
    
    String getSaltByUsername(String username) throws Exception;
    
    boolean checkIfUserIsAdmin(int userID) throws Exception;
            
    /*Contributor crud*/
    void insertArticleContributor(int id, List<Integer> personID) throws Exception;
    
    void deleteArticleContributor(int articleID, List<Integer> personID) throws Exception;
    
    List<Person> getArticleContributors(int id) throws Exception;
}
