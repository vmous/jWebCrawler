package crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import toolbox.util.URLManipulator;

/**
 * The elementary spider task of crawling on a single URL.
 *
 * @author billy
 */
public class Spider implements Runnable {
    private final int id;

    private final URL url;

    private final int level;

    private final WebCrawler spiderman;

    public Spider(int id, URL url, int level, WebCrawler spiderman) {
        this.id = id;
        this.url = url;
        this.level = level;
        this.spiderman = spiderman;
    }

    @Override
    public void run() {
        workItOut();
    }

    /**
     * The basic work method.
     */
    private void workItOut() {
        StringBuilder info = new StringBuilder(1024);

        info.append("##########" + "\n");
        info.append("# [" + id + "] on \" " + url.toString() + "\"\n");
        // The objects that we're dealing with here a strings for urls
        try {
            HttpURLConnection conn = initHttpConn(url);

            info.append("# Length: " + conn.getContentLength()  + " [" + conn.getContentType() + "]\n");

            // Prepare to everything that is needed
            String filePathName = url.getFile();
            String hostName = url.getHost();
            String filePath = filePathName.substring(0, filePathName.lastIndexOf('/') + 1);
            String fileName = filePathName.substring( (filePathName.lastIndexOf('/')) + 1 );

            if(fileName.equalsIgnoreCase("")) {
                fileName = "index.html";
            }
            String fileMime = conn.getContentType();

            if(fileName.endsWith(".mpg")) {
                info.append("# Ignoring .mpg file.\n");
                return;
            }

            String savePath = URLManipulator.constructSavePath(spiderman.getStoragePath(), hostName + filePath);
            File path = new File(savePath);
            if(!path.exists()) {
                path.mkdirs();
            }

            String saveName = savePath + fileName;

            // Download URL
            info.append("# Saving file " + saveName + ": ");
            URLManipulator.streamURLtoFile(url, saveName);
            info.append("OK\n");

//            String title = fileName;

            // if url is a web page try to extract hyperlinks
            if(fileMime.contains("text/html")) {
                String rawPage = URLManipulator.streamURLToString(url);
                String smallPage = rawPage.toLowerCase().replaceAll("\\s", " ");

                info.append("# Extracting title: ");
                String tmpTitle = null;
                tmpTitle = URLManipulator.extractTitle(rawPage, smallPage);
                if(tmpTitle != null) {
//                    title = tmpTitle;
                }
                info.append("OK\n");

                // If you haven't reached the maximum depth, extract links and push them.
                if (level < spiderman.getDepth()) {
                    info.append("# Extracting links: ");
                    // treat the url as an html file and try to extract links
                    Vector<String> links =
                            URLManipulator.extractLinks(
                                    rawPage,
                                    smallPage,
                                    spiderman.doFollowImgLinks()
                                    );

                    // Convert each link text to a url and enque
                    int linkNumber = 0;
                    for (int n = 0; n < links.size(); n++) {
                        try {
                            // urls might be relative to current page
//                            URL link = new URL(url, links.elementAt(n));
                            URL link = new URL(links.elementAt(n));
                            spiderman.crawl(new Spider(0, link, level + 1, spiderman));
                            linkNumber++;
                        }
                        catch (MalformedURLException murle) {
                            murle.printStackTrace();
                            // Ignore malformed URLs, the link extractor might
                            // have failed.
                        }
                    }
                    info.append(linkNumber + " valid links found.\n");
                }
            }

            info.append("# Storing to database: ");
            // store info into database
//          DBObject.storeCrawlInfo(title, hostName, url.toString(), saveName, fileMime);
            info.append("NO DB AVAILABLE YET\n");


        }
        catch (IOException ioe) {
            ioe.printStackTrace();
//            info.append("Failed saving to file " + saveName + " from URL " + url.toString() + " due to a " + ioe.toString() + "\n");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            info.append("\n");

            if(spiderman.isVerbose()) {
                if(spiderman.isRedirect()) {
                    try {
                        printToFile(info, spiderman.getLogFilePath());
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    printToConsole(info);
                }
            }
        }
    }

    /**
     * Prepares an HTTP URL connection.
     *
     * @param url
     *     The URL to connect to.
     *
     * @return An established HTTP connection to the given URL.
     *
     * @throws IOException
     */
    private HttpURLConnection initHttpConn(URL url)
            throws IOException {
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();

        String agent = WebCrawlerConfigurator.getInstance().property("agent");
        String accept = WebCrawlerConfigurator.getInstance().property("accept");
        int msectimeout = 1000 * WebCrawlerConfigurator.getInstance().propertyInteger("timeout");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.addRequestProperty("User-Agent", agent);
        conn.addRequestProperty("Accept", accept);
        conn.addRequestProperty("Cache-Control", "no-cache");
        conn.setReadTimeout(20000);
        conn.setConnectTimeout(msectimeout);
        conn.connect();

        return conn;
    }

    /**
     * Prints to standard output.
     *
     * @param info
     *     The string to be printed.
     */
    private synchronized void printToConsole(StringBuilder info) {
        System.out.println(info.toString());
    }

    /**
     * Print to a file.
     *
     * @param info
     *     The string to be printed.
     * @param fileName
     *     The path to the file to be used.
     *
     * @throws IOException
     */
    private synchronized void printToFile(StringBuilder info, String filePath)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));

        writer.write(info.toString());
        writer.close();
    }

}
