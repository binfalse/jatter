/**
 * This file is part of JATTER
 * <https://binfalse.de/software/jatter/>
 * 
 * Copyright (c) 2014 Martin Scharm -- <software@binfalse.de>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.binfalse.jatter.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.xmpp.XmppMessage;
import org.jivesoftware.smack.packet.Message;

import de.binfalse.jatter.Config;
import de.binfalse.jatter.JatterTools;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.User;



/**
 * @author martin
 *
 */
public class JabberMessageProcessor
	implements Processor
{

	private Config config;
	
	/**
	 * 
	 */
	public JabberMessageProcessor (Config config)
	{
		this.config = config;
	}
	
	
	public static boolean isBotCommand (Exchange exchange)
	{
		return getSmackMessage (exchange).getBody ().startsWith ("!");
	}
	
	
	public static Message getSmackMessage (final Exchange exchange)
	{
		XmppMessage xmppMessage = (XmppMessage) exchange.getIn ();
		Message smackMessage = xmppMessage.getXmppMessage ();
		return smackMessage;
	}


	@Override
	public void process (Exchange exchange) throws Exception
	{
		Message message = getSmackMessage (exchange);
		String [] msg = message.getBody ().substring (1).split ("\\s+");
		boolean help = false;
		
		if (msg.length < 1 || msg[0].length () < 1)
		{
			exchange.getOut ().setBody ("there was no command... try !help");
			help = true;
		}
		
		String result = "";
		try
		{
			switch (msg[0])
			{
				case "retweet":
				case "rt":
					result += retweet (msg);
					break;
					
				case "favorite":
				case "favourite":
				case "fav":
					result += favorite (msg);
					break;
					
				case "delete":
				case "del":
					result += delete (msg);
					break;
					
				case "location":
					result += "location";
					break;
					
				case "follow":
					result += follow (msg);
					break;
					
				case "unfollow":
					result += unfollow (msg);
					break;
					
				case "profile":
					result += getProfile (msg);
					break;
					
				default:
					help = true;
			}
		}
		catch (TwitterException e)
		{
			result += "received an *error from twitter*: " + e.getMessage ();
		}
		
		if (help)
			result += usage ();
		
		exchange.getOut ().setBody (result);
	}
	
	private String favorite (String [] commandLine) throws TwitterException
	{
		if (commandLine.length == 2)
		{
			long id = 0;
			try
			{
				id = Long.parseLong (commandLine[1]);
			}
			catch (NumberFormatException e)
			{
				return "*"+id+"* is not a number";
			}
			Status status = JatterTools.getTwitterInstance (config).createFavorite (id);
			
			if (status != null)
				return "fav'ed *"+id+"*\n" + TwitterStatusProcessor.translateTwitterStatus (status);
			else
				return "cannot find status *"+id+"*";
		}
		return "do not understand '" + String.join (" ", commandLine) + "'.. try !help";
	}
	
	private String delete (String [] commandLine) throws TwitterException
	{
		if (commandLine.length == 2)
		{
			long id = 0;
			try
			{
				id = Long.parseLong (commandLine[1]);
			}
			catch (NumberFormatException e)
			{
				return "*"+id+"* is not a number";
			}
			Status status = JatterTools.getTwitterInstance (config).destroyStatus (id);
			
			if (status != null)
				return "deleted *"+id+"*\n" + TwitterStatusProcessor.translateTwitterStatus (status);
			else
				return "cannot find status *"+id+"*";
		}
		return "do not understand '" + String.join (" ", commandLine) + "'.. try !help";
	}
	
	private String retweet (String [] commandLine) throws TwitterException
	{
		if (commandLine.length == 2)
		{
			long id = 0;
			try
			{
				id = Long.parseLong (commandLine[1]);
			}
			catch (NumberFormatException e)
			{
				return "*"+id+"* is not a number";
			}
			Status status = JatterTools.getTwitterInstance (config).retweetStatus (id);
			
			if (status != null)
				return "retweeted *"+id+"*\n" + TwitterStatusProcessor.translateTwitterStatus (status);
			else
				return "cannot find status *"+id+"*";
		}
		return "do not understand '" + String.join (" ", commandLine) + "'.. try !help";
	}
	
	private String unfollow (String [] commandLine) throws TwitterException
	{
		if (commandLine.length == 2)
		{
			String profile = commandLine[1];
			User user = JatterTools.getTwitterInstance (config).destroyFriendship (profile);
			
			if (user != null)
				return "unfollowed *"+profile+"*\n" + translateUser (user, profile);
			else
				return "cannot find *"+profile+"*";
		}
		return "do not understand '" + String.join (" ", commandLine) + "'.. try !help";
	}
	
	private String follow (String [] commandLine) throws TwitterException
	{
		if (commandLine.length == 2)
		{
			String profile = commandLine[1];
			User user = JatterTools.getTwitterInstance (config).createFriendship (profile);
			
			if (user != null)
				return "following *"+profile+"*\n" + translateUser (user, profile);
			else
				return "cannot find *"+profile+"*";
		}
		return "do not understand '" + String.join (" ", commandLine) + "'.. try !help";
	}
	
	public static String translateUser (User user, String profile)
	{
		String ret = "profile of *"+profile+"*\n" +
					"name: " + user.getName () + "\n" +
					"screen name: " + user.getScreenName () + "\n" +
					"id: " + user.getId () + "\n";

				if (user.getDescription () != null)
					ret += "description: " + user.getDescription () + "\n";
				if (user.getURL () != null)
					ret += "url: " + JatterTools.expandUrl (user.getURL ()) + "\n";
				if (user.getLang () != null)
					ret += "language: " + user.getLang () + "\n";
				if (user.getLocation () != null)
					ret += "location: " + user.getLocation () + "\n";
				if (user.getTimeZone () != null)
					ret += "time zone: " + user.getTimeZone () + "\n";

					ret += "tweets: " + user.getStatusesCount () + "\n";
					ret += "favourites: " + user.getFavouritesCount () + "\n";
					ret += "followers: " + user.getFollowersCount () + "\n";
					ret += "friends: " + user.getFriendsCount () + "\n";

				if (user.getStatus () != null)
					ret += "last status: " + TwitterStatusProcessor.translateTwitterStatus (user.getStatus ());
				return ret;
	}
	
	private String getProfile (String [] commandLine) throws TwitterException
	{
		if (commandLine.length == 2)
		{
			String profile = commandLine[1];
			User user = JatterTools.getTwitterInstance (config).users ().showUser (profile);
			
			if (user != null)
				return translateUser (user, profile);
			else
				return "cannot find *"+profile+"*";
		}
		return "do not understand '" + String.join (" ", commandLine) + "'.. try !help";
	}
	
	public String usage ()
	{
		return "\n" +
			"*avaiable commands* (commands always start with !)\n" +
			"*retweet|rt [ID]* retweet message\n" + 
			"*favourite|favorite|fav [ID]* fav message\n" +  
			"*delete|del [ID]* delete message\n" +  
			"*follow [USER]* start following a user\n" +  
			"*unfollow [USER]* stop following a user\n" +  
			"*profile [USER]* show the profile of a user - shows your profile if no USER is supplied\n" +    
			"*location [LATITUDE,LONGITUDE]* set the location of your status updates - to unset location leave argiments empty\n" + 
			" - [ID] is the last number in a Twitter status\n" + 
			" - [USER] is the username of a profile";
	}
}
