package com.olexyn.pot.scrape;

import com.olexyn.propconf.PropConf;
import com.olexyn.tabdriver.TabDriver;
import com.olexyn.tabdriver.TabDriverBuilder;
import org.openqa.selenium.By;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.olexyn.tabdriver.Constants.CHECKBOX;
import static com.olexyn.tabdriver.Constants.CLASS;
import static com.olexyn.tabdriver.Constants.DIV;
import static com.olexyn.tabdriver.Constants.INPUT;
import static com.olexyn.tabdriver.Constants.LABEL;

public class App {
    public static void main(String[] args) throws InterruptedException {

        var confPath = Path.of("TODO_CONFIG/pot-scrape/src/main/resources/tabdriver.properties");
        PropConf.loadProperties("TODO_CONFIG/ws/pot-scrape/src/main/resources/config.properties");
        var td = TabDriverBuilder.build(confPath);
        td.get("https://hotpot.ai/login");
        td.getByFieldValue(LABEL, CLASS, "radioButton login").click();
        td.getByFieldValue(INPUT, CLASS, "email").sendKeys(PropConf.get("user"));
        td.getByFieldValue(INPUT, CLASS, "password").sendKeys(PropConf.get("pwd"));
        td.getByFieldValue(DIV, CLASS, "button submit").click();
        Thread.sleep(2000L);
        goToArtMaker(td);

        var htmlStr = td.getPageSource();

        List<String> artIdList = new ArrayList<>();
        Pattern pattern = Pattern.compile("href=\"/s/art-generator/([^\"]+)\"");
        Matcher matcher = pattern.matcher(htmlStr);

        while (matcher.find()) {
            artIdList.add(matcher.group(1).split("/")[0]);
        }

        for (var artId : artIdList) {
            if (!exists(artId)) {
                td.get("https://hotpotmedia.s3.us-east-2.amazonaws.com/" + artId + ".png");
            }
        }

        goToArtMaker(td);
        int count = 0;
        for (var artId : artIdList) {
            if (!exists(artId)) {
                continue;
            }

            var item = td.getByFieldValue(DIV, "data-itemid", artId);
            var check = item.findElement(By.className(CHECKBOX));
            td.setRadio(check, true);
            count++;
            if (count >= 100) {
                td.getByFieldValue(DIV, CLASS, "button delete").click();
                count = 0;
                Thread.sleep(5000L);
            }
            td.getByFieldValue(DIV, CLASS, "button delete").click();

        }

    }

    private static Path expectedPath(String artId) {
        String expectedPath = PropConf.get("download.dir") + artId + ".png";
        return FileSystems.getDefault().getPath(expectedPath);
    }

    private static boolean exists(String artId) {
        return Files.exists(expectedPath(artId));
    }

    private static void goToArtMaker(TabDriver td) throws InterruptedException {
        td.get("https://hotpot.ai/drive");
        Thread.sleep(1000L);
        td.getByFieldValue(DIV, "serviceid", "8").click(); // Art Maker
        Thread.sleep(15000L);
    }

}
