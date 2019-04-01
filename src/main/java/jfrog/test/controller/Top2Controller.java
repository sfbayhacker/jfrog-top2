package jfrog.test.controller;

import java.util.List;
import jfrog.test.model.Result;
import jfrog.test.service.Top2Service;
import jfrog.test.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author arun
 */
@RestController
public class Top2Controller {
    @Autowired
    Top2Service top2Service;
    
    @RequestMapping(value = "/top2", method = RequestMethod.GET,
            produces = {"application/json"})
    public Result get(
            @RequestParam(value="repo", required = true) String repo,
            @RequestParam(value="apiKey", required = true) String apiKey) {
        List<Pair<String, Integer>> top2List = top2Service.findTop2(repo, apiKey);
        Pair<String, Integer> first = null;
        Pair<String, Integer> second = null;
        if (top2List.size() > 0) first = top2List.get(0);
        if (top2List.size() > 1) second = top2List.get(1);
        
        return new Result(first, second);
    }
}
