package olama.jira.issue.downloader.controller;

import lombok.extern.log4j.Log4j2;
import olama.jira.issue.downloader.model.Response;
import olama.jira.issue.downloader.service.MyService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Log4j2
public class Controller {


    @Value("${github.base_address}")
    private String baseAddress;

    private final MyService service;

    public Controller(MyService service) {
        this.service = service;
    }


    @GetMapping("/track/{id}")

    public Response trackIssue(@PathVariable("id") String key) throws Exception {

        String url = baseAddress.replace("{ISSUE}", key);


        Document documentFromURL = getDocumentFromURL(url);

        return service.createResult(documentFromURL , key);
    }

    public Document getDocumentFromURL(String resourceUrl) throws IOException {
        return  Jsoup
                .connect(resourceUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                .header("Accept-Language", "*")
                .get();
    }


}
