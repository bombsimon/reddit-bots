# Reddit Bots

This is a place where I can store concepts and ideas or even live bots used on
Reddit. It's also much appreciated with pull requests in any form or language
with some kind of new or improved feature for a Reddit bot.

## Authentication

The repository has an example (masked) YAML file which includes all required
credentials to authenticate the bot with read/write privileges. All bots in
this repository should obey this format. That is they should all read form the
YAML file to get the application credentials and never use any other name or
references besides the one in the file.

### Example

To read the credentials from the YAML file with Python

```python
__location__ = os.path.realpath(
    os.path.join(os.getcwd(), os.path.dirname(__file__)))

with open(os.path.join(__location__, auth_file), 'r') as af:
    document = yaml.load(af)

print(document['username']) # Will print the username for the credentials.
```
