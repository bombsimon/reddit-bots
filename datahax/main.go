package main

import (
	"fmt"
	"io/ioutil"
	"log"
	"math/rand"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/bwmarrin/discordgo"
	"github.com/turnage/graw/reddit"

	kingpin "gopkg.in/alecthomas/kingpin.v2"
	yaml "gopkg.in/yaml.v2"
)

// DatahaxBot is the bot doing nice things in #datahax
type DatahaxBot struct {
	DiscordToken   string `yaml:"token"`
	Discord        *discordgo.Session
	RedditSettings RedditSettings
	Reddit         reddit.Bot
}

// RedditSettings holds settings for Reddit.
type RedditSettings struct {
	ClientID     string `yaml:"client_id"`
	ClientSecret string `yaml:"client_secret"`
	Username     string `yaml:"username"`
	Password     string `yaml:"password"`
	UserAgent    string `yamn:"useragent"`
}

func main() {
	var (
		redditSettings  = kingpin.Flag("reddit-settings", "YAML file for Reddit settings").Default("../auth.yml").String()
		discordSettings = kingpin.Flag("discord-settings", "YAML file for Discord settings").Default("discord.yml").String()
	)

	kingpin.Parse()

	dhb := NewDatahaxBot(*redditSettings, *discordSettings)
	dhb.Discord.AddHandler(dhb.randomReddit)

	err := dhb.Discord.Open()
	if err != nil {
		log.Fatal("error opening connection,", err)
	}

	fmt.Println("Bot is now running.  Press CTRL-C to exit.")
	sc := make(chan os.Signal, 1)
	signal.Notify(sc, syscall.SIGINT, syscall.SIGTERM, os.Interrupt, os.Kill)
	<-sc

	dhb.Discord.Close()
}

func readYaml(file string, destination interface{}) {
	yamlFile, err := ioutil.ReadFile(file)
	if err != nil {
		log.Fatalf("Open file: %v", err)
	}

	err = yaml.Unmarshal(yamlFile, destination)
	if err != nil {
		log.Fatalf("Unmarshal: %v", err)
	}
}

// NewDatahaxBot will create a new bot instance.
func NewDatahaxBot(redditYaml, discordYaml string) *DatahaxBot {
	bot := DatahaxBot{}

	readYaml(redditYaml, &bot.RedditSettings)
	readYaml(discordYaml, &bot)

	dg, err := discordgo.New("Bot " + bot.DiscordToken)
	if err != nil {
		log.Fatal("error creating Discord session,", err)
	}

	cfg := reddit.BotConfig{
		Agent: bot.RedditSettings.UserAgent,
		App: reddit.App{
			ID:       bot.RedditSettings.ClientID,
			Secret:   bot.RedditSettings.ClientSecret,
			Username: bot.RedditSettings.Username,
			Password: bot.RedditSettings.Password,
		},
	}

	redditBot, err := reddit.NewBot(cfg)
	if err != nil {
		log.Fatal("error conneting to reddit", err)
	}

	bot.Discord = dg
	bot.Reddit = redditBot

	return &bot
}

func (dhb *DatahaxBot) randomReddit(s *discordgo.Session, m *discordgo.MessageCreate) {
	// Ignore self by convention
	if m.Author.ID == s.State.User.ID {
		return
	}

	var (
		post *reddit.Post
		err  error
	)

	switch m.Content {
	case "ples?":
		s.ChannelMessageSend(m.ChannelID, fmt.Sprintf("try one of these:\n* %s\n* %s\n* %s\n* %s", "meme", "eli5", "keyboard", "battlestation"))
		return
	case "meme ples":
		post, err = dhb.pollReddit("funny")
	case "eli5 ples":
		post, err = dhb.pollReddit("explainlikeimfive")
	case "keyboard ples":
		post, err = dhb.pollReddit("MechanicalKeyboards")
	case "battlestation ples":
		post, err = dhb.pollReddit("battlestations")
	default:
		return
	}

	if err != nil {
		s.ChannelMessageSend(m.ChannelID, fmt.Sprintf("strul med babylon: %s", err.Error()))
		return
	}

	s.ChannelMessageSend(m.ChannelID, fmt.Sprintf("%s: %s", post.Title, post.URL))
}

func (dhb *DatahaxBot) pollReddit(subreddit string) (*reddit.Post, error) {
	harvest, err := dhb.Reddit.Listing(fmt.Sprintf("/r/%s", subreddit), "")
	if err != nil {
		log.Printf("Failed to fetch /r/%s: %s", subreddit, err.Error())
		return nil, err
	}

	rand.Seed(time.Now().Unix())
	post := harvest.Posts[rand.Intn(len(harvest.Posts))]

	return post, nil
}
