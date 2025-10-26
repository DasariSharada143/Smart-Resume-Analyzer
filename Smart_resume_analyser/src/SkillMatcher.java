import java.util.*;

public class SkillMatcher {

    static class Role {
        String roleName;
        Set<String> roleSkills;

        Role(String roleName, String skills) {
            this.roleName = roleName;
            this.roleSkills = new HashSet<>();
            for (String skill : skills.split(",")) this.roleSkills.add(skill.trim().toLowerCase());
        }
    }

    private static List<Role> jobRoles = Arrays.asList(
        new Role("Software Developer", "Java, Python, SQL, HTML, CSS, JavaScript, C++"),
        new Role("Backend Developer", "Java, SQL, Python"),
        new Role("Data Analyst", "Python, SQL, Excel, Tableau, PowerBI"),
        new Role("Web Developer", "HTML, CSS, JavaScript, React, Node.js"),
        new Role("DevOps Engineer", "Linux, AWS, Docker, Jenkins, CI/CD"),
        new Role("Machine Learning Engineer", "Python, TensorFlow, Keras, Data Analysis, SQL")
    );

    public static void calculateScore(ResumeInfo resume, String jobDescText) {
        List<String> matched = getMatchingSkills(resume.skills, jobDescText);
        resume.score = matched.size();
    }

    public static List<String> getMatchingSkills(List<String> resumeSkills, String jobDescText) {
        Set<String> matched = new HashSet<>();
        Set<String> resumeSet = new HashSet<>();
        for (String s : resumeSkills) resumeSet.add(s.toLowerCase());

        for (Role role : jobRoles)
            for (String skill : role.roleSkills)
                if (resumeSet.contains(skill)) matched.add(skill);

        List<String> result = new ArrayList<>(matched);
        Collections.sort(result);
        return result;
    }

    public static List<String> getSuggestedRoles(List<String> resumeSkills, String jobDescText) {
        List<String> suggestedRoles = new ArrayList<>();
        Set<String> resumeSet = new HashSet<>();
        for (String s : resumeSkills) resumeSet.add(s.toLowerCase());

        for (Role role : jobRoles) {
            for (String skill : role.roleSkills) {
                if (resumeSet.contains(skill)) {
                    suggestedRoles.add(role.roleName);
                    break;
                }
            }
        }
        return suggestedRoles;
    }
}
