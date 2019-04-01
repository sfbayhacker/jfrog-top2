package jfrog.test.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import jfrog.test.model.Result;
import jfrog.test.service.Top2Service;
import jfrog.test.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author arun
 */
@RestController
@RequestMapping("/top2")
public class Top2Controller {
    @Autowired
    Top2Service top2Service;
    
    @RequestMapping(value = "", method = RequestMethod.GET,
            produces = {"application/json"})
    @ApiOperation(value = "Returns name and downloads information for top 2 artifacts", response = Result.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successful retrieval of top 2 artifacts", response = Result.class),
        @ApiResponse(code = 400, message = "Bad request params"),
        @ApiResponse(code = 500, message = "Internal server error")}
    )
    public ResponseEntity get(
            @RequestParam(value="repo", required = true) String repo,
            @RequestParam(value="apiKey", required = true) String apiKey) {
        
        if (StringUtils.isEmpty(repo) || StringUtils.isEmpty(apiKey)) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Pair<String, Integer>> top2List = top2Service.findTop2(repo, apiKey);
        Pair<String, Integer> first = null;
        Pair<String, Integer> second = null;
        if (top2List.size() > 0) first = top2List.get(0);
        if (top2List.size() > 1) second = top2List.get(1);
        
        return ResponseEntity.ok(new Result(first, second));
    }
}
