package crawler.content;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="content")
public class Content {

    @Column(name="pk_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name="remote_uri")
    private String remoteURI;

    @Column(name="local_uri")
    private String localURI;

    @Column(name="title")
    private String title;

    /**
     * Defines the relationship that a {@code Content} object references a
     * single {@code Domain} target with the specified foreign key.
     *
     * The {@code Many-} part of this relationship is enforced by the
     * definition of a {@code OneToMany} relationship in the {@code Domain}
     * object, mapped by this field.
     *
     * @see {@link crawler.content.Domain}
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="fk_domain_id")
    private Domain domain;

    /**
     * Defines the relationship that a {@code Content} object references a
     * single {@code MIME} target with the specified foreign key.
     *
     * The {@code Many-} part of this relationship is enforced by the
     * definition of a {@code OneToMany} relationship in the {@code MIME}
     * object, mapped by this field.
     *
     * @see {@link crawler.content.MIME}
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="fk_mime_id")
    private MIME mime;


    // -- Getters/Setters


    /**
     * Gets the identification number.
     *
     * @return
     *     The id
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
     * Gets the remote URI.
     *
     * @return
     *     The remote URI as a {@code String}.
     */
    public String getRemoteURI() {
        return remoteURI;
    }

    /**
     * Sets the remote URI.
     *
     * @param remoteURI
     *     The remote URI, as a {@code String}, to set.
     */
    public void setRemoteURI(String remoteURI) {
        this.remoteURI = remoteURI;
    }

    /**
     * Gets the local URI.
     *
     * @return
     *     The local URI as a {@code String}.
     */
    public String getLocalURI() {
        return localURI;
    }

    /**
     * Sets the remote URI.
     *
     * @param localURI
     *     The local URI, as a {@code String}, to set.
     */
    public void setLocalURI(String localURI) {
        this.localURI = localURI;
    }

    /**
     * Gets the title.
     *
     * @return
     *     The title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     *
     * @param title
     *     The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the domain.
     *
     * @return
     *     The domain.
     */
    public Domain getDomain() {
        return domain;
    }

    /**
     * Sets the domain.
     *
     * @param domain
     *     The domain to set.
     */
    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    /**
     * Gets the MIME.
     *
     * @return
     *     The MIME.
     */
    public MIME getMime() {
        return mime;
    }

    /**
     * Sets the MIME.
     *
     * @param mime
     *     The MIME to set.
     */
    public void setMime(MIME mime) {
        this.mime = mime;
    }

}
