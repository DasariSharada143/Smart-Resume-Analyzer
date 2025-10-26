import java.io.*;
import java.util.*;
import java.util.regex.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.Loader;

public class ResumeExtractor {

    public static String extractText(String pdfPath) {
        try (PDDocument document = Loader.loadPDF(new File(pdfPath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String readTextFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static ResumeInfo parseResume(String text) {
        ResumeInfo info = new ResumeInfo();

        String[] lines = text.split("\\r?\\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                info.name = line.trim();
                break;
            }
        }

        Matcher emailMatcher = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,6}")
                .matcher(text);
        if (emailMatcher.find()) info.email = emailMatcher.group();

        Matcher phoneMatcher = Pattern.compile("\\+?\\d[\\d -]{8,}\\d")
                .matcher(text);
        if (phoneMatcher.find()) info.phone = phoneMatcher.group();

        // Extract skills
        String[] possibleSkills = {"Java","Python","SQL","HTML","CSS","JavaScript","C++","C#","React","Node.js",
                                   "AWS","Docker","TensorFlow","Keras","Excel","Tableau","PowerBI"};
        List<String> skillsFound = new ArrayList<>();
        for (String skill : possibleSkills) {
            if (text.toLowerCase().contains(skill.toLowerCase())) skillsFound.add(skill);
        }
        info.skills = skillsFound;

        // Optional: education line
        for (String line : lines) {
            if (line.toLowerCase().contains("b.tech") || line.toLowerCase().contains("b.e") ||
                line.toLowerCase().contains("m.tech") || line.toLowerCase().contains("diploma")) {
                info.education = line.trim();
                break;
            }
        }

        return info;
    }
}
