import static spark.Spark.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.json.JSONObject;

public class WebApp {

    private static List<ResumeInfo> allResumes = new ArrayList<>();
    private static String jobDescText = "";  // <- make it static and global

    public static void main(String[] args) {
        port(4567);

        File publicDir = new File("public");
        if (!publicDir.exists()) {
            System.err.println("Error: 'public' folder not found!");
            System.exit(1);
        }
        staticFiles.externalLocation(publicDir.getAbsolutePath());

        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) uploadDir.mkdir();

        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });

        // Upload resumes
        post("/upload", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(uploadDir.getAbsolutePath()));
            Collection<Part> fileParts;
            try { fileParts = req.raw().getParts(); } 
            catch (Exception e) { return "<h3>Error reading files: " + e.getMessage() + "</h3>"; }

            jobDescText = ResumeExtractor.readTextFile("resumes/job_description.txt"); // store globally

            for (Part filePart : fileParts) {
                String fileName = filePart.getSubmittedFileName();
                if(fileName == null || fileName.isEmpty()) continue;

                File uploadedFile = new File(uploadDir, fileName);
                try (InputStream input = filePart.getInputStream();
                     OutputStream output = new FileOutputStream(uploadedFile)) {
                    byte[] buffer = new byte[1024]; int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) output.write(buffer, 0, bytesRead);
                }

                ResumeInfo resumeInfo = ResumeExtractor.parseResume(ResumeExtractor.extractText(uploadedFile.getAbsolutePath()));
                SkillMatcher.calculateScore(resumeInfo, jobDescText);
                allResumes.add(resumeInfo);
            }

            return generateResumeSelectionForm();
        });

        // Select resumes
        post("/select", (req, res) -> {
            String[] selectedIndices = req.queryParamsValues("selectCandidate");
            if(selectedIndices == null || selectedIndices.length == 0)
                return "<h3>No resume selected!</h3>";

            StringBuilder output = new StringBuilder();
            List<ResumeInfo> selectedResumes = new ArrayList<>();

            for(String idxStr : selectedIndices) {
                int idx = Integer.parseInt(idxStr);
                if(idx >= allResumes.size()) continue;
                ResumeInfo r = allResumes.get(idx);
                selectedResumes.add(r);
            }

            output.append("<h2>Selected Candidates:</h2>");
            for(ResumeInfo r : selectedResumes) {
                output.append("<div style='border:1px solid #007BFF;padding:10px;margin:5px;'>");
                output.append("Name: ").append(r.name).append("<br>");
                output.append("Email: ").append(r.email).append("<br>");
                output.append("Phone: ").append(r.phone).append("<br>");
                output.append("Skills: ").append(String.join(", ", r.skills)).append("<br>");

                List<String> matchedSkills = SkillMatcher.getMatchingSkills(r.skills, jobDescText); // use global variable
                List<String> suggestedRoles = SkillMatcher.getSuggestedRoles(r.skills, jobDescText);
                output.append("Matching Skills: ").append(matchedSkills.isEmpty() ? "No skills matched." : String.join(", ", matchedSkills)).append("<br>");
                output.append("Suggested Roles: ").append(suggestedRoles.isEmpty() ? "Not found." : String.join(", ", suggestedRoles)).append("<br>");
                output.append("</div>");
            }

            ResumeInfo best = Collections.max(selectedResumes, Comparator.comparingDouble(r -> r.score));
            output.append("<h2>Best Candidate:</h2>");
            output.append("Name: ").append(best.name).append("<br>");
            output.append("Email: ").append(best.email).append("<br>");
            output.append("Phone: ").append(best.phone).append("<br>");
            output.append("Skills: ").append(String.join(", ", best.skills)).append("<br>");
            List<String> bestMatchedSkills = SkillMatcher.getMatchingSkills(best.skills, jobDescText);
            List<String> bestSuggestedRoles = SkillMatcher.getSuggestedRoles(best.skills, jobDescText);
            output.append("Matching Skills: ").append(bestMatchedSkills.isEmpty() ? "No skills matched." : String.join(", ", bestMatchedSkills)).append("<br>");
            output.append("Suggested Roles: ").append(bestSuggestedRoles.isEmpty() ? "Not found." : String.join(", ", bestSuggestedRoles)).append("<br>");

            return output.toString();
        });

        System.out.println("Smart Resume Analyzer running at http://localhost:4567/index.html");
    }

    private static String generateResumeSelectionForm() {
        StringBuilder output = new StringBuilder();
        output.append("<h2>Choose resumes to process:</h2>");
        output.append("<form method='post' action='/select'>");

        for(int idx=0; idx<allResumes.size(); idx++) {
            ResumeInfo r = allResumes.get(idx);
            output.append("<div class='resume'>");
            output.append((idx+1) + ". " + r.name + " (" + r.email + ")<br>");
            output.append("<input type='checkbox' name='selectCandidate' value='" + idx + "'> Select this candidate<br>");
            output.append("</div><hr>");
        }

        output.append("<button type='submit'>Submit Selected Resumes</button>");
        output.append("</form>");
        return output.toString();
    }
}

//java -cp "bin;lib/*;C:\Users\shara\Downloads\json-20210307.jar" WebApp
//http://localhost:4567
