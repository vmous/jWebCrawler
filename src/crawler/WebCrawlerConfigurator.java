/**
 *
 */
package crawler;

// Dependecy on another project of mine.
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

	/* (non-Javadoc)
	 * @see AbstractConfigurator#loadDefaults()
	 */
	@Override
	protected void loadDefaults() {
	    System.out.println("AAAAAAAAAAGFFFFFFFFFFFFFFF");
		// Clear the default properties...
		getDefaultProperties().clear();

		// ... and then set each one
	    getDefaultProperties().setProperty("redirect", "false");
	    getDefaultProperties().setProperty("timeout", "5");
	    getDefaultProperties().setProperty("quiet", "false");
	    getDefaultProperties().setProperty("verbose", "true");
	    getDefaultProperties().setProperty("recursive", "false");
	    getDefaultProperties().setProperty("imglinks", "true");
	    getDefaultProperties().setProperty("max_files", "2000");
	    getDefaultProperties().setProperty("local_path", "/home/billy/Desktop/");
	    getDefaultProperties().setProperty("agent", "JCrawler/00001");
	    getDefaultProperties().setProperty("depth", "0");
	    getDefaultProperties().setProperty("accept", "text/html");
	    getDefaultProperties().setProperty("reject", "");
	    getDefaultProperties().setProperty("threads", "5");
	}

}
