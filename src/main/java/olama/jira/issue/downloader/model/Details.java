package olama.jira.issue.downloader;

import lombok.Data;

import java.util.List;

@Data
public class Details {

    private String issueType;

    private String priority;

    private String affectsVersion;

    private String status;

    private String resolution;

    private List<String> fixVersion;

    private List<String> components;

    private List<String> labels;

    private String patchInfo;

    private String estimatedComplexity;
}
