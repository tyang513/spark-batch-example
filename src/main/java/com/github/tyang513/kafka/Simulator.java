package com.github.tyang513.kafka;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by loong on 4/20/16.
 */
public class Simulator {

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(args[0]));
            List<String> lineList = new ArrayList<String>();
            String line = null;
            while ((line = br.readLine()) != null) {
                lineList.add(String.valueOf(System.nanoTime()) + line);
            }

            for (int i = 0; i < 1000; i++)
                for (String l : lineList){
                    KafkaService.getInstance("test", "172.23.7.125:9092").send(KafkaService.SRC_TYPE_WIFI, l.getBytes(), String.valueOf(System.nanoTime()));
                }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
