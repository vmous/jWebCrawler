package crawler;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

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
        Query q = em.createQuery("SELECT x FROM Content x");

        if (q.getResultList().size() == 0) {
            assertTrue(q.getResultList().size() == 0);

            // Create the content.
            Content c = new Content();

            // Create the domain
            Domain d = new Domain();
            d.setName("jazzman.webhop.net");
            // Set the domain for this content...
            c.setDomain(d);
            // ...add this content to the domain's list.
            d.getContents().add(c);

            // Create the MIME
            MIME m = new MIME();
            m.setContentType("text/html");
            // Set the mime for this content...
            c.setMime(m);
            // ...add this contents to the MIME's list.
            m.getContents().add(c);

            em.persist(c);
            em.persist(d);
            em.persist(m);
        }

        // End the local transaction by commit.
        em.getTransaction().commit();

        em.close();
    }
}
