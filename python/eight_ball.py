#!/usr/bin/env python3

import os
import praw
import random
import yaml


class EightBall:
    """
    EightBall will return one of the possible answers gotten from a magic
    8-ball. https://en.wikipedia.org/wiki/Magic_8-Ball.
    """

    def __init__(self):
        credentials = self.read_credentials()

        self.c = praw.Reddit(
            client_id=credentials["client_id"],
            client_secret=credentials["client_secret"],
            password=credentials["password"],
            user_agent=credentials["useragent"],
            username=credentials["username"],
        )

        self.alternatives = [
            "It is certain.",
            "It is decidedly so.",
            "Without a doubt.",
            "Yes - definitely.",
            "You may rely on it.",
            "As I see it, yes.",
            "Most likely.",
            "Outlook good.",
            "Yes.",
            "Signs point to yes.",
            "Reply hazy, try again.",
            "Ask again later.",
            "Better not tell you now.",
            "Cannot predict now.",
            "Concentrate and ask again.",
            "Don't count on it.",
            "My reply is no.",
            "My sources say no.",
            "Outlook not so good.",
            "Very doubtful.",
        ]

    def read_credentials(self, auth_file="../auth.yml"):
        """
        Read the YAML file with credentials and return a dictionary to use for
        authentication.
        """
        __location__ = os.path.realpath(
            os.path.join(os.getcwd(), os.path.dirname(__file__))
        )

        with open(os.path.join(__location__, auth_file), "r") as af:
            document = yaml.load(af)

        return document

    def random_answer(self):
        return random.choice(self.alternatives)

    def run(self):
        for submission in self.c.subreddit("all").hot(limit=250):
            print(
                "Checking {} in /r/{}".format(
                    submission.title, submission.subreddit.display_name
                )
            )

            for comment in submission.comments.list():
                if "!8ball" in comment.body:
                    reply = self.random_answer()

                    comment.reply(reply)

                    print(
                        "replied to a comment tagging !8ball with {}".format(
                            reply
                        )
                    )


if __name__ == "__main__":
    app = EightBall()
    app.run()
