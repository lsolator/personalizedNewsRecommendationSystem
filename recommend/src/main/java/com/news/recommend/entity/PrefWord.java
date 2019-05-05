package com.news.recommend.entity;

import java.util.Objects;

public class PrefWord implements Comparable<PrefWord>{
    private String word;

    private Double wordNum;

    public PrefWord(String word, Double wordNum) {
        this.word = word;
        this.wordNum = wordNum;
    }

    public PrefWord() {
    }

    public PrefWord(String word) {
        this.word = word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setWordNum(Double wordNum) {
        this.wordNum = wordNum;
    }

    public String getWord() {
        return word;
    }

    public Double getWordNum() {
        return wordNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrefWord prefWord = (PrefWord) o;
        return word.equals(prefWord.word);
    }

    @Override
    public int hashCode() {
        return Objects.hash(word);
    }

    @Override
    public String toString() {
        return "PrefWord{" +
                "word='" + word + '\'' +
                ", wordNum=" + wordNum +
                '}';
    }

    @Override
    public int compareTo(PrefWord o) {
        return o.getWordNum().compareTo(this.getWordNum());
    }
}
