package cn.devit.planner;

import java.util.HashSet;

/**
 * 边界数据容器
 * <p>
 *
 *
 * @author lxb
 *
 */
public class Edge extends HashSet<FlightLeg> {

    private static final long serialVersionUID = 1L;

    public static enum EdgeType {
        left, right
    }

    EdgeType type;

    public Edge(EdgeType left) {
        type = left;
    }

    public EdgeType getType() {
        return type;
    }
}
