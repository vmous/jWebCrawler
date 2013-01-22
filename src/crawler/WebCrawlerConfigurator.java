package crawler;

import java.util.Properties;

import toolbox.config.AbstractConfigurator;

/**
 * A concrete class extending the {@link AbstractConfigurator} abstract class
 * following the Singleton design pattern to ensure exactly one object is
 * realized.
 *
 * @author billy
 *
 */
public class WebCrawlerConfigurator extends AbstractConfigurator {

	/**
	 * The, single, instance of the class. It is used to implement the
	 * <em>Singleton</em> design pattern and is instantiated outside the
	 * {@link WebCrawlerConfigurator#getInstance()} method to ensure thread
	 * safety without the additional synchronization cost.
	 *
	 * @see AbstractConfigurator#getInstance()
	 * @see AbstractConfigurator#ConfigurationManager()
	 * */
	private static WebCrawlerConfigurator instance = new WebCrawlerConfigurator();

	/**
	 * Made private in order to implement the Singleton design pattern.
	 *
	 * @see WebCrawlerConfigurator#instance
	 * @see WebCrawlerConfigurator#getInstance()
	 * */
	private WebCrawlerConfigurator() {
		super();
        this.setUserConfFilePath("webcrawler.user.conf");
        this.setUserConfFileHeader("Web Crawler Configuration File");

        // instantiate the default properties...
        this.defaultProperties = new Properties();

        // ...and load them
        this.loadDefaultProperties();

        /*
         * instantiate the user properties with the default ones as fall-back
         * values...
         */
        this.userProperties = new Properties(this.defaultProperties);

        // ... and then try to load the user defined properties
        this.loadUserProperties();
	}

	/**
	 * The method that makes the Singleton class available to the outside world.
	 *
	 * @return the Singleton class of type {@code SingletonConfigurator}.
	 *
	 * @see WebCrawlerConfigurator#instance
	 * @see WebCrawlerConfigurator#SingletonConfigurator()
	 */
	public static synchronized WebCrawlerConfigurator getInstance() {
		return instance;
	}
}
