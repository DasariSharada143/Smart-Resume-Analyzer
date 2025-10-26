import java.io.File;
import java.util.*;

public class App {
    public static void main(String[] args) {
        System.out.println("Smart Resume Analyzer (CLI) started...\n");

        String resumeFolder = "resumes";
        String jobDescriptionPath = "resumes/job_description.txt";

        File folder = new File(resumeFolder);
        File[] resumeFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (resumeFiles != null && resumeFiles.length > 0) {
            String jobDescText = ResumeExtractor.readTextFile(jobDescriptionPath);
            List<ResumeInfo> allResumes = new ArrayList<>();

            for (File resume : resumeFiles) {
                String text = ResumeExtractor.extractText(resume.getAbsolutePath());
                ResumeInfo info = ResumeExtractor.parseResume(text);
                SkillMatcher.calculateScore(info, jobDescText);
                allResumes.add(info);
            }

            if (allResumes.isEmpty()) {
                System.out.println("No resumes parsed correctly.");
                return;
            }

            ResumeInfo best = Collections.max(allResumes, Comparator.comparingDouble(r -> r.score));
            System.out.println("Best Candidate:");
            System.out.println("Name: " + best.name);
            System.out.println("Email: " + best.email);
            System.out.println("Phone: " + best.phone);
            System.out.println("Skills: " + String.join(", ", best.skills));
            System.out.println("Education: " + best.education);

            List<String> matchedSkills = SkillMatcher.getMatchingSkills(best.skills, jobDescText);
            List<String> suggestedRoles = SkillMatcher.getSuggestedRoles(best.skills, jobDescText);

            System.out.println("\nMatching Skills: " +
                    (matchedSkills.isEmpty() ? "No skills matched." : String.join(", ", matchedSkills)));
            System.out.println("Suggested Roles: " +
                    (suggestedRoles.isEmpty() ? "Not found." : String.join(", ", suggestedRoles)));

            if (!best.skills.isEmpty()) JobFetcher.fetchJobs(best.skills.get(0));

        } else {
            System.out.println("No resumes found in " + resumeFolder);
        }
    }
}
