/**
 * 
 */
package de.binfalse.jatter.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;



/**
 * The Class TwitterUpdatePreprocessor.
 *
 * @author martin
 */
public class TwitterUpdatePreprocessor
	implements Processor
{
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.camel.Processor#process(org.apache.camel.Exchange)
	 */
	@Override
	public void process (Exchange exchange) throws Exception
	{
		// for example, to short urls
	}
	
}
