import java.util.Random;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import net.dean.jraw.pagination.DefaultPaginator;
import net.dean.jraw.references.SubredditReference;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Datahax extends ListenerAdapter {
  /**
   * The Datahax class holds a Reddit client and extends the ListenerAdapter.
   */
  private RedditClient reddit;

  Datahax(RedditClient r) {
    reddit = r;
  }

  /**
   * Get a random post from the passed subreddit.
   *
   * @param subredditName The name of the subreddit to query.
   * @return A JRAW Submission
   */
  private Submission getRedditPost(String subredditName) {
    SubredditReference subreddit = this.reddit.subreddit(subredditName);
    DefaultPaginator<Submission> paginator = subreddit.posts().build();

    Random rand = new Random();
    int randomPostID = rand.nextInt(25);

    Submission post = paginator.iterator().next().get(randomPostID);

    return post;
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

    switch (msg) {
      case "java meme ples":
        Submission post = getRedditPost("funny");
        String response = String.format("%s: %s", post.getTitle(), post.getUrl());

        channel.sendMessage(response).queue();
        break;
      default:
        // Not yet implemented.
    }
  }
}