package de.binfalse.jatter;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import org.jivesoftware.smackx.jiveproperties.JivePropertiesManager;

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
		startJatter (args[0]);
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
		Config config = new Config ();
		config.readConfig (configFile);
		
		final String chatEndpoint = String.format (
			"xmpp://%s@%s:5222/%s?password=%s", config.getJabberUser (),
			config.getJabberServer (), config.getJabberContact (),
			config.getJabberPassword ());
		
		final String twitterHomeEndpoint = String.format (
			"twitter://timeline/home?type=polling&delay=%s&consumerKey=%s&consumerSecret=%s&accessToken=%s&accessTokenSecret=%s",
			config.getTwitterPollingintervall (), config.getTwitterConsumerKey (),
			config.getTwitterConsumerSecret (), config.getTwitterAccesstoken (),
			config.getTwitterAccesstokenSecret ());
		
		final String twitterMentionsEndpoint = String.format (
			"twitter://timeline/mentions?type=polling&delay=%s&consumerKey=%s&consumerSecret=%s&accessToken=%s&accessTokenSecret=%s",
			config.getTwitterPollingintervall (), config.getTwitterConsumerKey (),
			config.getTwitterConsumerSecret (), config.getTwitterAccesstoken (),
			config.getTwitterAccesstokenSecret ());
		
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
		main.addRouteBuilder (new RouteBuilder ()
		{
			
			
			@Override
			public void configure () throws IOException
			{
				
				from (twitterHomeEndpoint).process (twitterStatusProc)
					.to (chatEndpoint);
				
				from (twitterMentionsEndpoint).process (twitterStatusProc)
					.process (new Processor ()
					{
						
						
						public void process (Exchange exchange) throws Exception
						{
							String payload = exchange.getIn ().getBody (String.class);
							exchange.getIn ().setBody ("*MENTIONED*: " + payload);
						}
					}).to (chatEndpoint);
					
				from (chatEndpoint).choice ()
					.when (exchange -> JabberMessageProcessor.isBotCommand (exchange))
					.process (jabberMessageProc).to (chatEndpoint).otherwise ()
					.process (tupre).doTry ().to (twitterUpdater)
					.setBody (constant ("timeline updated")).to (chatEndpoint)
					.doCatch (TwitterException.class).process (new Processor ()
					{
						
						
						public void process (Exchange exchange) throws Exception
						{
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
