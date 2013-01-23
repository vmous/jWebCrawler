package crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebCrawler {
    private static final String VERSION = "0.0.0.1";

    /**
     * The number of threads in the pool.
     */
    private final int threadNumber;

    /**
     * Set whether you want logging output during crawl.
     */
    private final boolean verbose;

    /**
     * Set wheter you want logging output to be redirected to a file.
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
     * The agent name with which the crawler is "introduced" to a web server.
     */
    private final String agent;

    /**
     * The initial URL to start the Web crawl.
     */
    private final URL startURL;

    /**
     * The crawler's configurator.
     */
    private static final WebCrawlerConfigurator configurator = WebCrawlerConfigurator.getInstance();

    /**
     * The thread pool.
     */
    private final ExecutorService executor;

    /**
     * Constructor.
     *
     * @param startURL
     *     The starting URL for the crawling.
     */
    public WebCrawler(URL startURL) {
        threadNumber = configurator.propertyInteger("threadNumber");
        System.out.println("threadNumber " + threadNumber);
        verbose = configurator.propertyBoolean("verbose");
        System.out.println("verbose " + verbose);
        redirect = configurator.propertyBoolean("redirect");
        System.out.println("redirect " + redirect);
        logFilePath = configurator.property("logFilePath");
        System.out.println("logFilePath " + logFilePath);
        storagePath = configurator.property("storagePath");
        System.out.println("storagePath " + storagePath);
        maximumFileNumber = configurator.propertyInteger("maximumFileNumber");
        System.out.println("maximumFileNumber " + maximumFileNumber);
        timeout = configurator.propertyInteger("timeout");
        System.out.println("timeout " + timeout);
        depth = configurator.propertyInteger("depth");
        System.out.println("depth " + depth);
        agent = configurator.property("agent");
        System.out.println("agent " + agent);
        this.startURL = startURL;

        executor = Executors.newFixedThreadPool(this.threadNumber);
    }

    /**
     * The main function that drives the execution.
     *
     * @param args
     *     The command-line arguments.
     */
    public static void main(String[] args) {

        String strAppAuthor = "Vassilis S. Moustakas (vsmoustakas[at]gmail[dot]com)";

        String strAppName =
                "Web Crawler v." + VERSION;

        String strAppUsage =
                strAppName + "\n" +
                "Usage: java WebCrawler [options] <url>\n\n" +
                "Use -h for more help";

        String strAppVersion =
                strAppName + "\n" +
                "\n" +
                "This program is distributed in the hope that it will be useful,\n" +
                "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the\n" +
                "GNU General Public License for more details.\n" +
                "\n" +
                "Authored by " + strAppAuthor + ".";

        String strAppHelp =
                strAppName + "\n" +
                "A simple multi-threaded, database assisted, network content retriever.\n" +
                "Author: " + strAppAuthor + "\n" +
                "Usage: java JCrawler [options] <url>\n" +
                "\n" +
                "Startup:\n" +
                "  -V,  display the version of JCrawler and exit.\n" +
                "  -h,  print this help.\n" +
//                "\n" +
//                "Logging:\n" +
//                "  -o<FILE>, log messages to FILE.\n" +
//                "  -q, enable quiet mode (no output).\n" +
//                "  -v, be verbose (the default here).\n" +
//                "\n" +
//                "Download:\n" +
//                "  -t<SECONDS>, set SECONDS for time-outs.\n" +
//                "  -n<NUMBER>, download a maximum of NUMBER files." +
//                "\n" +
//                "Directories:\n" +
//                "  -p<PREFIX>, save retrieved files to PREFIX/...\n" +
//                "\n" +
//                "HTTP options:\n" +
//                "  -u<AGENT>, identify as Agent instead of JCrawler/VERSION.\n" +
//                "\n" +
//                "Recursive download:\n" +
//                "  -r, enable recursive download.\n" +
//                "  -l<LEVELS>, specify maximum LEVELS of recursion depth." +
//                "\n" +
//                "Recursive accept/reject:\n" +
//                "  -A, comma-separated list of accepted mimes.\n" +
//                "  -R, comma-separated list of rejected mimes.\n" +
//                "  -x, don't follow links incorporated into images.\n" +
//                "\n" +
                "Multi-threading:" +
                "  -m<THREADS>, specify THREADS number of threads to be used at maximum.";

        URL url = null;

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
                    case 'm':
                        // Maximum number of threads
                        configurator.assign("threadNumber", args[i].substring(2, args[i].length()));
                        break;
                    case 'v':
                        // Turn on verbose mode and display info
                        configurator.assign("verbose", "true");
                        break;
                    case 'q':
                        // Turn on verbose mode and display info
                        configurator.assign("quiet", "true");
                        break;
                    case 'o':
                        // Print output to FILE
                        configurator.assign("redirect", "true");
                        configurator.assign("logFilePath", args[i].substring(2, args[i].length()));
                        break;
                    case 'p':
                        // Determine storage area on local FS
                        configurator.assign("storagePath", args[i].substring(2, args[i].length()));
//                        q.setFilenamePrefix(args[i].substring(2, args[i].length()));
                        break;
                    case 'n':
                        // set the maximum allowed number of downloaded files
                        configurator.assign("maximumFileNumber", args[i].substring(2, args[i].length()));
//                        q.setMaxElements(Integer.parseInt(args[i].substring(2, args[i].length())));
                        break;
                    case 't':
                        // define connection time-out period
                        configurator.assign("timeout", args[i].substring(2, args[i].length()));
                        break;
                    case 'd':
                        // determine "recursive" traversing of pages
//                        configurator.assign("recursive", "true");
                        // define depth of crawling function in levels
                        configurator.assign("depth", args[i].substring(2, args[i].length()));
                        break;
                    case 'a':
                        // Determine the name with which the crawler is introduced to the
                        // various web servers it traverses.
                        configurator.assign("agent", args[i].substring(2, args[i].length()));
                        break;
                    case 'V':
                        // Print application version
                        System.out.println(strAppVersion);
                        abort = true;
                        break;
//                    case 'x':
//                        // don't follow links incorporated into images
//                        configurator.assign("imglinks", "false");
//                        break;
//                    case 'A':
//                        // comma-separated list of accepted extensions.
//                        configurator.assign("accept", args[i].substring(2, args[i].length()));
//                        break;
//                    case 'R':
//                        // comma-separated list of rejected extensions.
//                        configurator.assign("accept", args[i].substring(2, args[i].length()));
//                        break;
                    case 'h':
                        // print help info
                        System.out.println(strAppHelp);
                        abort = true;
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
                new WebCrawler(url);
            }
            else {
                    System.out.println("Error: Defining the starting URL is mandatory!");
                    System.out.println(strAppUsage);
            }
        }
    }

    /**
     * Gets the number of threads in the pool.
     *
     * @return The number of threads in the pool.
     */
    public int getThreadNumber() {
        return threadNumber;
    }

    /**
     * Gets the verbose switch.
     *
     * @return {@code true} if the verbose mode is enabled; {@code false}
     *         otherwise.
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Gets the redirect switch.
     *
     * @return {@code true} if the redirect mode is enabled; {@code false}
     *         otherwise.
     */
    public boolean isRedirect() {
        return redirect;
    }

    /**
     * Gets the path to the log file.
     *
     * @return The path to the log file.
     */
    public String getLogFilePath() {
        return logFilePath;
    }

    /**
     * Gets the the path to the file-system where the downloaded files will be
     * stored into.
     *
     * @return The file-system path where the downloaded files will be stored.
     */
    public String getStoragePath() {
        return storagePath;
    }

    /**
     * Gets the maximum number of files to be downloaded.
     *
     * @return The maximum number of files to be downloaded.
     */
    public int getMaximumFileNumber() {
        return maximumFileNumber;
    }

    /**
     * Gets the connection timeout period.
     *
     * @return The connection timeout period (in seconds).
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Gets the number of levels that crawling should take place.
     *
     * @return The number of levels the crawler is going to descent.
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Gets the agent name used to "introduce" to web servers.
     *
     * @return The agent name.
     */
    public String getAgent() {
        return agent;
    }

    /**
     * Gets the starting URL.
     *
     * @return The starting URL.
     */
    public URL getStartURL() {
        return startURL;
    }

    /**
     * Gets the web crawler's executor service.
     *
     * @return The executor service.
     */
    public ExecutorService getExecutor() {
        return executor;
    }
}
