/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jfrog.test.service;

import java.util.List;
import jfrog.test.util.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * @author arun
 */
@SpringBootTest
public class Top2ServiceTest {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Top2ServiceTest.class);
    
    @InjectMocks
    Top2Service top2Service;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    public void findTop2() {
        String[] apiKey = {"", "AKCp5ccGCB5JYMuS8YvzDgNi2ssmD1Eegi2Qy9ACjNitwAXn8xHfZuw34oXTNfjfQG1YRjn7G", null,
            "AKCp5ccGCB5JYMuS8YvzDgNi2ssmD1Eegi2Qy9ACjNitwAXn8xHfZuw34oXTNfjfQG1YRjn7G"};
        String[] repo = {"gradle-release-local", null, "junk",
            "jcenter-cache"};
        
        List<Pair<String, Integer>> top2List = null;
        Pair<String, Integer> p1 = null;
        Pair<String, Integer> p2 = null;
        for(int i=0; i<apiKey.length; i++) {
            top2List = top2Service.findTop2(repo[i], apiKey[i]);
            
            Assert.assertNotNull(top2List);

            if (top2List.size() > 0) {
                p1 = top2List.get(0);
                Assert.assertNotNull(p1);
            }
            if (top2List.size() > 1) {
                p2 = top2List.get(1);
                Assert.assertNotNull(p2);
                Assert.assertTrue(p1.b >= p2.b);
            }
        }
            
    }
}
