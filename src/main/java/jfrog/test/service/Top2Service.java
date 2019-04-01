package jfrog.test.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jfrog.test.util.HttpUtils;
import jfrog.test.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service for finding top 2 artifacts in a repository
 * 
 * @author arun
 */
@Service
public class Top2Service {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Top2Service.class);
    
    /**
     * find top two artifacts in the given repo
     * 
     * @param repo the repo name
     * @param apiKey the api key to access artifactory REST API
     * @return 
     */
    public List<Pair<String, Integer>> findTop2(String repo, String apiKey) {
        List<Pair<String, Integer>> artifacts = getArtifactsData(repo, apiKey);
        
        if (artifacts == null || artifacts.isEmpty()) return Collections.EMPTY_LIST;
        
        Pair<String, Integer> first = null;
        Pair<String, Integer> second = null;
        
        if (artifacts.size() == 1) {
            first = artifacts.get(0);
        } else if (artifacts.size() >= 2) {
            if (artifacts.get(0).b > artifacts.get(1).b) {
                first = artifacts.get(0);
                second = artifacts.get(1);
            } else {
                first = artifacts.get(1);
                second = artifacts.get(0);
            }
            
            if (artifacts.size() > 2) {
                for(int i=2; i<artifacts.size(); i++) {
                    if (artifacts.get(i).b >= first.b) {
                        second = first;
                        first = artifacts.get(i);
                    } else if (artifacts.get(i).b > second.b) {
                        second = artifacts.get(i);
                    }
                }
            }
        }
        List<Pair<String, Integer>> result = new ArrayList<>();
        if (first != null)  {
            result.add(first);
            if (logger.isDebugEnabled())
                logger.debug(String.format("Most popular is %s: %s downloads", first.a, String.valueOf(first.b)));
        }
        
        if (second != null)  {
            result.add(second);
            if (logger.isDebugEnabled())
                logger.debug(String.format("Second most popular is %s: %s downloads", second.a, String.valueOf(second.b)));
        }
        
        return result;
    }
    
    private List<Pair<String, Integer>> getArtifactsData(String repo, String apiKey) {
        if (StringUtils.isEmpty(repo) || StringUtils.isEmpty(apiKey)) return null;
        String payload = String.format("items.find(\n" +
            "{\n" +
            "\"repo\":{\"$eq\":\"%s\"}\n" +
            "}\n" +
            ").include(\"path\", \"name\", \"stat.downloads\")", repo);
        
        String url = "https://jfrog.local/artifactory/api/search/aql";
        String response = null;
        List<Pair<String, Integer>> artifacts = null;
        try {
            response = HttpUtils.postRequest(url, apiKey, payload);
            artifacts = getPathInfo(response);
        } catch (Exception ex) {
            logger.error("Error while fetching artifact list", ex);
            return null;
        }
        
        return artifacts;
    }
    
    private List<Pair<String, Integer>> getPathInfo(String artifacts) throws IOException {
        if (artifacts == null) return null;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(artifacts);
        JsonNode results = tree.get("results");
        List<Pair<String, Integer>> pathInfo = new ArrayList<>();
        if (results != null && results instanceof ArrayNode) {
            for(JsonNode n: (ArrayNode)results) {
                pathInfo.add(
                    new Pair<>(
                        n.get("path").textValue() + "/" + n.get("name").textValue(),
                        ((ArrayNode)n.get("stats")).get(0).get("downloads").intValue()
                    )
                );
            }
        }
        
        return pathInfo;
    }
    
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("API Key: ");
        String apiKey = sc.nextLine();
        System.out.println("Repo Name: ");
        String repo = sc.nextLine();
        
        System.out.println("API Key = " + apiKey);
        
        System.out.println("Fetching artifacts for repo " + repo + "..");
        List<Pair<String, Integer>> result = new Top2Service().findTop2(repo, apiKey);
        System.out.println("Top 2 artifacts are: " + result);
    }
}
