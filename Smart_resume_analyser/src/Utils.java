// src/Utils.java
public class Utils {
    public static String cleanText(String text) {
        if (text == null) return "";
        return text.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }
}
