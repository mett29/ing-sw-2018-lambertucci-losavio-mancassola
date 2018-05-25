package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;
import it.polimi.se2018.utils.Extractor;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static it.polimi.se2018.model.DiceContainerCoord.asDieCoord;

public class ToolCardController {
    private Match match;
    private int id;
    private Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> operations;
    private List<DieCoord> memory;

    ToolCardController(Match match, ToolCard toolCard) {
        this.match = match;
        this.id = toolCard.getId();
        this.memory = new ArrayList<>();
        //TODO: this.operations = Operations.ops.get(this.id).clone();
    }

    /**
     * Applies the player's move on the first operation of the ops Queue.
     * If the returned state is REPEAT, the operation must be repeated. Poll it otherwise.
     * @param playerMove of the player
     * @return the new state at the end of the operation
     */
    PlayerState handleMove(PlayerMove playerMove) {
        PlayerState newState = operations.peek().apply(memory, playerMove);

        if(newState.get() != EnumState.REPEAT) {
            operations.poll();
        }

        return newState;
    }

    /**
     * A static class where belongs all operations of every toolcard.
     */
    private static class Operations{
        static final Map<Integer, Queue<BiFunction<List<DieCoord>, PlayerMove,PlayerState>>> ops;

        static {
            Map<Integer, Queue<BiFunction<List<DieCoord>, PlayerMove,PlayerState>>> tmpOps = new HashMap<>();

            Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> queue0 = new PriorityQueue<>(3);

            queue0.add((memory, pm) -> new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL)));
            queue0.add((memory, pm) -> {
                DieCoord selection = (DieCoord) pm.getMove();
                memory.add(selection);
                return new PlayerState(EnumState.UPDOWN);
            });
            queue0.add((memory, pm) -> {
                boolean up = (boolean) pm.getMove();
                Die target = memory.get(0).get();
                int targetValue = target.getValue();
                if(up){
                    if(targetValue == 6){
                        return new PlayerState(EnumState.REPEAT);
                    }
                    targetValue++;
                } else {
                    if(targetValue == 1){
                        return new PlayerState(EnumState.REPEAT);
                    }
                    targetValue--;
                }
                Action a = new SetValue(memory.get(0), targetValue);
                a.perform();
                return new PlayerState(EnumState.IDLE);
            });

