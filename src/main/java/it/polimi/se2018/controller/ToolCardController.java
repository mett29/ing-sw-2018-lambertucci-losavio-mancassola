package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;

import java.util.*;
import java.util.function.BiFunction;

import static it.polimi.se2018.model.DiceContainerCoord.asDieCoord;

/**
 * This class represents the logic of all toolcards of the game
 * @author ontech7
 */
class ToolCardController {
    private Match match;
    private int id;
    private Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> operations;
    private List<DieCoord> memory;
    private List<Die> pickedDice;

    ToolCardController(Match match, ToolCard toolCard) {
        this.match = match;
        this.id = toolCard.getId();
        this.memory = new ArrayList<>();
        this.pickedDice = new ArrayList<>();
        this.operations = Operations.get(this.id);
    }

    Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> getOperations() {
        return operations;
    }

    /**
     * Applies the player's move on the first operation of the ops Queue.
     * If the returned state is REPEAT, the operation must be repeated. Poll it otherwise.
     * @param playerMove of the player
     * @return the new state at the end of the operation
     */
    PlayerState handleMove(PlayerMove playerMove) {
        //Applies the first operation of the Queue of BiFunctions based on this toolcard controller and the playerMove of the player.
        PlayerState newState = operations.peek().apply(this, playerMove);

        //If the state returned is not REPEAT, poll the Queue. Otherwise the player must repeat that operation of the Queue.
        if(newState.get() != EnumState.REPEAT) {
            operations.poll();
        }

        return newState;
    }

    /**
     * A static class where belongs all operations of every toolcard.
     */
    private static class Operations{
        private Operations(){}

        static final Map<Integer, Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>>> ops;

