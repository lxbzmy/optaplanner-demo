package cn.devit.planner;

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

}
