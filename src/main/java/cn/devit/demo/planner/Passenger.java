package cn.devit.demo.planner;

public class Passenger {

    String name;

    Seat bookedSeat;

    Seat actSeat;

    public Passenger(String name, Seat seat) {
        super();
        this.name = name;
        this.bookedSeat = seat;
    }

    @Override
    public String toString() {
        return name + " book " + bookedSeat + ",act "+actSeat;
    }

}
