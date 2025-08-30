/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.dal.sql;

import hr.algebra.dal.Repository;
import hr.algebra.model.Article;
import hr.algebra.model.Person;
import hr.algebra.model.User;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;

public class SqlRepository implements Repository {

    /*Article const*/
    private static final String ID_ARTICLE = "IDArticle";
    private static final String TITLE = "Title";
    private static final String LINK = "Link";
    private static final String DESCRIPTION = "Description";
    private static final String PUBLISHED_DATE = "PublishedDate";
    private static final String CREATOR = "Creator";
    private static final String PICTURE_PATH = "PicturePath";
    private static final String CONTENT = "Content";

    /*Person const*/
    private static final String ID_PERSON = "IDPerson";
    private static final String NAME = "Name";
    private static final String SURNAME = "Surname";
    private static final String EMAIL = "Email";

    /*User const*/
    private static final String ID_USER = "IDUser";
    private static final String USERNAME = "Username";
    private static final String PWDHASH = "PwdHash";
    private static final String PWDSALT = "PwdSalt";
    private static final String ISADMIN = "IsAdmin";
    private static final String PERSONID = "PersonID";

    /*Article crud const*/
    private static final String CREATE_ARTICLE = "{ CALL createArticle (?,?,?,?,?,?,?,?) }";
    private static final String UPDATE_ARTICLE = "{ CALL updateArticle (?,?,?,?,?,?,?,?) }";
    private static final String DELETE_ARTICLE = "{ CALL deleteArticle (?) }";
    private static final String SELECT_ARTICLE = "{ CALL selectArticle (?) }";
    private static final String SELECT_ARTICLES = "{ CALL selectArticles () }";

    /*Person crud const*/
    private static final String CREATE_PERSON = "{ CALL createPerson (?,?,?,?) }";
    private static final String UPDATE_PERSON = "{ CALL updatePerson (?,?,?,?) }";
    private static final String DELETE_PERSON = "{ CALL deletePerson (?) }";
    private static final String SELECT_PERSON = "{ CALL selectPerson (?) }";
    private static final String SELECT_PEOPLE = "{ CALL selectPeople () }";

    /*User crud const*/
    private static final String CREATE_USER = "{ CALL createUser (?,?,?,?,?,?) }";
    private static final String UPDATE_USER = "{ CALL updateUser (?,?,?,?,?,?) }";
    private static final String DELETE_USER = "{ CALL deleteUser (?) }";
    private static final String GETSALTBYUSERNAME = "{ CALL getSaltByUsername (?, ?) }";
    private static final String CHECK_USER = "{ CALL checkUser (?,?,?,?) }";
    private static final String CHECKIFUSERISADMIN = "{ CALL checkIfUserIsAdmin (?,?) }";

    /*ArticleContributor crud const*/
    private static final String INSERTARTICLECONTRIBUTOR = "{ CALL insertArticleContributor (?,?) }";
    private static final String DELETEARTICLECONTRIBUTOR = "{ CALL deleteArticleContributor (?,?) }";
    private static final String GETARTICLECONTRIBUTORS = "{ CALL getArticleContributors (?) }";

