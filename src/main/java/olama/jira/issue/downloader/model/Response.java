package olama.jira.issue.downloader.model;

import lombok.Data;

import java.util.List;

@Data
public class Response {

    private Details details;

    private People people;

    private Dates dates;

    private Description description;

    private List<Comments> comments;
}
