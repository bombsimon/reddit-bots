import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import javax.security.auth.login.LoginException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.yaml.snakeyaml.Yaml;

public class Main {
  /**
   * This is the main program that will run the bot.
   *
   * @param args Arguments from command line
   * @throws LoginException Exception thrown when failing to login
   */
  public static void main(String[] args) throws LoginException {
    // Load discord and reddit auth configuration.
    Map<String,String> discordCredentials = getCredentials("../discord.yml.ignore");
    Map<String,String> redditCredentials = getCredentials("../auth.yml.ignore");

    // Create OAuth2 credentials for reddit.
    Credentials oauthCreds = Credentials.script(
            redditCredentials.get("username"),
            redditCredentials.get("password"),
            redditCredentials.get("client_id"),
            redditCredentials.get("client_secret")
    );

    // Create a custom User-Agent.
    UserAgent userAgent = new UserAgent(
            "bot",
            "datahax",
            "0.0.1",
            redditCredentials.get("username")
    );

    // Create a Reddit client.
    RedditClient reddit = OAuthHelper.automatic(new OkHttpNetworkAdapter(userAgent), oauthCreds);

    // Create a discord client and register the Datahax class as event listener.
    JDA jda = new JDABuilder(discordCredentials.get("token")).build();
    jda.addEventListener(new Datahax(reddit));
  }

  /**
   * Get configuration from yaml files and return  a map of strings.
   * @param filePath The path of the file
   * @return A map of strings from the yaml file.
   */
  private static Map<String, String> getCredentials(String filePath) {
    Yaml yaml = new Yaml();
    InputStream inputStream;
    Map<String, String> credentials = null;

    try {
      inputStream = new FileInputStream(new File(filePath));
    }
    catch (java.io.FileNotFoundException e) {
      System.out.println("Could not open file");

      return credentials;
    }

    credentials = yaml.load(inputStream);

    return credentials;
  }
}