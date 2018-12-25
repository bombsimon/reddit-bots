#!/usr/bin/env ruby

require 'redd'
require 'yaml'

config = YAML.load_file('../auth.yml')

session = Redd.it(
  user_agent: config['useragent'],
  client_id: config['client_id'],
  secret: config['client_secret'],
  username: config['username'],
  password: config['password']
)

session.subreddit('testabot').comments.each do |comment|
  if comment.body.include?('roll a dice')
    comment.reply("It's a #{rand(1..6)}!")
  elsif comment.body.include?('flip a coin')
    comment.reply("It's a #{%w[heads tails].sample}!")
  end
end
