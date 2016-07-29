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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;

import de.binfalse.bflog.LOGGER;
import de.binfalse.jatter.processors.JabberMessageProcessor;
import de.binfalse.jatter.processors.TwitterStatusProcessor;
import de.binfalse.jatter.processors.TwitterUpdatePreprocessor;
import twitter4j.TwitterException;



/**
 * The main file of the JATTER application
 *
 * @author Martin Scharm
 *
 */
public class App
{
	
	
	public static final SimpleDateFormat printDateFormat = new SimpleDateFormat (
		"yyyy-MM-dd HH:mm:ss");
	
	
	public static void help (Options options, String msg)
	{
		System.out.println (msg);
		System.out.println ();
		new HelpFormatter ().printHelp ("java -jar jatter.jar -c CONFIG", options);
		System.exit (1);
	}
	
	
	/**
	 * Run jatter's main.
	 *
	 * @param args
	 *          the arguments
	 * @throws Exception
	 *           the exception
	 */
	public static void main (String[] args) throws Exception
	{
		Options options = new Options ();
		
		Option conf = new Option ("c", "config", true, "config file path");
		conf.setRequired (false);
		options.addOption (conf);
		
		Option t = new Option ("t", "template", false, "show a config template");
		t.setRequired (false);
		options.addOption (t);
		
		Option v = new Option ("v", "verbose", false, "print information messages");
		v.setRequired (false);
		options.addOption (v);
		
		Option d = new Option ("d", "debug", false,
			"print debugging messages incl stack traces");
		d.setRequired (false);
		options.addOption (d);
		
		Option h = new Option ("h", "help", false, "show help");
		h.setRequired (false);
		options.addOption (h);
		
		CommandLineParser parser = new DefaultParser ();
		CommandLine cmd;
		
		try
		{
			cmd = parser.parse (options, args);
			if (cmd.hasOption ("h"))
				throw new RuntimeException ("showing the help page");
		}
		catch (Exception e)
		{
			help (options, e.getMessage ());
			return;
		}
		
		if (cmd.hasOption ("t"))
		{
			System.out.println ();
			BufferedReader br = new BufferedReader (new InputStreamReader (App.class
				.getClassLoader ().getResourceAsStream ("config.properties.template")));
			while (br.ready ())
				System.out.println (br.readLine ());
			br.close ();
			System.exit (0);
		}
		
		if (cmd.hasOption ("v"))
			LOGGER.setMinLevel (LOGGER.INFO);
		
		if (cmd.hasOption ("d"))
		{
			LOGGER.setMinLevel (LOGGER.DEBUG);
			LOGGER.setLogStackTrace (true);
		}
		
		if (!cmd.hasOption ("c"))
			help (options, "a config file is required for running jatter");
		
		startJatter (cmd.getOptionValue ("c"));
	}
	
	
	/**
	 * Start a new jatter instance.
	 *
	 * @param configFile
	 *          the config file
	 * @throws Exception
	 *           the exception
	 */
	public static void startJatter (String configFile) throws Exception
	{
		LOGGER.info ("starting jatter");
		Config config = new Config ();
		config.readConfig (configFile);
		
		LOGGER.debug ("creating a chat endpoint at");
		final String chatEndpoint = String.format (
			"xmpp://%s@%s:5222/%s?password=%s", config.getJabberUser (),
			config.getJabberServer (), config.getJabberContact (),
			config.getJabberPassword ());
		LOGGER.debug (chatEndpoint.replaceAll ("password=.*$", "password=XXX"));
		
		LOGGER.debug ("creating a twitter-home endpoint");
		final String twitterHomeEndpoint = String.format (
			"twitter://timeline/home?type=polling&delay=%s&consumerKey=%s&consumerSecret=%s&accessToken=%s&accessTokenSecret=%s",
			config.getTwitterPollingintervall (), config.getTwitterConsumerKey (),
			config.getTwitterConsumerSecret (), config.getTwitterAccesstoken (),
			config.getTwitterAccesstokenSecret ());
		
		LOGGER.debug ("creating a twitter-mentions endpoint");
		final String twitterMentionsEndpoint = String.format (
			"twitter://timeline/mentions?type=polling&delay=%s&consumerKey=%s&consumerSecret=%s&accessToken=%s&accessTokenSecret=%s",
			config.getTwitterPollingintervall (), config.getTwitterConsumerKey (),
			config.getTwitterConsumerSecret (), config.getTwitterAccesstoken (),
			config.getTwitterAccesstokenSecret ());
		
		LOGGER.debug ("creating a twitter-updater endpoint");
		final String twitterUpdater = String.format (
			"twitter://timeline/user?consumerKey=%s&consumerSecret=%s&accessToken=%s&accessTokenSecret=%s",
			config.getTwitterConsumerKey (), config.getTwitterConsumerSecret (),
			config.getTwitterAccesstoken (), config.getTwitterAccesstokenSecret ());
		
		JivePropertiesManager.setJavaObjectEnabled (true);
		
		final TwitterStatusProcessor twitterStatusProc = new TwitterStatusProcessor (
			config);
		final JabberMessageProcessor jabberMessageProc = new JabberMessageProcessor (
			config);
		
		final TwitterUpdatePreprocessor tupre = new TwitterUpdatePreprocessor ();
		
		Main main = new Main ();
		LOGGER.info ("creating routes");
		main.addRouteBuilder (new RouteBuilder ()
		{
			
			
			@Override
			public void configure () throws IOException
			{
				
				LOGGER.debug ("twitter-home -> status processor -> chat");
				from (twitterHomeEndpoint).process (twitterStatusProc)
					.to (chatEndpoint);
				
				LOGGER.debug ("twitter-mentions -> status processor -> chat");
				from (twitterMentionsEndpoint).process (twitterStatusProc)
					.process (new Processor ()
					{
						
						
						public void process (Exchange exchange) throws Exception
						{
							String payload = exchange.getIn ().getBody (String.class);
							exchange.getIn ().setBody ("*MENTIONED*: " + payload);
						}
					}).to (chatEndpoint);
					
				LOGGER.debug ("chat -> decider -> twitter|bot");
				from (chatEndpoint).choice ()
					.when (exchange -> JabberMessageProcessor.isBotCommand (exchange))
					.process (jabberMessageProc).to (chatEndpoint).otherwise ()
					.process (tupre).doTry ().to (twitterUpdater)
					.setBody (constant ("timeline updated")).to (chatEndpoint)
					.doCatch (TwitterException.class).process (new Processor ()
					{
						
						
						public void process (Exchange exchange) throws Exception
						{
							LOGGER.debug ("chat message failed to send to twitter");
							String payload = exchange.getIn ().getBody (String.class);
							exchange.getIn ().setBody ("*failed*: " + payload + " (length: "
								+ payload.length () + ")");
						}
					}).to (chatEndpoint).end ().endChoice ();
			}
		});
		main.run ();
	}
}
