package com.news.recommend.Toneo4j;

import com.google.gson.Gson;
import java.io.*;
import java.util.ArrayList;

public class JsonRead {
//    读取文件
    public static ArrayList<BNews> read2(String filePath){
        ArrayList<BNews> news = new ArrayList<>();
        System.out.println("------second method-------");
        File file = new File(filePath);
        if(file.exists()){
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader br = new BufferedReader(fileReader);
                String lineContent = null;
//                计数
//                Integer flag = 0;

                while((lineContent = br.readLine())!=null){
                    BNews bNews = new Gson().fromJson(lineContent, BNews.class);
                    news.add(bNews);
                    System.out.println(bNews.getTitle()+"----"+bNews.getTag());
//                    flag++;
                }
                br.close();
                fileReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("no this file");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("io exception");
                e.printStackTrace();
            }
        }
        return news;
    }

}
