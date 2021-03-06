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
package de.binfalse.jatter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import de.binfalse.bflog.LOGGER;



/**
 * The Class Config for storing and generating configuration files.
 *
 * @author martin
 */
public class Config
{
	
	
	/** The prop. */
	private Properties		prop;
	
	/** The general configuration for displaying time. */
	private static String	GENERAL_TIMEFORMAT								= "general.timeformat";
	
	/** The jabber server. */
	private static String	JABBER_SERVER								= "jabber.server";
	
	/** The jabber port. */
	private static String	JABBER_PORT									= "jabber.port";
	
	/** The jabber user. */
	private static String	JABBER_USER									= "jabber.user";
	
	/** The jabber password. */
	private static String	JABBER_PASSWORD							= "jabber.password";
	
	/** The jabber contact. */
	private static String	JABBER_CONTACT							= "jabber.contact";
	
	/** The twitter pollingintervall. */
	private static String	TWITTER_POLLINGINTERVALL		= "twitter.pollingintervall";
	
	/** The twitter consumer key. */
	private static String	TWITTER_CONSUMER_KEY				= "twitter.consumer.key";
	
	/** The twitter consumer secret. */
	private static String	TWITTER_CONSUMER_SECRET			= "twitter.consumer.secret";
	
	/** The twitter accesstoken. */
	private static String	TWITTER_ACCESSTOKEN					= "twitter.accesstoken";
	
	/** The twitter accesstoken secret. */
	private static String	TWITTER_ACCESSTOKEN_SECRET	= "twitter.accesstoken.secret";
	
	/** The twitter location latitude. */
	private static String	TWITTER_LOCATION_LATITUDE		= "twitter.location.latitude";
	
