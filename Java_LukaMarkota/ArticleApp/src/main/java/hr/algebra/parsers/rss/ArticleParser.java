/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.parsers.rss;

import hr.algebra.dal.Repository;
import hr.algebra.dal.RepositoryFactory;
import hr.algebra.factory.ParserFactory;
import hr.algebra.factory.UrlConnectionFactory;
import hr.algebra.model.Article;
import hr.algebra.model.Person;
import hr.algebra.utilities.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static javax.swing.TransferHandler.LINK;
import static javax.swing.text.html.HTML.Attribute.CONTENT;
import static javax.swing.text.html.HTML.Attribute.TITLE;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import jdk.jshell.SourceCodeAnalysis.Attribute;

/**
 *
 * @author lukam
 */
public class ArticleParser {
    private static Repository repository;
    private static final String RSS_URL = "https://rss.politico.com/congress.xml";
    private static final String ATTRIBUTE_URL = "url";
    private static final String EXT = ".jpg";
    private static final String DIR = "assets";

    public static List<Article> parse() throws IOException, XMLStreamException, Exception {
        List<Article> articles = new ArrayList<>();
        repository = RepositoryFactory.getRepository();
        HttpURLConnection con = UrlConnectionFactory.getHttpUrlConnection(RSS_URL);
        try (InputStream is = con.getInputStream()) {
            XMLEventReader reader = ParserFactory.createStaxParser(is);

            Optional<TagType> tagType = Optional.empty();
            Article article = null;
            StartElement startElement = null;

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                switch (event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        startElement = event.asStartElement();
                        String qName = startElement.getName().getLocalPart();
                        tagType = TagType.from(qName);
                        if (tagType.isPresent() && tagType.get().equals(TagType.ITEM)) {
                            article = new Article();

                            if (!articles.isEmpty()) {
                                Article previousArticle = articles.get(articles.size() - 1);
                                System.out.println("Previous Article before adding new one: " + previousArticle);
                            } else {
                                System.out.println("No previous article, this is the first one.");
                            }

                            articles.add(article);
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (tagType.isPresent() && article != null) {
                            Characters characters = event.asCharacters();
                            String data = characters.getData().trim();
                            switch (tagType.get()) {
                                case TITLE:
                                    if (!data.isEmpty()) {
                                        article.setTitle(data);
                                    }
                                    break;
                                case LINK:
                                    if (!data.isEmpty()) {
                                        article.setLink(data);
                                    }
                                    break;
                                case DESCRIPTION:
                                    if (!data.isEmpty()) {
                                        article.setDescription(data);
                                    }
                                    break;
                                case PUBLISHED_DATE:
                                    if (!data.isEmpty()) {
                                        String fixedDate = data.replace("EST", "-0500");
                                        article.setPublishedDate(LocalDateTime.parse(
                                                fixedDate,
                                                DateTimeFormatter.RFC_1123_DATE_TIME));

                                    }
                                    break;
                                case MEDIA:
                                    if (startElement != null && article.getPicturePath() == null) {
                                        javax.xml.stream.events.Attribute att = startElement.getAttributeByName(new QName(ATTRIBUTE_URL));
                                        if (att != null) {
                                            handlePicture(article, att.getValue());
                                        }
                                    }
                                    break;
                                case CREATOR:
                                    if (!data.isEmpty()) {
                                        /*Tri*/
                                        String cleanCreator = data.replaceFirst("(?i)^by\\s+", "").trim();

                                        if (cleanCreator.contains(",")) {
                                            cleanCreator = cleanCreator.split(",")[0].trim();
                                        } else if (cleanCreator.toLowerCase().contains(" and ")) {
                                            cleanCreator = cleanCreator.split("(?i)\\s+and\\s+")[0].trim();
                                        }

                                        /*Creator check*/
                                        Person creator = null;
                                        String[] nameParts = cleanCreator.split(" ");

                                        if (nameParts.length >= 2) {
                                            String inputName = nameParts[0];
                                            String inputSurname = nameParts[1];

                                            List<Person> people = repository.selectPeople();

                                            for (Person person : people) {
                                                if (person.getName().equalsIgnoreCase(inputName)
                                                        && person.getSurname().equalsIgnoreCase(inputSurname)) {
                                                    creator = person;
                                                    break;  // Found the person, stop looping
                                                }
                                            }

                                            // If creator not found in DB, create new person
                                            if (creator == null) {
                                                Person newPerson = new Person(inputName, inputSurname);
                                                int idPerson = repository.createPerson(newPerson);
                                                newPerson.setId(idPerson);
                                                creator = newPerson;
                                            }
                                        }

                                        article.setCreator(creator);

                                    }
                                    break;
                                case CONTRIBUTOR:
                                    if (!data.isEmpty()) {
                                        List<Person> contributors = new ArrayList<>();

                                        String cleanedContributors = data.replaceFirst("(?i)^by\\s+", "").trim();

                                        String[] contributorParts = cleanedContributors.split("(?i),|\\s+and\\s+");

                                        List<Person> people = repository.selectPeople();

                                        for (String part : contributorParts) {
                                            String[] nameParts = part.trim().split("\\s+");

                                            if (nameParts.length >= 2) {
                                                String inputName = nameParts[0];
                                                String inputSurname = nameParts[1];
                                                Person contributor = null;

                                                for (Person person : people) {
                                                    if (person.getName().equalsIgnoreCase(inputName)
                                                            && person.getSurname().equalsIgnoreCase(inputSurname)) {
                                                        contributor = person;
                                                        break;
                                                    }
                                                }

                                                if (contributor == null) {
                                                    Person newPerson = new Person(inputName, inputSurname);
                                                    int idPerson = repository.createPerson(newPerson);
                                                    newPerson.setId(idPerson);
                                                    contributor = newPerson;
                                                }

                                                contributors.add(contributor);
                                            }
                                        }

                                        article.setContributors(contributors);       
                                    }
                                    break;

                                case CONTENT:
                                    if (!data.isEmpty()) {
                                        article.setContent(data);
                                    }
                                    break;
                            }
                        }

                        break;
                }
            }
        }

        return articles;
    }

    private static void handlePicture(Article article, String pictureUrl) {
        try {
            String ext = pictureUrl.substring(pictureUrl.lastIndexOf("."));
            if (ext.length() > 4) {
                ext = EXT;
            }
            String localPath = DIR + File.separator + UUID.randomUUID() + ext;
            FileUtils.copyFromUrl(pictureUrl, localPath);
            article.setPicturePath(localPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArticleParser() {
    }

    private enum TagType {
        ITEM("item"),
        TITLE("title"),
        LINK("link"),
        DESCRIPTION("description"),
        PUBLISHED_DATE("pubDate"),
        MEDIA("thumbnail"),
        CREATOR("creator"),
        CONTRIBUTOR("contributor"),
        CONTENT("encoded"),;
        private final String name;

        private TagType(String name) {
            this.name = name;
        }

        private static Optional<TagType> from(String name) {
            for (TagType value : values()) {
                if (value.name.equals(name)) {
                    return Optional.of(value);
                }
            }
            return Optional.empty();
        }

    }
}
