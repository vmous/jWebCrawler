package crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import crawler.content.Content;
import crawler.content.Domain;
import crawler.content.MIME;

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

        String strURL = this.url.toString();
        int contentLength;
        String contentType;
        String domainName;
        String rFilePathName;
        String rFilePath;
        String rFileName;
        String lFilePathName;
        String lFilePath;
        String lFileName;
        String title;

        info.append("##########" + "\n");
        info.append("# [" + id + "] on \" " + strURL + "\"\n");
        // The objects that we're dealing with here a strings for urls
        try {
            HttpURLConnection conn = initHttpConn(url);

            contentLength = conn.getContentLength();
            contentType = conn.getContentType();

            // Prepare to everything that is needed
            domainName = url.getHost();
            rFilePathName = url.getFile();
            rFilePath = rFilePathName.substring(0, rFilePathName.lastIndexOf('/') + 1);
            rFileName = rFilePathName.substring( (rFilePathName.lastIndexOf('/')) + 1 );

            info.append(
                    "# Content Length: " + contentLength  + "\n" +
                    "# Content Type: " + contentType + "\n" +
                    "# File: " + rFileName + "\n"
                    );

            lFilePath = URLManipulator.constructSavePath(spiderman.getStoragePath(), domainName + rFilePath);
            lFileName = rFileName.equalsIgnoreCase("") ? "index.html" : rFileName;
            lFilePathName = lFilePath + lFileName;

            File path = new File(lFilePath);
            if(!path.exists()) {
                path.mkdirs();
            }

            // Download URL
            info.append("# Saving to " + lFilePathName + ": ");
            URLManipulator.streamURLtoFile(url, lFilePathName);
            info.append("OK\n");

            title = lFileName;

            // if url is a web page try to extract hyperlinks
            if(contentType.contains("text/html")) {
                String rawPage = URLManipulator.streamURLToString(url);
                String smallPage = rawPage.toLowerCase().replaceAll("\\s", " ");

                info.append("# Extracting title: ");
                String t = URLManipulator.extractTitle(rawPage, smallPage);
                if (!t.equals("")) {
                    title = t;
                    info.append("OK\n");
                }
                else {
                    info.append("FAILED\n");
                }

                // If you haven't reached the maximum depth, extract links and push them.
                if (level < spiderman.getDepth()) {
                    info.append("# Extracting links: ");
                    // treat the url as an html file and try to extract links
                    Set<String> links =
                            URLManipulator.extractLinks(
                                    rawPage,
                                    smallPage,
                                    spiderman.doFollowImgLinks()
                                    );

                    // Convert each link text to a url and enqueue
                    int linkNumber = 0;
                    Iterator<String> iter = links.iterator();

                    while (iter.hasNext()) {
                        try {
                            // urls might be relative to current page
//                            URL link = new URL(url, links.elementAt(n));
                            URL link = new URL(iter.next());
                            if (spiderman.crawl(new Spider(0, link, level + 1, spiderman)))
                                linkNumber++;
                        }
                        catch (MalformedURLException murle) {
                            murle.printStackTrace();
                            // Ignore malformed URLs, the link extractor might
                            // have failed.
                        }
                    }

                    info.append(linkNumber + " new valid links found.\n");
                }
            }

            info.append("# Storing to database: ");

            // TODO: Make PERSISTENCE_UNIT_NAME a property of the application.
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("jWebCrawler");
            EntityManager em = emf.createEntityManager();

            // Begin a new local transaction.
            em.getTransaction().begin();

            // Read the existing entries.

            // Create the content.
            Content c = new Content();
            c.setRemoteURI(rFilePathName);
            c.setLocalURI(lFilePathName);
            c.setTitle(title);

            // Create the domain
            // Check if it already exists.
            TypedQuery<Domain> dq = em.createQuery("SELECT x FROM Domain x WHERE x.name = '" + domainName + "'", Domain.class);
            List<Domain> dl = dq.getResultList();
            Domain d;
            if (dl.size() == 0) {
                d = new Domain();
                d.setName(domainName);
                // Add this content to the domain's set.
                d.getContents().add(c);
            }
            else {
                assert (dl.size() == 1);
                d = dl.remove(0);
            }
            // Set the domain for this content...
            c.setDomain(d);

            // Create the MIME
            // Check if it already exists.
            TypedQuery<MIME> mq = em.createQuery("SELECT x FROM MIME x WHERE x.contentType = '" + contentType + "'", MIME.class);
            List<MIME> ml = mq.getResultList();
            MIME m;
            if (ml.size() == 0) {
                m = new MIME();
                m.setContentType(contentType);
                // Add this contents to the MIME's set.
                m.getContents().add(c);
            }
            else {
                assert (ml.size() == 1);
                m = ml.remove(0);
            }
            // Set the mime for this content...
            c.setMime(m);

            em.persist(c);

            // End the local transaction by commit.
            em.getTransaction().commit();

            em.close();
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


    // -- Getters / Setters


    /**
     * Gets the URL the to be crawled.
     *
     * @return
     *     The URL.
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Gets the (depth) level of this crawl.
     *
     * @return
     *     The level.
     */
    public int getLevel() {
        return level;
    }
}