	/** The twitter location longitude. */
	private static String	TWITTER_LOCATION_LONGITUDE	= "twitter.location.longitude";
	
	
	/**
	 * Instantiates a new config.
	 */
	public Config ()
	{
		prop = new Properties ();
	}
	
	
	/**
	 * Read config.
	 *
	 * @param file
	 *          the file
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void readConfig (String file) throws IOException
	{
		LOGGER.info("reading configuration at ", file);
		try (InputStream in = new FileInputStream (file))
		{
			prop.clear ();
			prop.load (in);
		}
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug ("found ", prop.size(), " configuration options:");
			for (Object s : prop.keySet())
				LOGGER.debug(" > ", s , " -> ", prop.get(s).toString().length(), " chars");
		}
	}
	
	
	/**
	 * Write config.
	 *
	 * @param file
	 *          the file
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public void writeConfig (String file) throws IOException
	{
		LOGGER.info("writing configuration to ", file);
		try (OutputStream out = new FileOutputStream (file))
		{
			prop.store (out, null);
		}
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug ("wrote ", prop.size(), " configuration options:");
			for (Object s : prop.keySet())
				LOGGER.debug(" > ", s , " -> ", prop.get(s).toString().length(), " chars");
		}
	}
	
	
	/**
	 * Sets the jabber server.
	 *
	 * @param server
	 *          the new jabber server
	 */
	public void setJabberServer (String server)
	{
		prop.setProperty (JABBER_SERVER, server);
	}
	
	
	/**
	 * Sets the jabber user.
	 *
	 * @param value
	 *          the new jabber user
	 */
	public void setJabberUser (String value)
	{
		prop.setProperty (JABBER_USER, value);
	}
	
	
	/**
	 * Sets the jabber password.
	 *
	 * @param value
	 *          the new jabber password
	 */
	public void setJabberPassword (String value)
	{
		prop.setProperty (JABBER_PASSWORD, value);
	}
	
	
	/**
	 * Sets the jabber contact.
	 *
	 * @param value
	 *          the new jabber contact
	 */
	public void setJabberContact (String value)
	{
		prop.setProperty (JABBER_CONTACT, value);
	}
	
	
	/**
	 * Sets the jabber port.
	 *
	 * @param value
	 *          the new jabber port
	 */
	public void setJabberPort (String value)
	{
		prop.setProperty (JABBER_PORT, value);
	}
	
	
	/**
	 * Sets the twitter pollingintervall.
	 *
	 * @param value
	 *          the new twitter pollingintervall
	 */
	public void setTwitterPollingintervall (String value)
	{
		prop.setProperty (TWITTER_POLLINGINTERVALL, value);
	}
	
	
	/**
	 * Sets the twitter consumer key.
	 *
	 * @param value
	 *          the new twitter consumer key
	 */
	public void setTwitterConsumerKey (String value)
	{
		prop.setProperty (TWITTER_CONSUMER_KEY, value);
	}
	
	
	/**
	 * Sets the twitter consumer secret.
	 *
	 * @param value
	 *          the new twitter consumer secret
	 */
	public void setTwitterConsumerSecret (String value)
	{
		prop.setProperty (TWITTER_CONSUMER_SECRET, value);
	}
	
	
	/**
	 * Sets the twitter accesstoken.
	 *
	 * @param value
	 *          the new twitter accesstoken
	 */
	public void setTwitterAccesstoken (String value)
	{
		prop.setProperty (TWITTER_ACCESSTOKEN, value);
	}
	
	
	/**
	 * Sets the twitter accesstoken secret.
	 *
	 * @param value
	 *          the new twitter accesstoken secret
	 */
	public void setTwitterAccesstokenSecret (String value)
	{
		prop.setProperty (TWITTER_ACCESSTOKEN_SECRET, value);
	}
	
	
	/**
	 * Sets the twitter location latitude.
	 *
	 * @param value
	 *          the new twitter location latitude
	 */
	public void setTwitterLocationLatitude (String value)
	{
		prop.setProperty (TWITTER_LOCATION_LATITUDE, value);
	}
	
	
	/**
	 * Sets the twitter location longitude.
	 *
	 * @param value
	 *          the new twitter location longitude
	 */
	public void setTwitterLocationLongitude (String value)
	{
		prop.setProperty (TWITTER_LOCATION_LONGITUDE, value);
	}
	
	
	/**
	 * Sets the time format.
	 *
	 * @param value
	 *          the new time format
	 */
	public void setTimeFormat (String value)
	{
		prop.setProperty (GENERAL_TIMEFORMAT, value);
	}
	
	
	/**
	 * Gets the time format.
	 *
	 * @return the time format
	 */
	public String getTimeFormat ()
	{
		return prop.getProperty (GENERAL_TIMEFORMAT);
	}
	
	
	/**
	 * Gets the jabber server.
	 *
	 * @return the jabber server
	 */
	public String getJabberServer ()
	{
		return prop.getProperty (JABBER_SERVER);
	}
	
	
	/**
	 * Gets the jabber user.
	 *
	 * @return the jabber user
	 */
	public String getJabberUser ()
	{
		return prop.getProperty (JABBER_USER);
	}
	
	
	/**
	 * Gets the jabber password.
	 *
	 * @return the jabber password
	 */
	public String getJabberPassword ()
	{
		return prop.getProperty (JABBER_PASSWORD);
	}
	
	
	/**
	 * Gets the jabber contact.
	 *
	 * @return the jabber contact
	 */
	public String getJabberContact ()
	{
		return prop.getProperty (JABBER_CONTACT);
	}
	
	
	/**
	 * Gets the jabber port.
	 *
	 * @return the jabber port
	 */
	public String getJabberPort ()
	{
		return prop.getProperty (JABBER_PORT);
	}
	
	
	/**
	 * Gets the twitter pollingintervall.
	 *
	 * @return the twitter pollingintervall
	 */
	public String getTwitterPollingintervall ()
	{
		return prop.getProperty (TWITTER_POLLINGINTERVALL);
	}
	
	
	/**
	 * Gets the twitter consumer key.
	 *
	 * @return the twitter consumer key
	 */
	public String getTwitterConsumerKey ()
	{
		return prop.getProperty (TWITTER_CONSUMER_KEY);
	}
	
	
	/**
	 * Gets the twitter consumer secret.
	 *
	 * @return the twitter consumer secret
	 */
	public String getTwitterConsumerSecret ()
	{
		return prop.getProperty (TWITTER_CONSUMER_SECRET);
	}
	
	
	/**
	 * Gets the twitter accesstoken.
	 *
	 * @return the twitter accesstoken
	 */
	public String getTwitterAccesstoken ()
	{
		return prop.getProperty (TWITTER_ACCESSTOKEN);
	}
	
	
	/**
	 * Gets the twitter accesstoken secret.
	 *
	 * @return the twitter accesstoken secret
	 */
	public String getTwitterAccesstokenSecret ()
	{
		return prop.getProperty (TWITTER_ACCESSTOKEN_SECRET);
	}
	
	
	/**
	 * Gets the twitter location latitude.
	 *
	 * @return the twitter location latitude
	 */
	public String getTwitterLocationLatitude ()
	{
		return prop.getProperty (TWITTER_LOCATION_LATITUDE);
	}
	
	
	/**
	 * Gets the twitter location longitude.
	 *
	 * @return the twitter location longitude
	 */
	public String getTwitterLocationLongitude ()
	{
		return prop.getProperty (TWITTER_LOCATION_LONGITUDE);
	}
	
}
