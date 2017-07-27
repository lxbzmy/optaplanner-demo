package cn.devit.planner;

/**
 * 飞机
 * <p>
 *
 *
 * @author lxb
 *
 */
public class Plane extends Entity {

    /**
     * 机型，机型衡量载客能力。
     */
    String model;

    public Plane(String id) {
        setId(id);
    }

    /**
     * 设置飞机型号
     * 
     * @param model
     */
    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {
        return "✈️[" + id +" M" +model+"]";
    }

}
