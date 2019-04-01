package jfrog.test.model;

import jfrog.test.util.Pair;

/**
 *
 * @author arun
 */
public class Result {

    private String first;
    private int firstDownloads;
    private String second;
    private int secondDownloads;

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

    public int getFirstDownloads() {
        return firstDownloads;
    }

    public void setFirstDownloads(int firstDownloads) {
        this.firstDownloads = firstDownloads;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public int getSecondDownloads() {
        return secondDownloads;
    }

    public void setSecondDownloads(int secondDownloads) {
        this.secondDownloads = secondDownloads;
    }
}
