package crawler;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import crawler.content.Content;
import crawler.content.Domain;
import crawler.content.MIME;

import junit.framework.TestCase;

/**
 * Tests the JPA infrastructure of the project.
 *
 * @author billy
 */
public class PersistencyTest extends TestCase {

    private static final String PERSISTENCE_UNIT_NAME = "jWebCrawler";
    private EntityManagerFactory emf;

    @Before
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @After
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testSomehting() {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        // Begin a new local transaction.
        em.getTransaction().begin();

        // Read the existing entries.

        // Create the content.
        Content c = new Content();

        // Create the domain
        String domainName = "jazzman.webhop.net";
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
            assertEquals(1, dl.size());
            d = dl.remove(0);
        }
        // Set the domain for this content...
        c.setDomain(d);

        // Create the MIME
        String mimeContentType = "text/html";
        // Check if it already exists.
        TypedQuery<MIME> mq = em.createQuery("SELECT x FROM MIME x WHERE x.contentType = '" + mimeContentType + "'", MIME.class);
        List<MIME> ml = mq.getResultList();
        MIME m;
        if (ml.size() == 0) {
            m = new MIME();
            m.setContentType(mimeContentType);
            // Add this contents to the MIME's set.
            m.getContents().add(c);
        }
        else {
            assertEquals(1, ml.size());
            m = ml.remove(0);
        }
        // Set the mime for this content...
        c.setMime(m);

        em.persist(c);

        // End the local transaction by commit.
        em.getTransaction().commit();

        em.close();
    }
}
