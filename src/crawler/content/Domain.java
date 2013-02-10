package crawler.content;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="domain")
public class Domain {

    @Column(name="pk_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name="name")
    private String name;

    /**
     * Defines the {@code -Many} part of the {@code OneToMany} relationship
     * which dictates that a {@code Domain} may have several different
     * {@code Content}s associated with it. The {@code One-} part is mapped by
     * the {@code domain} field of the {@code Content} object.
     *
     * @see {@link crawler.content.Content}
     */
    @OneToMany(mappedBy="domain")
    private final List<Content> contents = new ArrayList<Content>();


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
     * Gets the name of the domain.
     *
     * @return
     *     The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the domain.
     *
     * @param name
     *     The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of contents associated with this domain.
     *
     * @return
     *     The contents
     */
    public List<Content> getContents() {
        return contents;
    }

}
