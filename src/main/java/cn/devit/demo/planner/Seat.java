package cn.devit.demo.planner;

import static cn.devit.demo.planner.SeatFeature.*;

public class Seat {

    int row;

    int col;

    SeatFeature feature = 过道;

    Price price = Price.Y600;

    public Seat(int row, int col, SeatFeature feature, Price price) {
        super();
        this.row = row;
        this.col = col;
        this.feature = feature;
        this.price = price;
    }

    @Override
    public String toString() {
        return "seat(" + row + "排" + col + "号," + feature + "," + price + ")";
    }
}
