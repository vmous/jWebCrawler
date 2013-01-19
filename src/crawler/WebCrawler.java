package crawler;

public class WebCrawler {
private static final String version = "0.0.0.1";

    public static void main(String[] args) {

        String strAppAuthor = "Vassilis S. Moustakas (vsmoustakas[at]gmail[dot]com)";

        String strAppName =
                "Web Crawler v." + version;

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
                "\n" +
                "Logging:\n" +
                "  -o<FILE>, log messages to FILE.\n" +
                "  -q, enable quiet mode (no output).\n" +
                "  -v, be verbose (the default here).\n" +
                "\n" +
                "Download:\n" +
                "  -t<SECONDS>, set SECONDS for time-outs.\n" +
                "  -n<NUMBER>, download a maximum of NUMBER files." +
                "\n" +
                "Directories:\n" +
                "  -p<PREFIX>, save retrieved files to PREFIX/...\n" +
                "\n" +
                "HTTP options:\n" +
                "  -u<AGENT>, identify as Agent instead of JCrawler/VERSION.\n" +
                "\n" +
                "Recursive download:\n" +
                "  -r, enable recursive download.\n" +
                "  -l<LEVELS>, specify maximum LEVELS of recursion depth." +
                "\n" +
                "Recursive accept/reject:\n" +
                "  -A, comma-separated list of accepted mimes.\n" +
                "  -R, comma-separated list of rejected mimes.\n" +
                "  -x, don't follow links incorporated into images.\n" +
                "\n" +
                "Multi-threading:" +
                "  -m<THREADS>, specify THREADS number of threads to be used at maximum.";

        String confFileName = "config.xml";
        String confFileHeader = "-- Web Crawler Configuration File --";

        WebCrawlerConfigurator.getInstance().setConfigurationFileHeader(confFileHeader);
        WebCrawlerConfigurator.getInstance().setConfigurationFileName(confFileName);

        boolean depthset = false;

        // At least one argument needed (that of the start url).
        if(args.length >= 1) {
            int i = 0;
            boolean visited = false;
            boolean exit = false;

            // Cycle through cmd line arguments.
            while(!exit && (i < args.length)) {

                if(args[i].charAt(0) == '-') {
                    // If it is a switch... which switch?
                    switch(args[i].charAt(1)) {
                    case 'm':
                        // Maximum number of threads
                        WebCrawlerConfigurator.getInstance().assign("threads", args[i].substring(2, args[i].length()));
                        break;
                    case 'q':
                        // Turn on verbose mode and display info
                        WebCrawlerConfigurator.getInstance().assign("quiet", "true");
                        break;
                    case 'v':
                        // Turn on verbose mode and display info
                        // this is by default true
                        WebCrawlerConfigurator.getInstance().assign("verbose", "true");
                        break;
                    case 'V':
                        // Print application version
                        System.out.println(strAppVersion);
                        exit = true;
                        break;
                    case 'o':
                        // Print output to FILE
                        WebCrawlerConfigurator.getInstance().assign("redirect", "true");
                        WebCrawlerConfigurator.getInstance().assign("log_file", args[i].substring(2, args[i].length()));
                        break;
                    case 'p':
                        // Determine storage area on local FS
                        WebCrawlerConfigurator.getInstance().assign("local_path", args[i].substring(2, args[i].length()));
//                        q.setFilenamePrefix(args[i].substring(2, args[i].length()));
                        break;
                    case 'r':
                        // Determine "recursive" traversing of pages
                        WebCrawlerConfigurator.getInstance().assign("recursive", "true");
                        if(!depthset)
                            WebCrawlerConfigurator.getInstance().assign("depth", "2");
                        break;
                    case 'u':
                        // Determine the name with which the crawler is introduced to the
                        // various web servers it traverses.
                        WebCrawlerConfigurator.getInstance().assign("agent", args[i].substring(2, args[i].length()));
                        break;
                    case 't':
                        // define connection time-out period
                        WebCrawlerConfigurator.getInstance().assign("timeout", args[i].substring(2, args[i].length()));
                        break;
                    case 'l':
                        // determine "recursive" traversing of pages
                        WebCrawlerConfigurator.getInstance().assign("recursive", "true");
                        // define depth of crawling function in levels
                        WebCrawlerConfigurator.getInstance().assign("depth", args[i].substring(2, args[i].length()));
                        depthset = true;
                        break;
                    case 'x':
                        // don't follow links incorporated into images
                        WebCrawlerConfigurator.getInstance().assign("imglinks", "false");
                        break;
                    case 'A':
                        // comma-separated list of accepted extensions.
                        WebCrawlerConfigurator.getInstance().assign("accept", args[i].substring(2, args[i].length()));
                        break;
                    case 'R':
                        // comma-separated list of rejected extensions.
                        WebCrawlerConfigurator.getInstance().assign("accept", args[i].substring(2, args[i].length()));
                        break;
                    case 'n':
                        // set the maximum allowed number of downloaded files
                        WebCrawlerConfigurator.getInstance().assign("max_files", args[i].substring(2, args[i].length()));
//                        q.setMaxElements(Integer.parseInt(args[i].substring(2, args[i].length())));
                        break;
                    case 'h':
                        // print help info
                        System.out.println(strAppHelp);
                        exit = true;
                        break;
                    default:
                        // cmd line contained a non identifiable switch
                        System.out.println("Error: Switch " + args[i] + " is not valid.");
                        System.out.println(strAppUsage);
                        break;

                    }
                }
                else {
                    // If it is NOT a switch... its a URL
                    if(!visited) {
                        // push it into queue
//                        q.push(new URL(args[i]), 0);
                        // only one url is allowd
                        visited = true;
                    }
                    else {
                        System.out.println(strAppUsage);
                    }

                }

                // Prepare for next CMD argument.
                i++;
            }
        }
        else {
            System.out.println(strAppUsage);
        }
    }
}
