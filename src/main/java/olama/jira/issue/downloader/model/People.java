package olama.jira.issue.downloader.model;

import lombok.Data;

@Data
public class People {

    private String assignee;

    private String reporter;

    private int votes;

    private int watchers;
}
