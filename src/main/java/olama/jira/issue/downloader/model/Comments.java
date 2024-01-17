package olama.jira.issue.downloader.model;

import lombok.Data;

@Data
public class Comments {

    private String writer;

    private String date;

    private String content;
}
