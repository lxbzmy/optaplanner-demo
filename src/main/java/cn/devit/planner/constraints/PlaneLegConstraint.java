package cn.devit.planner.constraints;

import cn.devit.planner.Leg;
import cn.devit.planner.Plane;

/**
 * 有些飞机受到到能力限制（年限，航程，气候）不能飞指定的航段
 * <p>
 *
 *
 * @author lxb
 *
 */
public class PlaneLegConstraint {

    Leg leg;

    Plane plane;

    public PlaneLegConstraint(Plane plane2, Leg leg2) {
        plane = plane2;
        leg = leg2;
    }

    /**
     * @return the leg
     */
    public Leg getLeg() {
        return leg;
    }

    /**
     * @return the plane
     */
    public Plane getPlane() {
        return plane;
    }

    @Override
    public String toString() {
        return "限制：" + plane + "不能飞" + leg;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((leg == null) ? 0 : leg.hashCode());
        result = prime * result + ((plane == null) ? 0 : plane.hashCode());
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
        PlaneLegConstraint other = (PlaneLegConstraint) obj;
        if (leg == null) {
            if (other.leg != null)
                return false;
        } else if (!leg.equals(other.leg))
            return false;
        if (plane == null) {
            if (other.plane != null)
                return false;
        } else if (!plane.equals(other.plane))
            return false;
        return true;
    }

    
}
