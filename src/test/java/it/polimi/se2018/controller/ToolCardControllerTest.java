package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import static org.junit.Assert.*;

public class ToolCardControllerTest {
    private Match match;
    private List<Player> players;
    private ToolCard[] toolCards;
    private PublicObjCard[] publicObjCards;
    private Observer obs;

    @Before
    public void setUp() throws Exception {
        players = new ArrayList<>();
        players.add(new Player("Pino"));
        players.add(new Player("Rino"));
        players.add(new Player("Lino"));

        toolCards = new ToolCard[3];
        toolCards[0] = new ToolCard(0);
        toolCards[1] = new ToolCard(1);
        toolCards[2] = new ToolCard(2);

        publicObjCards = new PublicObjCard[3];
        publicObjCards[0] = new PublicObjCard(0);
        publicObjCards[1] = new PublicObjCard(1);
        publicObjCards[2] = new PublicObjCard(2);

        obs = (obs, obj) -> { /*do nothing*/ };

        match = new Match(players, toolCards, publicObjCards, obs);
    }

    @Test
    public void toolCard0_Test_NOREPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, match.getToolCards()[0]);
        DieCoord dice = new DiceContainerCoord(new DiceContainer(1), 0);
        dice.set(new Die(2, Color.RED));

        //PlayerMove random giusto per fare la prima Function
        PlayerMove<Integer> pm1 = new PlayerMove<>(players.get(0), 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(ps1.get(),EnumState.PICK);

        //PlayerMove dove prendo una DieCoord random (l'ho voluta testare con DiceContainerCoord per semplicità)
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(players.get(0), dice);
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(ps2.get(),EnumState.UPDOWN);

        //Alzo di 1 il valore del dado
        PlayerMove<Boolean> pm3 = new PlayerMove<>(players.get(0), true);
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(ps3.get(),EnumState.IDLE);

        assertEquals(3, dice.get().getValue());

        assertEquals(0, tcc.getOperations().size());
    }

    @Test
    public void toolCard0_Test_REPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, match.getToolCards()[0]);
        DieCoord dice = new DiceContainerCoord(new DiceContainer(1), 0);
        dice.set(new Die(6, Color.RED));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(players.get(0), 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(ps1.get(),EnumState.PICK);

        //PlayerMove dove prendo una DieCoord random (l'ho voluta testare con DiceContainerCoord per semplicità) -> Vado in stato UPDOWN
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(players.get(0), dice);
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(ps2.get(),EnumState.UPDOWN);

        //Alzo di 1 il valore del dado -> ERRORE -> Vado in stato REPEAT
        PlayerMove<Boolean> pm3 = new PlayerMove<>(players.get(0), true);
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(ps3.get(),EnumState.REPEAT);

        //Abbasso di 1 il valore del dado -> FINE QUEUE azioni -> Vado in stato IDLE
        PlayerMove<Boolean> pm4 = new PlayerMove<>(players.get(0), false);
        PlayerState ps4 = tcc.handleMove(pm4);

        assertEquals(ps4.get(),EnumState.IDLE);

        assertEquals(5, dice.get().getValue());

        assertEquals(0, tcc.getOperations().size());
    }
}
