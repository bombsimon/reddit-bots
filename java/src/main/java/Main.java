import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.yaml.snakeyaml.Yaml;

public class Main extends ListenerAdapter {
  /**
   * This is the main program that will run the bot.
   *
   * @param args Arguments from command line
   * @throws LoginException Exception thrown when failing to login
   */
  public static void main(String[] args) throws LoginException {
    Map<String,String> credentials = getCredentials("../discord.yml.ignore");

    JDA jda = new JDABuilder(credentials.get("token")).build();
    jda.addEventListener(new Main());

  }

  /**
   * This method will get every event from Discord.
   *
   * @param event Each event from Discord.
   */
  @Override
  public void onMessageReceived(MessageReceivedEvent event) {
    User author = event.getAuthor();
    Message message = event.getMessage();
    MessageChannel channel = event.getChannel();
    String msg = message.getContentDisplay();

    // Ignore bots.
    if (author.isBot()) {
      return;
    }

    // Log but don't handle private messages.
    if (event.isFromType(ChannelType.PRIVATE)) {
      System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
              event.getMessage().getContentDisplay());

      return;
    }

    System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
            event.getTextChannel().getName(), event.getMember().getEffectiveName(),
            msg);

    if (msg.equals("!ping")) {
      channel.sendMessage("pong from Java!").queue();
    }
  }

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