        static BiFunction<ToolCardController, PlayerMove, PlayerState> handlePickAndPickFromBoardEmpty = (tcc, pm) -> {
            DieCoord selection = (DieCoord) pm.getMove();
            tcc.memory.add(selection);
            pm.getActor().setPickedDie(selection.get());
            return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY));
        };

        static BiFunction<ToolCardController, PlayerMove, PlayerState> performWithFilter(Flag filter) {
            return (tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                Action a = new Switch(tcc.memory.get(0), tcc.memory.get(1));
                PlacementError err = a.check();
                boolean isOkay = err.hasNoErrorExceptEdgeFilter(EnumSet.of(filter));
                if(!isOkay){
                    tcc.memory.remove(1);
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    a.perform();
                    pm.getActor().setPickedDie(null);
                    pm.getActor().possibleActionsRemove(PossibleAction.ACTIVATE_TOOLCARD);
                    return new PlayerState(EnumState.YOUR_TURN);
                }
            };
        }

        static PlayerState performAndRemovePick(Action action, Player actor){
            action.perform();
            actor.setPickedDie(null);
            actor.possibleActionsRemove(PossibleAction.PICK_DIE);
            actor.possibleActionsRemove(PossibleAction.ACTIVATE_TOOLCARD);
            return new PlayerState(EnumState.YOUR_TURN);
        }

        static {
            Map<Integer, Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>>> tmpOps = new HashMap<>();

            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> queue0 = new LinkedList<>();

            queue0.add((tcc, pm) -> new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL)));
            queue0.add((tcc, pm) -> {
                DieCoord selection = (DieCoord) pm.getMove();
                tcc.memory.add(selection);
                pm.getActor().setPickedDie(selection.get());
                return new PlayerState(EnumState.UPDOWN);
            });
            queue0.add((tcc, pm) -> {
                boolean up = (boolean) pm.getMove();
                Die target = tcc.memory.get(0).get();
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
                Action a = new SetValue(tcc.memory.get(0), targetValue);
                a.perform();
                pm.getActor().setPickedDie(null);
                pm.getActor().possibleActionsRemove(PossibleAction.ACTIVATE_TOOLCARD);
                return new PlayerState(EnumState.YOUR_TURN);
            });

            tmpOps.put(0, queue0);

            //-------------------------------------------------------------------------------------------------------



            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> queue1 = new LinkedList<>();

            queue1.add((tcc, pm) -> new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL)));
            queue1.add(handlePickAndPickFromBoardEmpty);
            queue1.add(performWithFilter(Flag.COLOR));

            tmpOps.put(1, queue1);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> queue2 = new LinkedList<>();

            queue2.add((tcc, pm) -> new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL)));
            queue2.add(handlePickAndPickFromBoardEmpty);
            queue2.add(performWithFilter(Flag.VALUE));

            tmpOps.put(2, queue2);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> queue3 = new LinkedList<>();

            queue3.add((tcc, pm) -> new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL)));
            queue3.add((tcc, pm) -> {
                DieCoord die = (DieCoord) pm.getMove();
                tcc.memory.add(die);
                tcc.pickedDice.add(die.get());
                pm.getActor().setPickedDie(die.get());
                return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY));
            });
            queue3.add((tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                Action a = new Switch(tcc.memory.get(0), tcc.memory.get(1));
                PlacementError err = a.check();
                boolean isOkay = err.hasNoErrorExceptEdge();
                if(!isOkay) {
                    tcc.memory.remove(1);
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    a.perform();
                    pm.getActor().setPickedDie(null);
                    return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL));
                }
            });
            queue3.add((tcc, pm) -> {
                DieCoord die = (DieCoord) pm.getMove();
                if(die.get() == tcc.pickedDice.get(0))
                    return new PlayerState(EnumState.REPEAT);
                tcc.memory.add(die);
                pm.getActor().setPickedDie(die.get());
                return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY));
            });
            queue3.add((tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                Action b = new Switch(tcc.memory.get(2), tcc.memory.get(3));
                PlacementError err = b.check();
                boolean isOkay = err.hasNoErrorExceptEdge();
                if(!isOkay) {
                    tcc.memory.remove(3);
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    b.perform();
                    pm.getActor().setPickedDie(null);
                    pm.getActor().possibleActionsRemove(PossibleAction.ACTIVATE_TOOLCARD);
                    return new PlayerState(EnumState.YOUR_TURN);
                }
            });

            tmpOps.put(3, queue3);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> queue4 = new LinkedList<>();

            queue4.add((tcc, pm) -> new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL)));
            queue4.add((tcc, pm) -> {
                DieCoord selection = (DieCoord) pm.getMove();
                tcc.memory.add(selection);
                pm.getActor().setPickedDie(selection.get());
                return new PickState(EnumSet.of(Component.ROUNDTRACKER), EnumSet.of(CellState.FULL));
            });
            queue4.add((tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                Action a = new Switch(tcc.memory.get(0), tcc.memory.get(1));
                a.perform();
                pm.getActor().setPickedDie(null);
                pm.getActor().possibleActionsRemove(PossibleAction.ACTIVATE_TOOLCARD);
                return new PlayerState(EnumState.YOUR_TURN);
            });

            tmpOps.put(4, queue4);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> queue5 = new LinkedList<>();

            queue5.add((tcc, pm) -> new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL)));
            queue5.add((tcc, pm) -> {
                DieCoord selection = (DieCoord) pm.getMove();
                tcc.memory.add(selection);
                pm.getActor().setPickedDie(selection.get());
                Action a = new Reroll(tcc.memory.get(0));
                a.perform();
                if (pm.getActor().getPossibleActions().contains(PossibleAction.PICK_DIE)) {
                    return new PlayerState(EnumState.YESNO);
                } else {
                    pm.getActor().setPickedDie(null);
                    return new PlayerState(EnumState.YOUR_TURN);
                }
            });
            queue5.add((tcc, pm) -> {
                boolean choice = (boolean) pm.getMove();
                if(choice)
                    return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY));
                pm.getActor().setPickedDie(null);
                return new PlayerState(EnumState.YOUR_TURN);
            });
            queue5.add((tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                Action b = new Switch(tcc.memory.get(0), tcc.memory.get(1));
                PlacementError err = b.check();
                int diceOnBoard = pm.getActor().getBoard().countDice();
                if((diceOnBoard == 0 && err.hasErrorFilter(EnumSet.of(Flag.NEIGHBOURS))) || (diceOnBoard != 0 && !err.hasNoErrorExceptEdge())) {
                    tcc.memory.remove(1);
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    return performAndRemovePick(b, pm.getActor());
                }
            });



            tmpOps.put(5, queue5);



            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> queue6 = new LinkedList<>();

            queue6.add((tcc, pm) -> {
                for(Die die : tcc.match.getDraftPool()){
                    if(die != null)
                        die.randomize();
                }
                pm.getActor().possibleActionsRemove(PossibleAction.ACTIVATE_TOOLCARD);
                return new PlayerState(EnumState.YOUR_TURN);
            });

            tmpOps.put(6, queue6);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> queue7 = new LinkedList<>();

            queue7.add((tcc, pm) -> new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL)));
            queue7.add((tcc, pm) -> {
                DieCoord selection = (DieCoord) pm.getMove();
                tcc.memory.add(selection);
                pm.getActor().setPickedDie(selection.get());
                return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY, CellState.NEAR));
            });
            queue7.add((tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                Action move = new Switch(tcc.memory.get(0), tcc.memory.get(1));
                PlacementError err = move.check();
                int diceOnBoard = pm.getActor().getBoard().countDice();
                if ((diceOnBoard == 0 && err.hasErrorFilter(EnumSet.of(Flag.NEIGHBOURS))) || (diceOnBoard != 0 && !err.hasNoErrorExceptEdge())) {
                    tcc.memory.remove(1);
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    move.perform();
                    Queue<Player> newPlayerQueue = new LinkedList<>();
                    newPlayerQueue.add(pm.getActor());
                    for(Player p : tcc.match.getPlayerQueue()) {
                        if(p != pm.getActor())
                            newPlayerQueue.add(p);
                    }
                    tcc.match.setPlayerQueue(newPlayerQueue);
                    pm.getActor().setPickedDie(null);
                    pm.getActor().possibleActionsRemove(PossibleAction.ACTIVATE_TOOLCARD);
                    return new PlayerState(EnumState.YOUR_TURN);
                }
            });

            tmpOps.put(7, queue7);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> queue8 = new LinkedList<>();

            queue8.add((tcc, pm) -> new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL)));
            queue8.add((tcc, pm) -> {
                DieCoord selection = (DieCoord) pm.getMove();
                tcc.memory.add(selection);
                pm.getActor().setPickedDie(selection.get());
                return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY));
            });
            queue8.add((tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                Action move = new Switch(tcc.memory.get(0), tcc.memory.get(1));
                PlacementError err = move.check();
                if(!err.hasNoErrorExceptEdgeFilter(EnumSet.of(Flag.NEIGHBOURS))) {
                    tcc.memory.remove(1);
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    return performAndRemovePick(move, pm.getActor());
                }
            });

            tmpOps.put(8, queue8);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> queue9 = new LinkedList<>();

            queue9.add((tcc, pm) -> new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL)));
            queue9.add((tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                Action a = new Flip(tcc.memory.get(0));
                a.perform();
                pm.getActor().possibleActionsRemove(PossibleAction.ACTIVATE_TOOLCARD);
                return new PlayerState(EnumState.YOUR_TURN);
            });

            tmpOps.put(9, queue9);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> queue10 = new LinkedList<>();

            queue10.add((tcc, pm) -> new PickState(EnumSet.of(Component.DRAFTPOOL),EnumSet.of(CellState.FULL)));
            queue10.add((tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                Action a = new MoveToDicebag(tcc.memory.get(0), tcc.match);
                a.perform();
                Die extractedDie = tcc.match.extractDie();
                tcc.memory.add(asDieCoord(extractedDie));
                pm.getActor().setPickedDie(tcc.memory.get(1).get());
                return new PlayerState(EnumState.VALUE);
            });
            queue10.add((tcc, pm) -> {
                int value = (int) pm.getMove();
                if(value > 0 && value < 7) {
                    Action a = new SetValue(tcc.memory.get(1), value);
                    a.perform();
                    pm.getActor().setPickedDie(tcc.memory.get(1).get());
                    return new PickState(EnumSet.of(Component.BOARD),EnumSet.of(CellState.EMPTY, CellState.NEAR));
                } else {
                    return new PlayerState(EnumState.REPEAT);
                }
            });
            queue10.add((tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                Action move = new Switch(tcc.memory.get(1), tcc.memory.get(2));
                PlacementError err = move.check();
                int diceOnBoard = pm.getActor().getBoard().countDice();
                if((diceOnBoard == 0 && err.hasErrorFilter(EnumSet.of(Flag.NEIGHBOURS))) || (diceOnBoard != 0 && !err.hasNoErrorExceptEdge())) {
                    tcc.memory.remove(2);
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    move.perform();
                    pm.getActor().setPickedDie(null);
                    pm.getActor().possibleActionsRemove(PossibleAction.PICK_DIE);
                    pm.getActor().possibleActionsRemove(PossibleAction.ACTIVATE_TOOLCARD);
                    return new PlayerState(EnumState.YOUR_TURN);
                }
            });

            tmpOps.put(10, queue10);

            //-------------------------------------------------------------------------------------------------------

            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> queue11 = new LinkedList<>();

            queue11.add((tcc, pm) -> new PickState(EnumSet.of(Component.ROUNDTRACKER), EnumSet.of(CellState.FULL)));
            queue11.add((tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL));
            });
            queue11.add((tcc, pm) -> {
                DieCoord die = (DieCoord) pm.getMove();
                if(die.get().getColor() != tcc.memory.get(0).get().getColor()) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    tcc.memory.add(die);
                    tcc.pickedDice.add(die.get());
                    pm.getActor().setPickedDie(die.get());
                    return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY, CellState.NEAR));
                }
            });
            queue11.add((tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                Action move = new Switch(tcc.memory.get(1), tcc.memory.get(2));
                PlacementError err = move.check();
                boolean isOkay = err.hasNoErrorExceptEdge();
                if(!isOkay) {
                    tcc.memory.remove(2);
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    move.perform();
                    pm.getActor().setPickedDie(null);
                    return new PlayerState(EnumState.YESNO);
                }
            });
            queue11.add((tcc, pm) -> {
                boolean choice = (boolean) pm.getMove();
                if(choice)
                    return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.FULL));
                return new PlayerState(EnumState.YOUR_TURN);
            });
            queue11.add((tcc, pm) -> {
                DieCoord die = (DieCoord) pm.getMove();
                if(die.get().getColor() != tcc.memory.get(0).get().getColor() || die.get() == tcc.pickedDice.get(0)) {
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    tcc.memory.add(die);
                    pm.getActor().setPickedDie(die.get());
                    return new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY, CellState.NEAR));
                }
            });
            queue11.add((tcc, pm) -> {
                tcc.memory.add((DieCoord) pm.getMove());
                Action move = new Switch(tcc.memory.get(3), tcc.memory.get(4));
                PlacementError err = move.check();
                boolean isOkay = err.hasNoErrorExceptEdge();
                if(!isOkay) {
                    tcc.memory.remove(4);
                    return new PlayerState(EnumState.REPEAT);
                } else {
                    move.perform();
                    pm.getActor().setPickedDie(null);
                    pm.getActor().possibleActionsRemove(PossibleAction.ACTIVATE_TOOLCARD);
                    return new PlayerState(EnumState.YOUR_TURN);
                }
            });

            tmpOps.put(11, queue11);

            ops = Collections.unmodifiableMap(tmpOps);
        }

        /**
         * @param mapIndex of the HashMap of operations
         * @return a cloned Queue of BiFunctions
         */
        public static Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> get(int mapIndex) {
            Queue<BiFunction<ToolCardController, PlayerMove, PlayerState>> tmpQueue = new LinkedList<>();
            tmpQueue.addAll(ops.get(mapIndex));
            return tmpQueue;
        }
    }
}
