package olama.jira.issue.downloader.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import olama.jira.issue.downloader.model.*;
import olama.jira.issue.downloader.controller.IdentifierType;
import olama.jira.issue.downloader.repository.Repository;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MyService {

    private final Repository repository;

    private ObjectMapper objectMapper;

    public MyService(Repository repository) {
        this.repository = repository;
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }


    public Response createResult(Document document){

        Response response = new Response();
        response.setPeople(createPeople(document));
        response.setDetails(createDetails(document));
        response.setDates(createDates(document));
        response.setDescription(createDescription(document));
        return response;

    }


    public Details createDetails(Document document){
        List<Element> values = getElementsByIdentifier(document, "value", IdentifierType.CLASS);
        Details details = new Details();
        for (Element value : values) {
            Attribute attribute = value.attribute("id");
            if (attribute != null) {
                if (attribute.getValue().equals("type-val")) {
                    String string = value.childNode(2).toString();
                    details.setIssueType(string);
                } else if (attribute.getValue().equals("priority-val")) {
                    String string = value.childNode(2).toString();
                    details.setPriority(string);
                } else if (attribute.getValue().equals("versions-val")) {
                    String string = value.childNode(1).childNode(1).childNode(0).toString();
                    details.setAffectsVersion(string);
                } else if (attribute.getValue().equals("status-val")) {
                    String string = value.childNode(1).childNode(0).toString();
                    details.setStatus(string);
                } else if (attribute.getValue().equals("resolution-val")) {
                    String string = value.childNode(0).toString();
                    details.setResolution(string);
                } else if (attribute.getValue().equals("components-val")) {
                    List<Node> nodes = value.childNode(1).childNode(1).childNodes();
                    List<String> list = nodes.stream().map(r -> r.toString()).toList();
                    details.setComponents(list);
                }
            }
        }
        values = getElementsByIdentifier(document, "labels", IdentifierType.CLASS);
        for (Element value : values) {
            List<Node> nodes = value.childNodes();
            List<String> list = nodes.stream().map(r -> r.toString()).toList();
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
        String string1 = values.get(0).childNode(1).childNode(2).toString().trim();
        people.setAssignee(string1);

        values = getElementsByIdentifier(document, "reporter-val", IdentifierType.ID);
        string1 = values.get(0).childNode(1).childNode(2).toString().trim();
        people.setReporter(string1);

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
        StringBuilder builder = new StringBuilder();
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
                System.out.println("Not a valid Identifier type");
        }

        return elements;
    }
}