            tmpOps.put(0, queue0);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> queue1 = new PriorityQueue<>(3);
            queue1.add((memory, pm) -> new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL)));
            queue1.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY));
            });
            queue1.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                Action a = new Switch(memory.get(0), memory.get(1));
                PlacementError err = a.check();
                boolean isOkay = !err.hasErrorFilter(EnumSet.of(Flags.COLOR, Flags.EDGE));
                if(!isOkay){
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    a.perform();
                    return new PlayerState(EnumState.IDLE);
                }
            });

            tmpOps.put(1, queue1);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> queue2 = new PriorityQueue<>(3);
            queue2.add((memory, pm) -> new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL)));
            queue2.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY));
            });
            queue2.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                Action a = new Switch(memory.get(0), memory.get(1));
                PlacementError err = a.check();
                boolean isOkay = !err.hasErrorFilter(EnumSet.of(Flags.VALUE, Flags.EDGE));
                if(!isOkay){
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    a.perform();
                    return new PlayerState(EnumState.IDLE);
                }
            });

            tmpOps.put(2, queue2);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> queue3 = new PriorityQueue<>(6);
            queue3.add((memory, pm) -> new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL)));
            queue3.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY));
            });
            queue3.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                Action a = new Switch(memory.get(0), memory.get(1));
                PlacementError err = a.check();
                boolean isOkay = !err.hasError();
                if(!isOkay) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    a.perform();
                    return new PlayerState(EnumState.IDLE);
                }
            });
            queue3.add((memory, pm) -> new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL)));
            queue3.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY));
            });
            queue3.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                Action b = new Switch(memory.get(2), memory.get(3));
                PlacementError err = b.check();
                boolean isOkay = !err.hasError();
                if(!isOkay) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    b.perform();
                    return new PlayerState(EnumState.IDLE);
                }
            });

            tmpOps.put(3, queue3);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> queue4 = new PriorityQueue<>(3);
            queue4.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL));
            });
            queue4.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.ROUNDTRACKER), EnumSet.of(CellState.FULL));
            });
            queue4.add((memory, pm) -> {
                Action a = new Switch(memory.get(0), memory.get(1));
                PlacementError err = a.check();
                boolean isOkay = !err.hasError();
                if(!isOkay) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    a.perform();
                    return new PlayerState(EnumState.IDLE);
                }
            });

            tmpOps.put(4, queue4);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> queue5 = new PriorityQueue<>(2);
            queue5.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL));
            });
            queue5.add((memory, pm) -> {
                Action a = new Reroll(memory.get(0));
                PlacementError err = a.check();
                boolean isOkay = !err.hasError();
                if(!isOkay) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    a.perform();
                    return new PlayerState(EnumState.IDLE);
                }
            });

            tmpOps.put(5, queue5);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> queue6 = new PriorityQueue<>(1);
            queue6.add((memory, pm) -> {
                for(DieCoord die : (DieCoord[])pm.getMove()){
                    Action a = new Reroll(die);
                    a.perform();
                }
                return new PlayerState(EnumState.IDLE);
            });

            tmpOps.put(6, queue6);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> queue7 = new PriorityQueue<>(2);
            queue7.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL));
            });
            queue7.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                Action move = new Switch(memory.get(0), memory.get(1));
                PlacementError err = move.check();
                if (!err.hasNoErrorExceptEdge()) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    move.perform();
                    return new PlayerState(EnumState.IDLE);
                }
            });

            tmpOps.put(7, queue7);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> queue8 = new PriorityQueue<>(2);
            queue8.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL));
            });
            queue8.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                Action move = new Switch(memory.get(0), memory.get(1));
                PlacementError err = move.check();
                boolean isOkay = !err.hasErrorFilter(EnumSet.of(Flags.NEIGHBOURS));
                if(!isOkay) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    move.perform();
                    return new PlayerState(EnumState.IDLE);
                }
            });

            tmpOps.put(8, queue8);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> queue9 = new PriorityQueue<>(2);
            queue9.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL));
            });
            queue9.add((memory, pm) -> {
                Action a = new Flip(memory.get(0));
                PlacementError err = a.check();
                boolean isOkay = !err.hasError();
                if(!isOkay) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    a.perform();
                    return new PlayerState(EnumState.IDLE);
                }
            });

            tmpOps.put(9, queue9);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> queue10 = new PriorityQueue<>(4);
            queue10.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.DRAFTPOOL),EnumSet.of(CellState.FULL));
            });
            queue10.add((memory, pm) -> {
                Action a = new MoveToDicebag(memory.get(0), match);
                PlacementError err = a.check();
                boolean isOkay = !err.hasError();
                if(!isOkay) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    a.perform();
                    memory.add(asDieCoord((Die)pm.getMove()));
                    return new PickState(EnumSet.of(Component.DICEBAG), EnumSet.of(CellState.FULL));
                }
            });
            queue10.add((memory, pm) -> {
                int value = (int) pm.getMove();
                if(value > 0 && value < 7) {
                    Action a = new SetValue(memory.get(0), value);
                    a.perform();
                    return new PlayerState(EnumState.UPDOWN);
                } else {
                    return new PlayerState(EnumState.REPEAT);
                }
            });
            queue10.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                Action move = new Switch(memory.get(0), memory.get(1));
                PlacementError err = move.check();
                boolean isOkay = !err.hasError();
                if(!isOkay) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    move.perform();
                    return new PlayerState(EnumState.IDLE);
                }
            });

            tmpOps.put(10, queue10);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> queue11 = new PriorityQueue<>(2);
            queue11.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.ROUNDTRACKER), EnumSet.of(CellState.FULL));
            });
            queue11.add((memory, pm) -> {
                DieCoord die = (DieCoord) pm.getMove();
                if(die.get().getColor() != memory.get(0).get().getColor()) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    memory.add(die);
                    return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL));
                }
            });
            queue11.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                Action move = new Switch(memory.get(1), memory.get(2));
                PlacementError err = move.check();
                boolean isOkay = !err.hasError();
                if(!isOkay) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    return new PlayerState(EnumState.NEXT);
                }
            });
            queue11.add((memory, pm) -> {
                DieCoord die = (DieCoord) pm.getMove();
                if(die.get().getColor() != memory.get(0).get().getColor()) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    memory.add(die);
                    return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL));
                }
            });
            queue11.add((memory, pm) -> {
                memory.add((DieCoord) pm.getMove());
                Action move = new Switch(memory.get(3), memory.get(4));
                PlacementError err = move.check();
                boolean isOkay = !err.hasError();
                if(!isOkay) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    return new PlayerState(EnumState.IDLE);
                }
            });

            tmpOps.put(11, queue11);

            ops = Collections.unmodifiableMap(tmpOps);
        }

        //TODO: clone queue

        /**
         * Gets a specified queue of operations.
         * @param mapIndex toolcard id selected
         * @return a cloned queue of operations.
         */
        Queue<BiFunction<List<DieCoord>, PlayerMove, PlayerState>> get(int mapIndex){
            return cloneQueue(ops.get(mapIndex));
        }

        /**
         * Clone a queue
         * @param q queue to clone
         * @return cloned queue
         */
        private static Queue cloneQueue(Queue q){
            return q;
        }
    }
}
