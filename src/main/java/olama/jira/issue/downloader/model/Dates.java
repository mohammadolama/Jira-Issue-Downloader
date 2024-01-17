package olama.jira.issue.downloader.model;

import lombok.Data;

import java.util.Date;
@Data
public class Dates {

    private String createAt;

    private String createdAtTimestamp;

    private String updatedAt;

    private String updatedAtTimestamp;

    private String resolvedAt;

    private String resolvedAtTimestamp;
}
