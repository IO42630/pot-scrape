package com.olexyn.pot.scrape;

import com.olexyn.tabdriver.TabDriverBuilder;

import java.nio.file.Path;

public class App {
    public static void main(String[] args) {
        var confPath = Path.of("/home/user/home/ws/pot-scrape/src/main/resources/tabdriver.properties");
        var td = TabDriverBuilder.build(confPath);
        int br = 0;
    }
}
