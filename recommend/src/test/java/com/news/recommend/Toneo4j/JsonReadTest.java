package com.news.recommend.Toneo4j;

import org.junit.Test;

import static org.junit.Assert.*;

public class JsonReadTest {

//    tf-idf分词测试
    @Test
    public void testRead() {
        JsonRead jsonRead = new JsonRead();
        JsonRead.read2("F:\\code\\recommend\\src\\main\\resources\\test");
    }
}