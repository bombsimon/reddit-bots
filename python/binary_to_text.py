#!/usr/bin/env python3

import os
import praw
import re
import yaml


class BinaryToText:
    """
    BinaryToText will scrape subreddits and it's comments. If any posts
    contains more than 16 1s or 0s (doesn't need to be in order) this bot will
    convert the binary string to an ASCII string.
    """

    def __init__(self):
        credentials = self.read_credentials()

        self.c = praw.Reddit(
                client_id=credentials['client_id'],
                client_secret=credentials['client_secret'],
                password=credentials['password'],
                user_agent=credentials['useragent'],
                username=credentials['username']
                )

    def read_credentials(self, auth_file='../auth.yml'):
        """
        Read the YAML file with credentials and return a dictionary to use for
        authentication.
        """
        __location__ = os.path.realpath(
            os.path.join(os.getcwd(), os.path.dirname(__file__)))

        with open(os.path.join(__location__, auth_file), 'r') as af:
            document = yaml.load(af)

        return document

    def binary_to_ascii(self, s):
        """
        Convert a binary string to ASCII.
        """
        return "".join([chr(int(s[i:i+8], 2)) for i in range(0, len(s), 8)])

    def ascii_to_binary(self, s):
        """
        Convert an ASCII string to binary.
        """
        return "".join([format(ord(char), '#010b')[2:] for char in s])

    def run(self):
        for submission in self.c.subreddit('all').hot(limit=250):
            print('Checking ' + submission.title)

            for comment in submission.comments.list():
                ns = re.findall(r'[01]', comment.body)
                binary = "".join(ns)

                if len(binary) < 16:
                    continue

                ascii_value = self.binary_to_ascii(binary)
                comment.reply(ascii_value)

                print('replied to a comment with {}'.format(ascii_value))


if __name__ == '__main__':
    app = BinaryToText()
    app.run()
