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

import java.text.SimpleDateFormat;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.binfalse.jatter.Config;
import de.binfalse.jatter.JatterTools;
import twitter4j.Status;



/**
 * The Class TwitterStatusProcessor.
 *
 * @author martin
 */
public class TwitterStatusProcessor
	implements Processor
{
	
	
	/** The log. */
	private static Logger									LOG							= LoggerFactory
		.getLogger (TwitterStatusProcessor.class);
	
	/** The Constant printDateFormat. */
	public static final SimpleDateFormat	printDateFormat	= new SimpleDateFormat (
		"yyyy-MM-dd HH:mm:ss");
	
	/** The config. */
	private Config												config;
	
	
	/**
	 * Instantiates a new twitter status processor.
	 *
	 * @param config
	 *          the config
	 */
	public TwitterStatusProcessor (Config config)
	{
		this.config = config;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process (Exchange exchange) throws Exception
	{
		exchange.getIn ().setBody (
			"\n" + translateTwitterStatus ((Status) exchange.getIn ().getBody ()));
	}
	
	
	/**
	 * Translate twitter status.
	 *
	 * @param status
	 *          the status
	 * @return the string
	 */
	public static String translateTwitterStatus (Status status)
	{
		String msg = "";
		if (status.getUser () != null && status.getUser ().getScreenName () != null)
			msg += "*" + status.getUser ().getScreenName () + "*: ";
		if (!status.isRetweet ())
			msg += JatterTools.processTwitterMessag (status.getText ());
		msg += " [" + printDateFormat.format (status.getCreatedAt ());
		if (status.getGeoLocation () != null)
			msg += " - " + status.getGeoLocation ().getLatitude () + ","
				+ status.getGeoLocation ().getLongitude ();
		msg += " - " + status.getId ();
		msg += "]";
		
		if (status.isRetweet ())
			msg += "\n *RT* > "
				+ translateTwitterStatus (status.getRetweetedStatus ());
		
		if (status.getQuotedStatus () != null)
			msg += "\n *QT* > " + translateTwitterStatus (status.getQuotedStatus ());
		
		return msg;
	}
}
