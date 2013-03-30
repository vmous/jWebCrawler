package crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import toolbox.web.sitemap.SAXSitemapParser;
import toolbox.web.sitemap.WebPage;


/**
 * A multi-threaded, database assisted, web crawler.
 *
 * @author billy
 */
public class WebCrawler {

    /**
     * The version of the crawler.
     */
    private static final String VERSION =
            "0.7";

    /**
     * The author of the crawler.
     */
    private static final String strAppAuthor =
            "Vassilis S. Moustakas (vsmoustakas[at]gmail[dot]com)";

    /**
     * The application name.
     */
    private static final String strAppName =
            "Web Crawler v." + VERSION;

    /**
     * The basic application usage.
     */
    private static final String strAppUsage =
            strAppName + "\n" +
            "Usage: java WebCrawler [OPTIONS] <url>\n" +
            "Use -h for more help";

    /**
     * The application header.
     */
    private static final String strAppHeader =
            strAppName + "\n" +
            "\n" +
            "This program is distributed in the hope that it will be useful,\n" +
            "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
            "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the\n" +
            "GNU General Public License for more details.\n" +
            "\n" +
            "Authored by " + strAppAuthor + ".";

    /**
     * The application help.
     */
    private static final String strAppHelp =
            strAppName + "\n" +
            "A simple multi-threaded, database assisted, network content retriever.\n" +
            "Author: " + strAppAuthor + "\n" +
            "Usage: java WebCrawler [OPTIONS] <url>\n" +
            "\n" +
            "OPTIONS\n" +
            "\n" +
            "-a<NAME>\n\tSet the NAME with which the crawler will introduce itself to web servers. Can alternatively be handled by setting the \"agent\" configuration property.\n" +
//            "-A: Comma-separated list of accepted mimes.\n" +
            "-d<LEVELS>\n\tSpecify maximum LEVELS number of recursion depth. Can alternatively be handled by setting the \"depth\" configuration property.\n" +
            "-h\n\tPrint this help.\n" +
            "-H\n\tPrint the application's header information.\n" +
            "-m<NUMBER>\n\tSpecify NUMBER of maximum threads in the pool. Can alternatively be handled by setting the \"threadNumber\" configuration property.\n" +
            "-n<NUMBER>\n\tBound to NUMBER maximum files downloaded. Can alternatively be handled by setting the \"maximumFileNumber\" configuration property.\n" +
            "-o<PATHTOFILE>\n\tLog messages to the file denoted by PATHTOFILE. If no -o and/or PATHTOFILE is defined then logging will be directed to standard out. Can alternatively be handled by setting the \"logFilePath\" configuration property.\n" +
            "-p<PATH>\n\tSave retrieved files under PATH directory. Can alternatively be handled by setting the \"storagePath\" configuration property.\n" +
//            "-R\n\tComma-separated list of rejected mimes.\n" +
            "-s<URL>\n\tCrawl the pages dictated by the sitemap on this URL Can alternatively be handled by setting the \"sitemapURL\" configuration property.\n" +
            "-t<SECONDS>\n\tSet SECONDS for HTTP connection time-outs. Can alternatively be handled by setting the \"timeout\" configuration property.\n" +
            "-v\n\tBe verbose. Can alternatively be handled by setting the \"verbose\" configuration property.\n" +
            "-x\n\tDo not follow the image links. Can alternatively be handled by setting the \"followImgLinks\" configuration property.\n";

    /**
     * The number of threads in the pool.
     */
    private final int threadNumber;

    /**
     * Set whether you want logging output during crawl.
     */
    private final boolean verbose;

    /**
     * Set whether you want logging output to be redirected to a file.
     */
    private final boolean redirect;

    /**
     * The path to the log file.
     */
    private final String logFilePath;

    /**
     * The path to the file-system where the downloaded files will be stored
     * into.
     */
    private final String storagePath;

    /**
     * The maximum number of files allowed to be downloaded. Set it to a value
     * less than or equal to zero to set it to unlimited (highly discouraged).
     */
    private final int maximumFileNumber;

    /**
     * The connection timeout period in seconds.
     */
    private final int timeout;

    /**
     * How much deep in the link graph will the crawler go in the recursive
     * case. 0 (or less than?!) means the crawler will not descend further down
     * the link graph.
     */
    private final int depth;

    /**
     * Set whether image links will be followed.
     */
    private final boolean followImgLinks;

    /**
     * The agent name with which the crawler is "introduced" to a web server.
     */
    private final String agent;

    /**
     * Set whether the URL provided by the user should be treated as a sitemap
     * with which the crawling process should be initialized.
     */
    private final boolean sitemapAssisted;

    /**
     * The crawler's configurator.
     */
    private static final WebCrawlerConfigurator configurator = WebCrawlerConfigurator.getInstance();

    /**
     * The thread pool.
     */
    private final ExecutorService executor;

    /**
     * A Queue of futures for the submitted threads.
     */
    private final Queue<Future<?>> futures;

