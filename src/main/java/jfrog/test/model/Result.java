package jfrog.test.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jfrog.test.util.Pair;

/**
 *
 * @author arun
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {

    private String first;
    private Integer firstDownloads;
    private String second;
    private Integer secondDownloads;

    public Result(Pair<String, Integer> first, Pair<String, Integer> second) {
        if (first != null) {
            this.first = first.a;
            this.firstDownloads = first.b;
        }

        if (second != null) {
            this.second = second.a;
            this.secondDownloads = second.b;
        }
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public Integer getFirstDownloads() {
        return firstDownloads;
    }

    public void setFirstDownloads(Integer firstDownloads) {
        this.firstDownloads = firstDownloads;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public Integer getSecondDownloads() {
        return secondDownloads;
    }

    public void setSecondDownloads(Integer secondDownloads) {
        this.secondDownloads = secondDownloads;
    }
}
