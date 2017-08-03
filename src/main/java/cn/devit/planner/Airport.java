package cn.devit.planner;

public class Airport extends Entity {

    public Airport(String id) {
        this.setId(id);
    }

    @Override
    public String toString() {
        return "⚓" + id;
    }
}
