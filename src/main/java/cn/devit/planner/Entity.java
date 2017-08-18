package cn.devit.planner;

import org.optaplanner.core.api.domain.lookup.PlanningId;

/**
 * We Entity only has ID.
 * <p>
 *
 *
 * @author lxb
 *
 */
public abstract class Entity {

    protected String id;
    
    public void setId(String id) {
        this.id = id.trim();
    }
    
    @PlanningId
    public String getId() {
        return id;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Entity other = (Entity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