    /**
     * A map for storing the already visited web pages by the web crawler.
     * Using {@code ConcurrentMap<K, V>} to allow multiple reads, single write
     * by the crawler's threads.
     */
    private final ConcurrentMap<String, URL> visited;

    /**
     * Constructor.
     */
    public WebCrawler() {
        threadNumber = configurator.propertyInteger("threadNumber");
//        System.out.println("threadNumber " + threadNumber);

        verbose = configurator.propertyBoolean("verbose");
//        System.out.println("verbose " + verbose);

        logFilePath = configurator.property("logFilePath");
        redirect = (logFilePath.trim().isEmpty() ? false : true);
//        System.out.println("logFilePath " + logFilePath);

        storagePath = configurator.property("storagePath");
//        System.out.println("storagePath " + storagePath);

        maximumFileNumber = configurator.propertyInteger("maximumFileNumber");
//        System.out.println("maximumFileNumber " + maximumFileNumber);

        timeout = configurator.propertyInteger("timeout");
//        System.out.println("timeout " + timeout);

        depth = configurator.propertyInteger("depth");
//        System.out.println("depth " + depth);

        followImgLinks = configurator.propertyBoolean("followImgLinks");
//        System.out.println("followImgLinks " + followImgLinks);

        sitemapAssisted = configurator.propertyBoolean("sitemapAssisted");
//        System.out.println("sitemapAssisted " + sitemapAssisted);

        agent = configurator.property("agent");
//        System.out.println("agent " + agent);

        executor = Executors.newFixedThreadPool(this.threadNumber);
        futures = new LinkedList<Future<?>>();
        visited = new ConcurrentHashMap<String, URL>();
    }

    /**
     * The main function that drives the execution.
     *
     * @param args
     *     The command-line arguments.
     */
    public static void main(String[] args) {
        URL url = null;

        System.out.println(strAppHeader + "\n");

        boolean abort = false;
        // Parse CMD arguments.
        // At least one argument needed (that of the start url).
        if(args.length < 1) {
            System.out.println("Error: Defining the starting URL is mandatory!");
            System.out.println(strAppUsage);
            abort = true;
        }
        else {
            int i = 0;

            // Cycle through cmd line arguments.
            while(!abort && (i < args.length)) {

                if(args[i].charAt(0) == '-') {
                    // If it is a switch... which switch?
                    switch(args[i].charAt(1)) {
                    case 'a':
                        // Determine the name with which the crawler is introduced to the
                        // various web servers it traverses.
                        configurator.assign("agent", args[i].substring(2, args[i].length()));
                        break;
//                    case 'A':
//                        // comma-separated list of accepted extensions.
//                        configurator.assign("accept", args[i].substring(2, args[i].length()));
//                        break;
                    case 'd':
                        // define depth of crawling function in levels
                        configurator.assign("depth", args[i].substring(2, args[i].length()));
                        break;
                    case 'h':
                        // print help info
                        System.out.println(strAppHelp);
                        abort = true;
                        break;
                    case 'H':
                        // Print application header
                        System.out.println(strAppHeader);
                        abort = true;
                        break;
                    case 'm':
                        // Maximum number of threads
                        configurator.assign("threadNumber", args[i].substring(2, args[i].length()));
                        break;
                    case 'n':
                        // set the maximum allowed number of downloaded files
                        configurator.assign("maximumFileNumber", args[i].substring(2, args[i].length()));
                        break;
                    case 'o':
                        // Redirect crawling output to a file
                        configurator.assign("logFilePath", args[i].substring(2, args[i].length()));
                        break;
                    case 'p':
                        // Determine storage area on local FS
                        configurator.assign("storagePath", args[i].substring(2, args[i].length()));
                        break;
//                    case 'R':
//                        // comma-separated list of rejected extensions.
//                        configurator.assign("accept", args[i].substring(2, args[i].length()));
//                        break;
                    case 's':
                        // Determine the URL given is a sitemap.
                        configurator.assign("sitemapAssisted", "true");
                        break;
                    case 't':
                        // define connection time-out period
                        configurator.assign("timeout", args[i].substring(2, args[i].length()));
                        break;
                    case 'v':
                        // Turn on/off verbose mode
                        configurator.assign("verbose", "true");
                        break;
                    case 'x':
                        // don't follow links incorporated into images
                        configurator.assign("followImgLinks", "false");
                        break;
                    default:
                        // cmd line contained a non identifiable switch
                        System.out.println("Error: Switch " + args[i] + " is not valid.");
                        System.out.println(strAppUsage);
                        break;

                    }
                }
                else {
                    // it is not a switch... its a URL
                    if (url == null) {
                        // if it there is no url already set, then set it...
                        String tmp = new String(args[i]);
                        try {
                            url = new URL(tmp);
                        }
                        catch (MalformedURLException murle) {
                            murle.printStackTrace();
                        }
                    }
                    else {
                        // else, there is some problem since we need only one
                        // URL from the command line.
                        System.out.println("Warning: A URL has been already defined. Ignoring " + args[i] +".");
                        System.out.println(strAppUsage);
                    }
                }
                // Prepare for next CMD argument.
                i++;
            }
        } // End - Parse CMD arguments.

        if (!abort) {
            if (url != null) {
                WebCrawler spiderman = new WebCrawler();
                spiderman.start(url);
                spiderman.block();
                spiderman.stop();
            }
            else {
                    System.out.println("Error: Defining the starting URL is mandatory!");
                    System.out.println(strAppUsage);
            }
        }
    }

