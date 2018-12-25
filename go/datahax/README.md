# Datahax

This is a small example of a Discord bot with a Reddit client which allows
users to ask for random content. The following commands are supproted:

* `meme ples` - Will post a random article from [/r/funny](https://reddit.com/r/funny)
* `eli5 ples` - Will post a random article from [/r/explainlikeimfive](https://reddit.com/r/explainlikeimfive)
* `keyboard ples` - Will post a random article from [/r/MechanicalKeyboards](https://reddit.com/r/MechanicalKeyboards)
* `battlestation ples` - Will post a random article from [/r/battlestations](https://reddit.com/r/battlestations)

## Setup

The bot takes two arguments as input; one with the Reddit credentials like the
one in the root directory and one with the Discord credentials (only token is
needed).

```sh
go run main.go --reddit-settings ../auth.yml --discord-settings discord.yml
```
