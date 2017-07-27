package cn.devit.demo.planner;

import java.util.HashSet;
import java.util.Set;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;

public class EasyScore implements EasyScoreCalculator<SeatPlan> {

    public HardSoftScore calculateScore(SeatPlan solution) {
        int hardScore = 0;
        int softScore = 0;

        // 朋友的计算规则
        for (SeatAssignment item : solution.designations) {
            for (Friends group : solution.getFriends()) {
                // 我已经安排了座位，且我是有朋友的。
                if (item.seat != null && group.contains(item.passenger)) {
                    // get neighbor passenger
                    // my seat is
                    int col = item.seat.col;
                    int row = item.seat.row;
                    // 有朋友坐在我的旁边我就高兴，不然不高兴。
                    int minWidth = Integer.MAX_VALUE;
                    for (Passenger other : group) {
                        if (item.passenger == other) {
                            continue;
                        }
                        // 查查座位图，看看他们在哪里。
                        for (SeatAssignment dd : solution.designations) {
                            if (dd.passenger == other) {
                                if (dd.seat != null) {
                                    if (dd.seat.row == row) {
                                        minWidth = Math.min(minWidth,
                                                Math.abs(dd.seat.col - col));
                                    } else {
                                    }
                                } else {
                                    // 朋友没有座位，不高兴
                                    // System.out.println(dd.passenger
                                    // + "没有座位，不高兴");
                                    // softScore -= 10;
                                }
                            }
                        }
                    }
                    // 距离我最近的朋友就在我的旁边。
                    if (minWidth == 1) {

                    } else {
                        // System.out.println(item.passenger + "的最近的朋友是"
                        // + minWidth + "不高兴");
                        softScore -= 10;
                    }
                }
            }
        }

        // 原座位喜好，如果是原座位最好，不然同排，不然，同类型座位
        for (SeatAssignment item : solution.designations) {
            if (item.seat != null) {
                if (item.passenger.bookedSeat.row == item.seat.row) {
                    // Good!.
                } else if (item.seat.row > item.passenger.bookedSeat.row) {
                    // that's ok
                } else {
                    softScore -= 2;
                }
            }
        }

        // 座位喜好
        for (SeatAssignment item : solution.designations) {
            if (item.seat == null) {
                softScore--;
            }
            if (item.seat != null) {
                if (item.passenger.bookedSeat.feature == SeatFeature.安全出口) {
                    if (item.seat.row == 31) {
                        softScore--;
                    } else if (item.passenger.bookedSeat.feature != item.seat.feature) {
                        softScore -= 2;
                    }
                } else {
                    if (item.passenger.bookedSeat.feature != item.seat.feature) {
                        softScore--;
                    }
                }
            }
        }

        // 检查是否占用了相同的座位
        Set<Seat> set = new HashSet<Seat>(solution.seats.size());
        for (SeatAssignment item : solution.designations) {
            // true if this set did not already contain the specified element
            if (set.add(item.seat)) {

            } else {
                hardScore--;
            }
        }
        return HardSoftScore.valueOf(hardScore, softScore);
    }
}
