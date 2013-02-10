package crawler.content;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="mime")
public class MIME {

    @Column(name="pk_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name="content_type")
    private String contentType;

    /**
     * Defines the {@code -Many} part of the {@code OneToMany} relationship
     * which dictates that a {@code MIME} may have several different
     * {@code Content}s associated with it. The {@code One-} part is mapped by
     * the {@code mime} field of the {@code Content} object.
     *
     * Data structure implementing the {@code Set} interface in order not to
     * allow duplicates. Using {@code HashSet} implementation here in favor of
     * speed over ordering guarantees (otherwise use {@code TreeSet) or
     * {@code LinkedHashSet}.
     *
     * @see {@link crawler.content.Content}
     */
    @OneToMany(mappedBy="mime")
    private final Set<Content> contents = new HashSet<Content>();


    // -- Getters/Setters


    /**
     * Gets the identification number.
     *
     * @return
     *     The identification number.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the identification number.
     *
     * @param id
     *     The identification number to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the content type.
     *
     * @return
     *     The content type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the content type.
     *
     * @param contentType
     *     The content type to set.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Gets the set of contents associated with this MIME.
     *
     * @return
     *     The list of contents.
     */
    public Set<Content> getContents() {
        return contents;
    }

}