    /**
     * Offers the given spider job to the crawler. The job's URL is checked
     * whether it is already processed. If not, it is submitted its future
     * added to the futures' queue. Else the job is discarded.
     *
     * @param spider
     *     The {@code Runnable} job to be submitted.
     *
     * @return
     *     {@code true} if the offered job was accepted; {@code false}
     *     otherwise.
     */
    public boolean crawl(Spider spider) {
        boolean accepted = false;

        // Put the job's URL to the visited registry.
        if (visited.putIfAbsent(spider.getUrl().toString(), spider.getUrl()) == null) {
            futures.add(executor.submit(spider));
            accepted = true;
        }

        return accepted;
    }

    /**
     * Triggers the crawling process.
     *
     * @param url
     *     The initial URL.
     */
    public void start(URL url) {
        System.out.println("Starting crawler...");
        if (isSitemapAssisted()) {
            System.out.println("Accessing sitemap: " + url.toString());
            SAXSitemapParser parser = new SAXSitemapParser(url);
            List<WebPage> webpages = parser.parse();
            for (WebPage page: webpages) {
                crawl(new Spider(0, page.getLocation(), 0, this));
            }
        }
        else {
            crawl(new Spider(0, url, 0, this));
        }
    }

    /**
     * <p>
     * Waits on the futures queue and consumes them. This function blocks until
     * all futures are consumed (a.k.a. all submitted spider jobs have
     * finished).
     * </p>
     *
     * <p>
     * It seems that this function is not the optimal solution to the problem of
     * "waiting until all tasks are finished before shutting down the executor".
     * Not sure it works properly at all situations. Needs rework.
     * </p>
     */
    public void block() {
        while (!futures.isEmpty()) {
            System.out.println(futures.size());
            try {
                futures.remove().get();
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>
     * Stops the crawler.
     * <p>
     *
     * <p>
     * The following method shuts down the {@code ExecutorService} of the
     * crawler in two phases; first by calling {@code shutdown()} to reject
     * incoming tasks, and then calling {@code shutdownNow()}, if necessary,
     * to cancel any lingering tasks.
     * </p>
     */
    public void stop() {
        System.out.print("Stopping crawler: ");
        executor.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
            System.out.println("OK");
        }
        catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executor.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }


    // -- Getters/Setters


    /**
     * Gets the number of threads in the pool.
     *
     * @return
     *     The number of threads in the pool.
     */
    public int getThreadNumber() {
        return threadNumber;
    }

    /**
     * Gets the verbose switch.
     *
     * @return
     *     {@code true} if the verbose mode is enabled; {@code false}
     *         otherwise.
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Gets the redirect switch.
     *
     * @return
     *     {@code true} if the redirect mode is enabled; {@code false}
     *         otherwise.
     */
    public boolean isRedirect() {
        return redirect;
    }

    /**
     * Gets the path to the log file.
     *
     * @return
     *     The path to the log file.
     */
    public String getLogFilePath() {
        return logFilePath;
    }

    /**
     * Gets the the path to the file-system where the downloaded files will be
     * stored into.
     *
     * @return
     *     The file-system path where the downloaded files will be stored.
     */
    public String getStoragePath() {
        return storagePath;
    }

    /**
     * Gets the maximum number of files to be downloaded.
     *
     * @return
     *     The maximum number of files to be downloaded.
     */
    public int getMaximumFileNumber() {
        return maximumFileNumber;
    }

    /**
     * Gets the connection timeout period.
     *
     * @return
     *     The connection timeout period (in seconds).
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Gets the number of levels that crawling should take place.
     *
     * @return
     *     The number of levels the crawler is going to descent.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Gets the follow image links switch.
     *
     * @return
     *     {@code true} if the image links are to be followed; {@code false}
     *     otherwise.
     */
    public boolean doFollowImgLinks() {
        return followImgLinks;
    }

    /**
     * Gets the agent name used to "introduce" to web servers.
     *
     * @return
     *     The agent name.
     */
    public String getAgent() {
        return agent;
    }

    /**
     * Gets the sitemap assisted switch.
     *
     * @return
     *     {@code true} if the crawl is to be assisted by a sitemap;
     *     {@code false} otherwise.
     */
    public boolean isSitemapAssisted() {
        return sitemapAssisted;
    }

    /**
     * Gets the web crawler's executor service.
     *
     * @return
     *     The executor service.
     */
    public ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Gets the web crawler's futures.
     *
     * @return
     *     The futures.
     */
    public Queue<Future<?>> getFutures() {
        return futures;
    }

}
