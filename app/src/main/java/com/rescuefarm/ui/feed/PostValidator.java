package com.rescuefarm.ui.feed;

import java.util.ArrayList;
import java.util.List;

public final class PostValidator {
    private PostValidator() { }
    public static String validate(String title, String content) {
        int titleLength = clean(title).length(); int contentLength = clean(content).length();
        if (titleLength < 3 || titleLength > 120) return "Tiêu đề cần 3-120 ký tự.";
        if (contentLength < 10 || contentLength > 3000) return "Nội dung cần 10-3000 ký tự.";
        return null;
    }
    public static List<String> parseLines(String value) {
        List<String> result = new ArrayList<>();
        for (String item : clean(value).split("[,\\r\\n]+")) {
            String cleanItem = clean(item); if (!cleanItem.isEmpty() && !result.contains(cleanItem)) result.add(cleanItem);
        }
        return result;
    }
    private static String clean(String value) { return value == null ? "" : value.trim(); }
}
