/**
 * 
 */
package de.binfalse.jatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;



/**
 * The Class JatterTools.
 *
 * @author martin
 */
public class JatterTools
{
	
	
	/** The log. */
	private static Logger		LOG								= LoggerFactory
		.getLogger (JatterTools.class);
	
	/** The url pattern. */
	private static Pattern	urlPattern				= Pattern
		.compile ("\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)"
			+ "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov"
			+ "|mil|biz|info|mobi|name|aero|jobs|museum"
			+ "|travel|[a-z]{2}))(:[\\d]{1,5})?"
			+ "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?"
			+ "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?"
			+ "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)"
			+ "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?"
			+ "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*"
			+ "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");
	
	/** The refresh url patern. */
	private static Pattern	refreshUrlPatern	= Pattern
		.compile ("0;URL=([^\"]+)\"");
	
	
	/**
	 * Process twitter messag.
	 *
	 * @param msg
	 *          the msg
	 * @return the string
	 */
	public static String processTwitterMessag (String msg)
	{
		return expandUrls (msg);
	}
	
	
	/**
	 * Expand urls.
	 *
	 * @param msg
	 *          the msg
	 * @return the string
	 */
	public static String expandUrls (String msg)
	{
		
		HashMap<String, String> map = new HashMap<String, String> ();
		Matcher matcher = urlPattern.matcher (msg);
		while (matcher.find ())
		{
			String res = expandUrl (matcher.group ());
			if (!res.equals (matcher.group ()))
				map.put (matcher.group (), res);
		}
		
		for (String url : map.keySet ())
			msg = msg.replace (url, map.get (url));
		
		return msg;
	}
	
	/** The twitter. */
	private static Twitter twitter;
	
	
	/**
	 * Gets the twitter instance.
	 *
	 * @param conf
	 *          the conf
	 * @return the twitter instance
	 */
	public static Twitter getTwitterInstance (Config conf)
	{
		if (twitter == null)
		{
			ConfigurationBuilder confBuilder = new ConfigurationBuilder ();
			confBuilder.setOAuthConsumerKey (conf.getTwitterConsumerKey ());
			confBuilder.setOAuthConsumerSecret (conf.getTwitterConsumerSecret ());
			confBuilder.setOAuthAccessToken (conf.getTwitterAccesstoken ());
			confBuilder
				.setOAuthAccessTokenSecret (conf.getTwitterAccesstokenSecret ());
			twitter = new TwitterFactory (confBuilder.build ()).getInstance ();
		}
		return twitter;
	}
	
	
	/**
	 * Expand url.
	 *
	 * @param u
	 *          the u
	 * @return the string
	 */
	public static String expandUrl (String u)
	{
		try
		{
			URL url = new URL (u);
			URLConnection urlCon = url.openConnection ();
			urlCon.setRequestProperty ("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			urlCon.setRequestProperty ("Accept-Language", "en-US,en;q=0.5");
			urlCon.setRequestProperty ("Connection", "keep-alive");
			urlCon.setRequestProperty ("Accept-Encode", "gzip, deflate, br");
			urlCon.setRequestProperty ("User-Agent",
				"Mozilla/5.0 (X11; Linux x86_64; rv:45.0) Gecko/20100101 Firefox/45.0");
			
			HttpURLConnection httpUrlCon = HttpURLConnection.class.cast (urlCon);
			httpUrlCon.setReadTimeout (10000);
			
			// need to unshort the hard way -> parse page and unshort to
			// http-equiv="refresh" value...
			if (u.equals (httpUrlCon.getURL ().toString ())
				&& httpUrlCon.getResponseCode () < 300)
			{
				BufferedReader in = new BufferedReader (
					new InputStreamReader (httpUrlCon.getInputStream ()));
				String line = null;
				String newU = null;
				while ( (line = in.readLine ()) != null)
				{
					if (line.contains ("http-equiv=\"refresh\""))
					{
						Matcher matcher = refreshUrlPatern.matcher (line);
						if (matcher.find ())
						{
							newU = matcher.group (1);
							break;
						}
					}
				}
				in.close ();
				if (newU != null)
					return expandUrl (newU);
			}
			
			LOG.debug ("unshorting URL: " + u + " -> " + httpUrlCon.getURL ());
			return httpUrlCon.getURL ().toString ();
			
		}
		catch (IOException e)
		{
			e.printStackTrace ();
			LOG.error ("error reading " + u, e);
		}
		return u;
	}
}
