package com.android.ifnewshub.utils;

import com.android.ifnewshub.model.News;

import java.util.List;

public class HtmlBuilder {

    public static String buildNewsCards(List<News> list) {
        StringBuilder html = new StringBuilder();

        for (News n : list) {
            html.append("<div class='news-card' onclick=\"Android.openNewsById('")
                    .append(n.getId())
                    .append("')\">")
                    .append("<h3>").append(formatTitle(n.getTitle())).append("</h3>")
                    .append("</div>");
        }

        return html.toString();
    }

    private static String formatTitle(String title) {

        String[] words = title.split(" ");
        StringBuilder newTitle = new StringBuilder();
        int total = 0;
        int maxText = 60;
        boolean first = true;

        for (String word : words) {
            total += word.length();
            if (total < maxText) {
                if (!first) newTitle.append(" ");
                newTitle.append(word);
                first = false;
            } else {
                return newTitle + " ...";
            }
        }

        return newTitle + " ...";
    }
}
