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













## Setup of Jatter

The setup of jatter is a bit too difficult for my taste, as it involves 

* creating a new Jabber account for jatter
* creating a Twitter application to use their API
* authenticating jatter with your Twitter account

However, jatter will guide you through this process. In the end you will have a jatter configuration that has the following structure:

	###############################################################################################
	#
	# GENERAL
	#
	# the following configures jatter in general
	#
	general.timeformat=yyyy-MM-dd HH:mm:ss
	###############################################################################################


	###############################################################################################
	#
	# JABBER
	#
	# the following configures the jabber-side of jatter.
	#
	jabber.server=
	jabber.port=
	jabber.user=
	jabber.password=
	jabber.contact=
	###############################################################################################


	###############################################################################################
	#
	# TWITTER
	#
	# the follwoing configures the twitter-side of jatter.
	#
	twitter.pollingintervall=60000
	twitter.consumer.key=
	twitter.consumer.secret=
	twitter.accesstoken=
	twitter.accesstoken.secret=
	twitter.location.latitude=
	twitter.location.longitude=
	###############################################################################################

The following options are available:

### Configuring jatter in general

* The `general.timeformat` configures the time format to be used in Jabber messages, for example to show you when a certain tweet was composed. You may use the [SimpleDateFormat date and time patterns](http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html)

### Configuring the Jabber side

* The `jabber.server` contains either the IP address or the domain that points to the Jabber server hosting the jatter account.
* The `jabber.port` contains the XMPP port at the `jabber.server`
* The `jabber.user` contains the username of jatter's Jabber account at `jabber.server`
* The `jabber.password` contains the password for jatter's Jabber account at `jabber.server`
* The `jabber.contact` is the person that is allowed to receive updates form Twitter and send new tweets

That is, jatter will try to authenticate to `jabber.user@jabber.server:jabber.port` using the password `jabber.password` and it will be listening and talking to `jabber.contact`.


### Configuring the Twitter side

* The `twitter.pollingintervall` tells jatter how often (in ms) it should ask Twitter for updates. Of course, the more frequent the better.. However, if your asking Twitter too often Twitter will deny the requests and block your jatter instance for a certain time. We recommend polling every 60 seconds (=60000ms).
* The `twitter.consumer.key` and `twitter.consumer.secret` authenticate your jatter instance. You need to register a new Twitter App to use your jatter bot. That's a bit annoying, but it's for security reasons etc.. And it's not difficult, just have a look at **Register a new Twitter App** below.
* The `twitter.accesstoken` and `twitter.accesstoken.secret` authenticate your Twitter account for this application, so you can read and send tweets using jatter.

If you already registered a Twitter App you can just use the App's credentials, otherwise you need to register a new application, see **Register a new Twitter App** below.





## Extend jatter

### Use your URL shortener


## Supplemental Material

The following is not really related to jatter but necessary if you want to use it.

### Register a new Twitter App

To register a new Twitter application just go to [apps.twitter.com](https://apps.twitter.com/) and click *Create New App.* Fill in all the meta data about the new application, for example name it *jatter* and describe it as a *jabber-twitter-bridge*, website actually doesn't matter I think, and read and agree to the *Developer Agreement* and finally click *Create your Twitter Application*.

Congratulations, you have a new Twitter application! :)

Now go to the application's page and click *manage keys and access tokens*. This will get you to another page with the credentials for your App. You need to copy the *Consumer Key (API Key)* and *Consumer Secret (API Secret)* to the `twitter.consumer.key` and `twitter.consumer.secret` fields of the jatter configuration, and the *Access Token* and *Access Token Secret* to the `twitter.accesstoken` and `twitter.accesstoken.secret` fields of the jatter configuration.

### Using your google or facebook account as jatter contact



