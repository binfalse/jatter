# jatter

This is *jatter,* a Jabber-Twitter-bridge. That is, you can interact with Twitter through your Jabber/XMPP chats.

Jatter is basically a bot. It uses the Twitter API to get updates of your timeline and sends them to your Jabber account. On the other side it listens to chat messages of your Jabber account and sends them as a status update to Twitter. Thus, all you need is another chat window/tab in your Jabber client -- no more need for another Twitter client or the annoying Twitter website..

Depending on your Jabber client and how you configured jatter your Twitter timeline may look like this:



## Interacting with the jatter Bot

### Jatter commands

The jatter bot understands some basic commands, so you can make him do certain things. Every command starts with an exclamation mark (`!`). That means, you cannot update your twitter status with a message that starts with `!`. You shouldn't use too many exclamation, either...

Commands usually also take arguments, you can provide them seperated with spaces. For example, to follow me on twitter you cound send the following command to your bot:

    !follow binfalse

The current implementation of the jatter bot recognises 7 different commands. There is of course also a `!help` command:

* `retweet|rt [ID]` retweet the message with the id `ID`
* `favourite|favorite|fav [ID]` favorite message with the id `ID`
* `delete|del [ID]` delete message with the id `ID`
* `follow [USER]` start following the user `@USER`
* `unfollow [USER]` stop following the user `@USER`
* `profile [USER]` show the profile of the user `@USER` - shows your own profile if no `USER` is supplied
* `location [LATITUDE,LONGITUDE]` set the location of your status updates - to unset location leave arguments empty

The pipe (`|`) separated synonyms. That means, `favourite 123456` has the same effect as `fav 123456`, just take the one that you can remember best.

### Updating your Twitter status

That's super easy. Just send a message to your jatter bot that does not start with an exclamation mark (!). It will directly be sent to Twitter and your friends will see an updated status.

If you tried sending a mesage longer that the allowed 140 characters, you'll receive an error such as:

![jatter shows error if message too long](https://binfalse.de/assets/media/pics/2016/jatter/jatter-msg-too-long.png)

Here I tried to send

> i like jatter. it is one of the coolest tools. it makes the twitter thing super easy, especially for people who do not like twitter's browser interface...

But as you see, jatter failed to send the message because it's 154 characters and therefore twitter didn't accept the message. Please note, that jatter actually tried to send the message and received an error from twitter. Jatter itself doesn't do any checks. That means, it Twitter decides to allow for 200 chars or only 100 from tomorrow jatter will be perfectly fine with it!


## The Jabber side

jatter hooks into an XMPP server so listen to a contact. It behaves like a bot and waits for 



you could even use your google account for reading twitter

extend to use your own url shortener
