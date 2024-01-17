package olama.jira.issue.downloader.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import olama.jira.issue.downloader.model.*;
import olama.jira.issue.downloader.model.IdentifierType;
import olama.jira.issue.downloader.repository.Repository;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class MyService {

    private final Repository repository;


    public MyService(Repository repository) {
        this.repository = repository;
    }


    public Response createResult(Document document , String key){

        Response response = new Response();
        response.setPeople(createPeople(document));
        response.setDetails(createDetails(document));
        response.setDates(createDates(document));
        response.setDescription(createDescription(document));
        repository.save(response , key);
        return response;

    }


    public Details createDetails(Document document){
        List<Element> values = getElementsByIdentifier(document, "value", IdentifierType.CLASS);
        Details details = new Details();
        for (Element value : values) {
            Attribute attribute = value.attribute("id");
            if (attribute != null) {
                switch (attribute.getValue()) {
                    case "type-val" -> {
                        String string = value.childNode(2).toString();
                        details.setIssueType(string);
                    }
                    case "priority-val" -> {
                        String string = value.childNode(2).toString();
                        details.setPriority(string);
                    }
                    case "versions-val" -> {
                        String string = value.childNode(1).childNode(1).childNode(0).toString();
                        details.setAffectsVersion(string);
                    }
                    case "status-val" -> {
                        String string = value.childNode(1).childNode(0).toString();
                        details.setStatus(string);
                    }
                    case "resolution-val" -> {
                        String string = value.childNode(0).toString();
                        details.setResolution(string);
                    }
                    case "components-val" -> {
                        List<Node> nodes = value.childNode(1).childNode(1).childNodes();
                        List<String> list = nodes.stream().map(Node::toString).toList();
                        details.setComponents(list);
                    }
                }
            }
        }
        values = getElementsByIdentifier(document, "labels", IdentifierType.CLASS);
        for (Element value : values) {
            List<Node> nodes = value.childNodes();
            List<String> list = nodes.stream().map(Node::toString).toList();
            details.setLabels(list);
        }
        values = getElementsByIdentifier(document, "value type-select", IdentifierType.CLASS);
        for (Element value : values) {
            String string = value.childNode(0).toString().replace("\n" , "").trim();
            details.setPatchInfo(string);
        }

        values = getElementsByIdentifier(document, "value type-select", IdentifierType.CLASS);
        for (Element value : values) {
            String string = value.childNode(0).toString().replace("\n","").trim();
            details.setEstimatedComplexity(string);
        }
        values = getElementsByIdentifier(document, "customfield_12310041-field", IdentifierType.ID);
        for (Element value : values) {
            String string = value.childNode(1).childNode(0).toString();
            details.setPatchInfo(string);
        }

        values = getElementsByIdentifier(document, "fixfor-val", IdentifierType.ID);
        List<Node> nodes = values.get(0).childNode(1).childNodes();
        List<String> res = new ArrayList<>();
        for (int i = 1; i < nodes.size(); i+=2) {
            String string = nodes.get(i).childNode(0).toString();
            res.add(string);
        }
        details.setFixVersion(res);
        return details;
    }

    private People createPeople(Document document){
        People people = new People();

        List<Element> values = getElementsByIdentifier(document, "assignee-val", IdentifierType.ID);
        String string1;
        try {
             string1= values.get(0).childNode(1).childNode(2).toString().trim();
            people.setAssignee(string1);
        }catch (Exception e){
            string1 = values.get(0).childNode(2).toString().trim();
            people.setAssignee(string1);
        }

        values = getElementsByIdentifier(document, "reporter-val", IdentifierType.ID);
        try {
            string1 = values.get(0).childNode(1).childNode(2).toString().trim();
            people.setReporter(string1);
        }catch (Exception e){
            string1 = values.get(0).childNode(2).toString().trim();
            people.setAssignee(string1);
        }
        values = getElementsByIdentifier(document, "vote-data", IdentifierType.ID);
        string1 = values.get(0).childNode(0).toString().replace("\n" , "").trim();
        people.setVotes(Integer.parseInt(string1));


        values = getElementsByIdentifier(document, "watcher-data", IdentifierType.ID);
        string1 = values.get(0).childNode(0).toString().replace("\n" , "").trim();
        people.setWatchers( Integer.parseInt(string1));
        return people;
    }

    private Dates createDates(Document document){
        Dates dates = new Dates();

        List<Element> values = getElementsByIdentifier(document, "created-val", IdentifierType.ID);
        dates.setCreatedAtTimestamp(values.get(0).childNode(1).attr("datetime"));
        dates.setCreateAt(values.get(0).childNode(1).childNode(0).toString());

        values = getElementsByIdentifier(document, "updated-val", IdentifierType.ID);
        dates.setUpdatedAtTimestamp(values.get(0).childNode(1).attr("datetime"));
        dates.setUpdatedAt(values.get(0).childNode(1).childNode(0).toString());

        values = getElementsByIdentifier(document, "resolutiondate-val", IdentifierType.ID);
        dates.setResolvedAtTimestamp(values.get(0).childNode(1).attr("datetime"));
        dates.setResolvedAt(values.get(0).childNode(1).childNode(0).toString());
        return dates;
    }

    private Description createDescription(Document document){
        Description description = new Description();

        List<Element> values = getElementsByIdentifier(document, "description-val", IdentifierType.ID);

        List<Node> nodes = values.get(0).childNode(1).childNodes();
        List<String> res = new ArrayList<>();
        for (Node node : nodes) {
            if (!node.toString().trim().isBlank()){
                res.add(node.toString().replace("<p>" , "").replace("</p>" , ""));
            }
        }
        description.setDescription(res);
        return description;
    }

    private List<Element> getElementsByIdentifier(Document document ,String identifier, IdentifierType identifiertype){
        List<Element> elements = new ArrayList<>();

        switch (identifiertype){
            case ID:
                elements.add(document.getElementById(identifier));
                return elements;
            case TAG:
                return document.getElementsByTag(identifier);
            case ATTRIBUTE:
                return document.getElementsByAttribute(identifier);
            case CLASS:
                return document.getElementsByClass(identifier);
            default:
                log.debug("Not a valid Identifier type");
        }

        return elements;
    }
}
