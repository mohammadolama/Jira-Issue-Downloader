package olama.jira.issue.downloader.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.opendevl.JFlat;
import lombok.extern.log4j.Log4j2;
import olama.jira.issue.downloader.model.Response;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.PrintWriter;

@Component
@Log4j2
public class Repository {


    ObjectMapper objectMapper = new ObjectMapper();

    public boolean save(Response response, String key) {
        try {

            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            String s = objectMapper.writeValueAsString(response);
            JFlat flatMe = new JFlat(s);

            File file = new File("src/main/resources/" + String.format("result_{%s}/", key));
            if (!file.exists()){
                try {
                    file.mkdirs();
                }catch (Exception e){
                    log.debug(e.getMessage());
                }
            }

            File csv = new File(file.getPath() + "/result.csv");

            flatMe.json2Sheet().headerSeparator(",").write2csv(csv.getPath());

            File json = new File(file.getPath() + "/result.json");
            File file1 = new File(json.getPath());
            PrintWriter printWriter = new PrintWriter(file1);
            printWriter.println(s);
            printWriter.flush();
            printWriter.close();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