    @Override
    public int createArticle(Article article) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(CREATE_ARTICLE)) {

            stmt.setString(TITLE, article.getTitle());
            stmt.setString(LINK, article.getLink());
            stmt.setString(DESCRIPTION, article.getDescription());
            stmt.setString(PUBLISHED_DATE, article.getPublishedDate().format(Article.DATE_FORMATTER));
            stmt.setInt(CREATOR, article.getCreator().getId());
            stmt.setString(PICTURE_PATH, article.getPicturePath());
            stmt.setString(CONTENT, article.getContent());
            stmt.registerOutParameter(ID_ARTICLE, Types.INTEGER);

            stmt.executeUpdate();
            return stmt.getInt(ID_ARTICLE);
        }
    }

    @Override
    public Map<Integer, List<Integer>> createArticles(List<Article> articles) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        Map<Integer, List<Integer>> dictionary = new HashMap<>();

        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(CREATE_ARTICLE)) {

            for (Article article : articles) {
                stmt.setString(TITLE, article.getTitle());
                stmt.setString(LINK, article.getLink());
                stmt.setString(DESCRIPTION, article.getDescription());
                stmt.setString(PUBLISHED_DATE, article.getPublishedDate().format(Article.DATE_FORMATTER));

                if (article.getCreator() != null) {
                    stmt.setInt(CREATOR, article.getCreator().getId());
                } else {
                    stmt.setInt(CREATOR, 2); // Default creator ID
                }

                stmt.setString(PICTURE_PATH, article.getPicturePath());
                stmt.setString(CONTENT, article.getContent());

                stmt.registerOutParameter(ID_ARTICLE, Types.INTEGER);

                stmt.executeUpdate();

                int generatedArticleId = stmt.getInt(ID_ARTICLE);

                List<Person> people = article.getContributors();

                if (people == null || people.isEmpty()) {
                    // No contributors — skip or continue
                } else {
                    List<Integer> contributorIds = new ArrayList<>();
                    for (Person person : people) {
                        if (person != null) {
                            contributorIds.add(person.getId());
                        }
                    }
                    if (!contributorIds.isEmpty()) {
                        dictionary.put(generatedArticleId, contributorIds);
                    }
                }

            }
        }

        return dictionary;
    }

    @Override
    public void updateArticle(int id, Article article) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(UPDATE_ARTICLE)) {

            stmt.setString(TITLE, article.getTitle());
            stmt.setString(LINK, article.getLink());
            stmt.setString(DESCRIPTION, article.getDescription());
            stmt.setString(PUBLISHED_DATE, article.getPublishedDate().format(Article.DATE_FORMATTER));
            stmt.setInt(CREATOR, article.getCreator().getId());
            stmt.setString(PICTURE_PATH, article.getPicturePath());
            stmt.setString(CONTENT, article.getContent());
            stmt.setInt(ID_ARTICLE, id);

            stmt.executeUpdate();

        }
    }

    @Override
    public void deleteArticle(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(DELETE_ARTICLE)) {

            stmt.setInt(ID_ARTICLE, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Article> selectArticle(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(SELECT_ARTICLE)) {

            stmt.setInt(ID_ARTICLE, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int creatorId = rs.getInt(CREATOR);
                    Person creator = selectPerson(creatorId)
                            .orElseThrow(() -> new Exception("Creator not found with ID: " + creatorId));

                    return Optional.of(new Article(
                            rs.getInt(ID_ARTICLE),
                            rs.getString(TITLE),
                            rs.getString(LINK),
                            rs.getString(DESCRIPTION),
                            LocalDateTime.parse(rs.getString(PUBLISHED_DATE), Article.DATE_FORMATTER),
                            creator,
                            rs.getString(PICTURE_PATH),
                            rs.getString(CONTENT)
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Article> selectArticles() throws Exception {
        List<Article> articles = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(SELECT_ARTICLES); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int creatorId = rs.getInt(CREATOR);
                Person creator = selectPerson(creatorId)
                        .orElseThrow(() -> new Exception("Creator not found with ID: " + creatorId));

                articles.add(new Article(
                        rs.getInt(ID_ARTICLE),
                        rs.getString(TITLE),
                        rs.getString(LINK),
                        rs.getString(DESCRIPTION),
                        LocalDateTime.parse(rs.getString(PUBLISHED_DATE), Article.DATE_FORMATTER),
                        creator,
                        rs.getString(PICTURE_PATH),
                        rs.getString(CONTENT)
                ));
            }
        }
        return articles;
    }

    @Override
    public int createPerson(Person person) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(CREATE_PERSON)) {

            stmt.setString(NAME, person.getName());
            stmt.setString(SURNAME, person.getSurname());
            stmt.setString(EMAIL, person.getEmail());

            stmt.registerOutParameter(ID_PERSON, Types.INTEGER);

            stmt.executeUpdate();
            return stmt.getInt(ID_PERSON);
        }
    }

    @Override
    public List<Integer> createPeople(List<Person> people) throws Exception {
        List<Integer> ids = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();

        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(CREATE_PERSON)) {

            for (Person person : people) {
                stmt.setString(NAME, person.getName());
                stmt.setString(SURNAME, person.getSurname());
                stmt.setString(EMAIL, person.getEmail());

                stmt.registerOutParameter(ID_PERSON, Types.INTEGER);
                stmt.executeUpdate();
                int id = stmt.getInt(ID_PERSON);
                ids.add(id);
            }
        }

        return ids;
    }

    @Override
    public void updatePerson(int id, Person data) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(UPDATE_PERSON)) {

            stmt.setString(NAME, data.getName());
            stmt.setString(SURNAME, data.getSurname());
            stmt.setString(EMAIL, data.getEmail());

            stmt.setInt(ID_PERSON, id);

            stmt.executeUpdate();

        }
    }

    @Override
    public void deletePerson(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(DELETE_PERSON)) {

            stmt.setInt(ID_PERSON, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public Optional<Person> selectPerson(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(SELECT_PERSON)) {

            stmt.setInt(ID_PERSON, id);
            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(new Person(
                            rs.getInt(ID_PERSON),
                            rs.getString(NAME),
                            rs.getString(SURNAME),
                            rs.getString(EMAIL)));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Person> selectPeople() throws Exception {
        List<Person> people = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(SELECT_PEOPLE); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                people.add(new Person(
                        rs.getInt(ID_PERSON),
                        rs.getString(NAME),
                        rs.getString(SURNAME),
                        rs.getString(EMAIL)));
            }
        }
        return people;
    }

    @Override
    public int createUser(User user) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(CREATE_USER)) {

            stmt.setString(USERNAME, user.getUsername());
            stmt.setString(PWDHASH, user.getPwdHash());
            stmt.setString(PWDSALT, user.getPwdSalt());
            stmt.setBoolean(ISADMIN, user.getIsAdmin());
            stmt.setInt(PERSONID, user.getPersonID());

            stmt.registerOutParameter(ID_USER, Types.INTEGER);

            stmt.executeUpdate();
            return stmt.getInt(ID_USER);
        }
    }

    @Override
    public void createUsers(List<User> users) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(CREATE_USER)) {

            for (User user : users) {
                stmt.setString(USERNAME, user.getUsername());
                stmt.setString(PWDHASH, user.getPwdHash());
                stmt.setString(PWDSALT, user.getPwdSalt());
                stmt.setBoolean(ISADMIN, user.getIsAdmin());
                stmt.setInt(PERSONID, user.getPersonID());

                stmt.registerOutParameter(ID_USER, Types.INTEGER);

                stmt.executeUpdate();
            }

        }
    }

    @Override
    public void updateUser(int id, User data) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(UPDATE_USER)) {

            stmt.setString(USERNAME, data.getUsername());
            stmt.setString(PWDHASH, data.getPwdHash());
            stmt.setString(PWDSALT, data.getPwdSalt());
            stmt.setBoolean(ISADMIN, data.getIsAdmin());
            stmt.setInt(PERSONID, data.getPersonID());

            stmt.setInt(ID_USER, id);

            stmt.executeUpdate();

        }
    }

    @Override
    public void deleteUser(int id) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(DELETE_USER)) {

            stmt.setInt(ID_USER, id);

            stmt.executeUpdate();
        }
    }

    @Override
    public void insertArticleContributor(int id, List<Integer> personID) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(INSERTARTICLECONTRIBUTOR)) {

            for (Integer personId : personID) {
                stmt.setInt(ID_ARTICLE, id);
                stmt.setInt(ID_PERSON, personId);
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public void deleteArticleContributor(int articleID, List<Integer> personID) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(DELETEARTICLECONTRIBUTOR)) {

            for (Integer personId : personID) {
                stmt.setInt(ID_ARTICLE, articleID);
                stmt.setInt(ID_PERSON, personId);
                stmt.executeUpdate();
            }
        }
    }

    @Override
    public List<Person> getArticleContributors(int id) throws Exception {
        List<Person> people = new ArrayList<>();
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(GETARTICLECONTRIBUTORS)) {

            stmt.setInt(ID_ARTICLE, id);
            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    people.add(new Person(
                            rs.getInt(ID_PERSON),
                            rs.getString(NAME),
                            rs.getString(SURNAME),
                            rs.getString(EMAIL)));
                }
            }
        }
        return people;
    }

    @Override
    public int checkUser(User user) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(CHECK_USER)) {

            stmt.setString(USERNAME, user.getUsername());
            stmt.setString(PWDHASH, user.getPwdHash());
            stmt.setString(PWDSALT, user.getPwdSalt());

            stmt.registerOutParameter(ID_USER, Types.INTEGER);

            stmt.executeUpdate();
            return stmt.getInt(ID_USER);
        }
    }

    @Override
    public String getSaltByUsername(String username) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(GETSALTBYUSERNAME)) {

            stmt.setString(USERNAME, username);
            stmt.registerOutParameter(PWDSALT, Types.NVARCHAR);

            stmt.executeUpdate();
            return stmt.getString(PWDSALT);
        }
    }
    
    @Override
    public boolean checkIfUserIsAdmin(int userID) throws Exception {
        DataSource dataSource = DataSourceSingleton.getInstance();
        try (Connection con = dataSource.getConnection(); CallableStatement stmt = con.prepareCall(CHECKIFUSERISADMIN)) {

            stmt.setInt(ID_USER, userID);
            stmt.registerOutParameter(ISADMIN, Types.BOOLEAN);

            stmt.executeUpdate();
            return stmt.getBoolean(ISADMIN);
        }
    }
}
