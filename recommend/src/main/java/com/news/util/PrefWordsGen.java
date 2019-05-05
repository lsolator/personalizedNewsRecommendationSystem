package com.news.util;

import com.google.gson.Gson;
import com.news.recommend.entity.PrefWord;

import java.util.*;

public class PrefWordsGen {
    private List<PrefWord> prefWordList;

    public PrefWordsGen() {
    }

    public List<PrefWord> getPrefWordList() {
        return prefWordList;
    }

    public void setPrefWordList(List<PrefWord> prefWordList) {
        this.prefWordList = prefWordList;
    }

    /**
     * 重组偏好词 并对其进行排序 方便后期的处理
     * @param prefWords
     */
    public void addPrefWord(Collection<PrefWord> prefWords) {
        if (this.prefWordList == null) {
            this.prefWordList = new ArrayList<>(10);
        }
        this.prefWordList.addAll(prefWords);
    }


    /**
     * 偏好词工具类  返回经过序列化后的偏好词列表
     * @return
     */
    public String getPrefWord() {
        Collections.sort(this.prefWordList);
        if (this.prefWordList.size() > 20) {
            this.prefWordList = this.prefWordList.subList(0, 20);
        }
//        设定阈值
        for (PrefWord p : this.prefWordList
                ) {
            if (p.getWordNum() < 0.0025) {    //大约看了50篇新闻都没有看过这种类型
                this.prefWordList.remove(p);
            }
        }
        return new Gson().toJson(this.prefWordList);
    }
}
