/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed prevPoint in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.devit.demo.planner;

import static cn.devit.demo.planner.SeatFeature.*;
import static cn.devit.demo.planner.Price.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

public class Main {

    public static void main(String[] args) {
        // Build the Solver
        SolverFactory solverFactory = SolverFactory.createFromXmlFile(new File(
                "src/main/resources/solver.xml"));
        Solver solver = solverFactory.buildSolver();
        // TODO create module.
        SeatPlan seatPlan = new SeatPlan();

        List<Seat> newSeats = new ArrayList<Seat>(10);

        // 新的座位
        newSeats.add(new Seat(31, 1, 窗口, Y600));
        newSeats.add(new Seat(31, 2, 中间, Y600));
        newSeats.add(new Seat(31, 3, 过道, Y600));
        newSeats.add(new Seat(31, 4, 过道, Y600));
        newSeats.add(new Seat(31, 5, 中间, Y600));
        newSeats.add(new Seat(31, 6, 过道, Y600));
        newSeats.add(new Seat(31, 7, 过道, Y600));
        newSeats.add(new Seat(31, 8, 中间, Y600));
        newSeats.add(new Seat(31, 9, 窗口, Y600));

        newSeats.add(new Seat(41, 1, 安全出口, Y600));
        newSeats.add(new Seat(41, 2, 中间, Y600));
        newSeats.add(new Seat(41, 3, 过道, Y600));
        newSeats.add(new Seat(41, 4, 过道, Y600));
        newSeats.add(new Seat(41, 5, 中间, Y600));
        newSeats.add(new Seat(41, 6, 过道, Y600));
        newSeats.add(new Seat(41, 7, 过道, Y600));
        newSeats.add(new Seat(41, 8, 中间, Y600));
        newSeats.add(new Seat(41, 9, 窗口, Y600));

        seatPlan.setSeats(newSeats);

        List<Passenger> 旅客列表 = new ArrayList<Passenger>(10);
        旅客列表.add(new Passenger("张三", new Seat(31, 1, 窗口, Y600)));
        旅客列表.add(new Passenger("李四", new Seat(31, 2, 过道, Y600)));
        旅客列表.add(new Passenger("王五", new Seat(31, 3, 窗口, Y600)));
        旅客列表.add(new Passenger("马六", new Seat(41, 1, 安全出口, Y600)));
        旅客列表.add(new Passenger("马七", new Seat(41, 2, 过道, Y600)));
        旅客列表.add(new Passenger("马八", new Seat(41, 3, 安全出口, Y600)));

        List<Friends> 朋友 = new ArrayList<Friends>(10);
        Friends f;
        f = new Friends();
        朋友.add(f);
        f.add(旅客列表.get(0));
        f.add(旅客列表.get(1));
        f.add(旅客列表.get(2));

        f = new Friends();
        朋友.add(f);
        f.add(旅客列表.get(3));
        f.add(旅客列表.get(4));
        f.add(旅客列表.get(5));

        seatPlan.setPassengers(旅客列表);

        List<SeatAssignment> list3 = new ArrayList<SeatAssignment>(10);
        list3.add(new SeatAssignment(旅客列表.get(0), newSeats.get(0)));
        list3.add(new SeatAssignment(旅客列表.get(1), newSeats.get(1)));
        list3.add(new SeatAssignment(旅客列表.get(2), newSeats.get(2)));
        list3.add(new SeatAssignment(旅客列表.get(3), newSeats.get(3)));
        list3.add(new SeatAssignment(旅客列表.get(4), newSeats.get(4)));
        list3.add(new SeatAssignment(旅客列表.get(5), newSeats.get(5)));

        seatPlan.setDesignations(list3);
        seatPlan.setFriends(朋友);

        // seatPlan.setScore(SimpleScore.valueOf(-100));

        // Solve the problem
        solver.solve(seatPlan);
        SeatPlan plan = (SeatPlan) solver.getBestSolution();

        // Display the result
        System.out.println("\nSolved seats:\n" + toDisplayString(plan));
//        没有朋友关系的前提下计算得到的一种结果。
//        张三 book seat(31排1号,窗口,Y600),act seat(31排1号,窗口,Y600) now @seat(31排1号,窗口,Y600)
//        李四 book seat(31排2号,过道,Y600),act seat(31排1号,窗口,Y600) now @seat(41排6号,过道,Y600)
//        王五 book seat(31排3号,窗口,Y600),act seat(31排1号,窗口,Y600) now @seat(31排9号,窗口,Y600)
//        马六 book seat(41排1号,安全出口,Y600),act seat(31排1号,窗口,Y600) now @seat(41排1号,安全出口,Y600)
//        马七 book seat(41排2号,过道,Y600),act seat(31排1号,窗口,Y600) now @seat(41排7号,过道,Y600)
//        马八 book seat(41排3号,安全出口,Y600),act seat(31排1号,窗口,Y600) now @seat(31排6号,过道,Y600)

        //-43soft
//        张三 book seat(31排1号,窗口,Y600),act seat(31排1号,窗口,Y600) now @seat(31排8号,中间,Y600)
//        李四 book seat(31排2号,过道,Y600),act seat(31排2号,中间,Y600) now @seat(31排7号,过道,Y600)
//        王五 book seat(31排3号,窗口,Y600),act seat(31排3号,过道,Y600) now @seat(31排9号,窗口,Y600)
//        马六 book seat(41排1号,安全出口,Y600),act seat(31排4号,过道,Y600) now @seat(31排5号,中间,Y600)
//        马七 book seat(41排2号,过道,Y600),act seat(31排5号,中间,Y600) now @seat(31排6号,过道,Y600)
//        马八 book seat(41排3号,安全出口,Y600),act seat(31排6号,过道,Y600) now @seat(31排4号,过道,Y600)

        // -3
//        张三 book seat(31排1号,窗口,Y600),act seat(31排1号,窗口,Y600) now @seat(41排8号,中间,Y600)
//        李四 book seat(31排2号,过道,Y600),act seat(31排2号,中间,Y600) now @seat(41排7号,过道,Y600)
//        王五 book seat(31排3号,窗口,Y600),act seat(31排3号,过道,Y600) now @seat(41排9号,窗口,Y600)
//        马六 book seat(41排1号,安全出口,Y600),act seat(31排4号,过道,Y600) now @seat(41排2号,中间,Y600)
//        马七 book seat(41排2号,过道,Y600),act seat(31排5号,中间,Y600) now @seat(41排3号,过道,Y600)
//        马八 book seat(41排3号,安全出口,Y600),act seat(31排6号,过道,Y600) now @seat(41排1号,安全出口,Y600)

//-3添加了同排喜好
//        张三 book seat(31排1号,窗口,Y600),act seat(31排1号,窗口,Y600) now @seat(31排2号,中间,Y600)
//        李四 book seat(31排2号,过道,Y600),act seat(31排2号,中间,Y600) now @seat(31排3号,过道,Y600)
//        王五 book seat(31排3号,窗口,Y600),act seat(31排3号,过道,Y600) now @seat(31排1号,窗口,Y600)
//        马六 book seat(41排1号,安全出口,Y600),act seat(31排4号,过道,Y600) now @seat(41排2号,中间,Y600)
//        马七 book seat(41排2号,过道,Y600),act seat(31排5号,中间,Y600) now @seat(41排3号,过道,Y600)
//        马八 book seat(41排3号,安全出口,Y600),act seat(31排6号,过道,Y600) now @seat(41排1号,安全出口,Y600)


    }

    public static String toDisplayString(SeatPlan plan) {
        StringBuilder sb = new StringBuilder();
        List<SeatAssignment> list = plan.getDesignations();
        for (SeatAssignment item : list) {
            sb.append(item).append("\r\n");
        }
        return sb.toString();
    }

}
