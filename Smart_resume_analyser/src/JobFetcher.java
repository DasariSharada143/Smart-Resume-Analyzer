import java.io.*;
import java.net.*;
import org.json.*;

public class JobFetcher {
    private static final String API_URL = "https://jsearch.p.rapidapi.com/search";
    private static final String API_KEY = "YOUR_RAPIDAPI_KEY"; // Replace this with your key

    public static void fetchJobs(String skill) {
        try {
            String query = URLEncoder.encode(skill, "UTF-8");
            URL url = new URL(API_URL + "?query=" + query + "&num_pages=1");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("x-rapidapi-host", "jsearch.p.rapidapi.com");
            conn.setRequestProperty("x-rapidapi-key", API_KEY);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray jobs = jsonResponse.getJSONArray("data");

            System.out.println("\n🔎 Top Job Matches for: " + skill);
            for (int i = 0; i < Math.min(5, jobs.length()); i++) {
                JSONObject job = jobs.getJSONObject(i);
                System.out.println("• " + job.getString("job_title") + " — " + job.getString("employer_name"));
                System.out.println("  🌍 " + job.getString("job_country"));
                System.out.println("  🔗 " + job.getString("job_apply_link"));
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
