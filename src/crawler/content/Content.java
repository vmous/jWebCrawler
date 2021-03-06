package crawler.content;

import javax.persistence.CascadeType;
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

    @Column(name="content")
    private String content;

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
    @ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
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
    @ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
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
     * Gets the content.
     *
     * @return
     *     The content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content.
     *
     * @param content
     *     The content to set.
     */
    public void setContent(String content) {
        this.content = content;
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


    // -- Overriding


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = 1;

        result = PRIME * result + ((remoteURI == null) ? 0 : remoteURI.hashCode());

        result = PRIME * result + ((localURI == null) ? 0 : localURI.hashCode());

        result = PRIME * result + ((title == null) ? 0 : title.hashCode());

        return result;

    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (obj == null || this.getClass() != obj.getClass()) return false;

        Content other = (Content) obj;

        return  ( this.remoteURI == other.remoteURI || (this.remoteURI != null && this.remoteURI.equals(other.remoteURI)) ) &&
                ( this.localURI == other.localURI || (this.localURI != null && this.localURI.equals(other.localURI)) ) &&
                ( this.title == other.title || (this.title != null && this.title.equals(other.title)) );


    }
}
