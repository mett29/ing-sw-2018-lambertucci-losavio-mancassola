package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class ToolCardControllerTest {
    private Match match;
    private List<Player> players;
    private ToolCard[] toolCards;
    private PublicObjCard[] publicObjCards;
    private Observer obs;
    private Board board;
    private Queue<Player> playerQueue;

    @Before
    public void setUp() throws Exception {
        Restriction[][] pattern1 = new Restriction[4][5];
        pattern1[1][1] = new Restriction(Color.YELLOW);
        pattern1[1][2] = new Restriction(2);
        pattern1[3][3] = new Restriction(4);

        players = new ArrayList<>();
        players.add(new Player("Pino"));
        players.add(new Player("Rino"));
        players.add(new Player("Lino"));

        board = new Board(pattern1, 4);

        players.get(0).setBoard(board);

        toolCards = new ToolCard[3];
        toolCards[0] = new ToolCard(0);
        toolCards[1] = new ToolCard(1);
        toolCards[2] = new ToolCard(6);

        publicObjCards = new PublicObjCard[3];
        publicObjCards[0] = new PublicObjCard(0);
        publicObjCards[1] = new PublicObjCard(1);
        publicObjCards[2] = new PublicObjCard(2);

        playerQueue = new LinkedList<>();
        playerQueue.add(players.get(0));
        playerQueue.add(players.get(1));
        playerQueue.add(players.get(1));
        playerQueue.add(players.get(0));

        obs = (obs, obj) -> { /*do nothing*/ };

        match = new Match(players, toolCards, publicObjCards, obs);

        match.setPlayerQueue(playerQueue);
    }

    @Test
    public void toolCard0_Test_NOREPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, match.getToolCards()[0]);
        DieCoord dice = new DiceContainerCoord(new DiceContainer(1), 0);
        dice.set(new Die(2, Color.RED));

        //PlayerMove random giusto per fare la prima Function
        PlayerMove<Integer> pm1 = new PlayerMove<>(players.get(0), 10);
        pm1.getActor().possibleActionsSetUp();
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove prendo una DieCoord random (l'ho voluta testare con DiceContainerCoord per semplicità)
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(players.get(0), dice);
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.UPDOWN, ps2.get());

        //Alzo di 1 il valore del dado
        PlayerMove<Boolean> pm3 = new PlayerMove<>(players.get(0), true);
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(EnumState.YOUR_TURN, ps3.get());

        assertEquals(3, dice.get().getValue());

        assertEquals(0, tcc.getOperations().size());

        pm1.getActor().possibleActionsSetUp();
    }

    @Test
    public void toolCard0_Test_REPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, match.getToolCards()[0]);
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        DieCoord dice = new DiceContainerCoord(new DiceContainer(1), 0);
        dice.set(new Die(6, Color.RED));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove prendo una DieCoord random (l'ho voluta testare con DiceContainerCoord per semplicità) -> Vado in stato UPDOWN
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, dice);
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.UPDOWN, ps2.get());

        //Alzo di 1 il valore del dado -> ERRORE -> Vado in stato REPEAT
        PlayerMove<Boolean> pm3 = new PlayerMove<>(currentPlayer, true);
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(EnumState.REPEAT, ps3.get());

        //Abbasso di 1 il valore del dado -> FINE QUEUE azioni -> Vado in stato IDLE
        PlayerMove<Boolean> pm4 = new PlayerMove<>(currentPlayer, false);
        PlayerState ps4 = tcc.handleMove(pm4);

        assertEquals(EnumState.YOUR_TURN, ps4.get());

        assertEquals(5, dice.get().getValue());

        assertEquals(0, tcc.getOperations().size());

        pm1.getActor().possibleActionsSetUp();
    }

    @Test
    public void toolCard1_Test_NOREPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, new ToolCard(1));
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        currentPlayer.getBoard().setDie(1, 0, new Die(3, Color.YELLOW));
        currentPlayer.getBoard().setDie(1, 2, new Die(2, Color.RED));
        currentPlayer.getBoard().setDie(2, 0, new Die(4, Color.BLUE));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove seleziono il dado dalla board -> Vado in stato PICK
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 1, 0));
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.PICK, ps2.get());

        //PlayerMove dove seleziono la cella vuota dalla board -> TERMINO -> Vado in stato YOUR_TURN
        PlayerMove<DieCoord> pm3 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 1, 1));
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(EnumState.YOUR_TURN, ps3.get());
    }

    @Test
    public void toolCard2_Test_NOREPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, new ToolCard(2));
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        currentPlayer.getBoard().setDie(1, 0, new Die(3, Color.YELLOW));
        currentPlayer.getBoard().setDie(1, 3, new Die(2, Color.RED));
        currentPlayer.getBoard().setDie(2, 0, new Die(4, Color.BLUE));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove seleziono il dado dalla board -> Vado in stato PICK
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 1, 0));
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.PICK, ps2.get());

        //PlayerMove dove seleziono la cella vuota dalla board -> TERMINO -> Vado in stato YOUR_TURN
        PlayerMove<DieCoord> pm3 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 1, 2));
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(EnumState.YOUR_TURN, ps3.get());
    }

    @Test
    public void toolCard3_Test_NOREPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, new ToolCard(3));
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        currentPlayer.getBoard().setDie(1, 0, new Die(3, Color.YELLOW));
        currentPlayer.getBoard().setDie(1, 3, new Die(2, Color.RED));
        currentPlayer.getBoard().setDie(2, 0, new Die(4, Color.BLUE));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove seleziono il dado dalla board -> Vado in stato PICK
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 2, 0));
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.PICK, ps2.get());

        //PlayerMove dove seleziono la cella vuota dalla board -> Vado in stato PICK
        PlayerMove<DieCoord> pm3 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 0, 0));
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(EnumState.PICK, ps3.get());

        //PlayerMove dove seleziono il dado dalla board -> Vado in stato PICK
        PlayerMove<DieCoord> pm4 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 1, 3));
        PlayerState ps4 = tcc.handleMove(pm4);

        assertEquals(EnumState.PICK, ps4.get());

        //PlayerMove dove seleziono la cella vuota dalla board -> TERMINO -> Vado in stato YOUR_TURN
        PlayerMove<DieCoord> pm5 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 2, 0));
        PlayerState ps5 = tcc.handleMove(pm5);

        assertEquals(EnumState.YOUR_TURN, ps5.get());
    }

    @Test
    public void toolCard4_Test_NOREPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, new ToolCard(4));
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        DiceContainer roundtracker = new DiceContainer(10);
        roundtracker.insert(new Die(5, Color.RED));

        DiceContainer draftpool = new DiceContainer(5);
        draftpool.insert(new Die(4, Color.BLUE));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove seleziono il dado dalla draftpool -> Vado in stato PICK
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(draftpool, 0));
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.PICK, ps2.get());

        //PlayerMove dove seleziono il dado dal roundtracker -> Vado in stato PICK
        PlayerMove<DieCoord> pm3 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(roundtracker, 0));
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(EnumState.YOUR_TURN, ps3.get());

        System.out.println(roundtracker.getDie(0));
        System.out.println(draftpool.getDie(0));
    }

    @Test
    public void toolCard5_Test_NOREPEAT_End() throws Exception {
        ToolCardController tcc = new ToolCardController(match, new ToolCard(5));
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        DiceContainer draftpool = new DiceContainer(5);
        draftpool.insert(new Die(4, Color.BLUE));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove seleziono il dado dalla draftpool -> Vado in stato PICK
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(draftpool, 0));
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.YESNO, ps2.get());

        System.out.println(draftpool.getDie(0));

        //PlayerMove dove decido di non posizionare il dado -> TERMINO -> Vado in stato YOUR_TURN
        PlayerMove<Boolean> pm3 = new PlayerMove<>(currentPlayer, false);
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(EnumState.YOUR_TURN, ps3.get());

        System.out.println(draftpool.getDie(0));
    }

    @Test
    public void toolCard5_Test_NOREPEAT_NotEnd() throws Exception {
        ToolCardController tcc = new ToolCardController(match, new ToolCard(5));
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        currentPlayer.getBoard().setDie(0,1, new Die(2, Color.RED));

        DiceContainer draftpool = new DiceContainer(5);
        draftpool.insert(new Die(4, Color.BLUE));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove seleziono il dado dalla draftpool -> Vado in stato PICK
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(draftpool, 0));
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.YESNO, ps2.get());

        System.out.println(draftpool.getDie(0));

        //PlayerMove dove decido di non posizionare il dado -> TERMINO -> Vado in stato YOUR_TURN
        PlayerMove<Boolean> pm3 = new PlayerMove<>(currentPlayer, true);
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(EnumState.PICK, ps3.get());

        //PlayerMove dove seleziono il dado dal roundtracker -> Vado in stato PICK
        PlayerMove<DieCoord> pm4 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 0, 2));
        PlayerState ps4 = tcc.handleMove(pm4);

        assertEquals(EnumState.YOUR_TURN, ps4.get());
    }

    @Test
    public void toolCard6_Test_NOREPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, match.getToolCards()[2]);
        DiceContainer draftpool = new DiceContainer(5);

        draftpool.insert(new Die(2, Color.BLUE));
        draftpool.insert(new Die(3, Color.BLUE));
        draftpool.insert(new Die(4, Color.GREEN));
        draftpool.insert(new Die(5, Color.YELLOW));
        draftpool.insert(new Die(6, Color.RED));

        match.setDraftPool(draftpool);

        PlayerMove<ToolCard> playerMove = new PlayerMove<>(players.get(0), match.getToolCards()[2]);
        playerMove.getActor().possibleActionsSetUp();
        PlayerState ps = tcc.handleMove(playerMove);

        assertEquals(EnumState.YOUR_TURN, ps.get());

        System.out.println(draftpool.getDie(0));
        System.out.println(draftpool.getDie(1));
        System.out.println(draftpool.getDie(2));
        System.out.println(draftpool.getDie(3));
        System.out.println(draftpool.getDie(4));

        System.out.println(playerMove.getActor().getPossibleActions());

        playerMove.getActor().possibleActionsSetUp();
    }

    @Test
    public void toolCard7_Test_NOREPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, new ToolCard(7));
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        currentPlayer.getBoard().setDie(1, 1, new Die(3, Color.YELLOW));

        DiceContainer draftpool = new DiceContainer(5);
        draftpool.insert(new Die(4, Color.BLUE));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove seleziono il dado dalla board -> Vado in stato PICK
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(draftpool, 0));
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(ps2.get(),EnumState.PICK);

        //PlayerMove dove seleziono la cella vuota dalla board -> TERMINO -> Vado in stato YOUR_TURN
        PlayerMove<DieCoord> pm3 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 1, 2));
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(EnumState.YOUR_TURN, ps3.get());

        System.out.println(match.getPlayerQueue());
    }

    @Test
    public void toolCard8_Test_NOREPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, new ToolCard(8));
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        currentPlayer.getBoard().setDie(1, 1, new Die(3, Color.YELLOW));

        DiceContainer draftpool = new DiceContainer(5);
        draftpool.insert(new Die(4, Color.BLUE));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove seleziono il dado dalla board -> Vado in stato PICK
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(draftpool, 0));
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.PICK, ps2.get());

        //PlayerMove dove seleziono la cella vuota dalla board -> TERMINO -> Vado in stato YOUR_TURN
        PlayerMove<DieCoord> pm3 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 1, 2));
        PlayerState ps3 = tcc.handleMove(pm3);

        //TODO: Restrizioni scritte con x e y invertite

        assertEquals(EnumState.YOUR_TURN, ps3.get());
    }

    @Test
    public void toolCard9_Test_NOREPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, new ToolCard(9));
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        DiceContainer draftpool = new DiceContainer(5);
        draftpool.insert(new Die(4, Color.BLUE));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove seleziono il dado dalla draftpool -> TERMINO -> Vado in stato YOUR_TURN
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(draftpool, 0));
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.YOUR_TURN, ps2.get());

        System.out.println(draftpool.getDie(0));
    }

    @Test
    public void toolCard10_Test_NOREPEAT() throws Exception {
        ToolCardController tcc = new ToolCardController(match, new ToolCard(10));
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        match.extractDie();

        DiceContainer draftpool = new DiceContainer(5);
        draftpool.insert(new Die(4, Color.BLUE));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove seleziono il dado dalla board -> Vado in stato VALUE
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(draftpool, 0));
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.VALUE, ps2.get());

        //PlayerMove dove seleziono il valore del dado compreso tra 1 e 6 -> Vado in stato PICK
        PlayerMove<Integer> pm3 = new PlayerMove<>(currentPlayer, 5);
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(EnumState.PICK, ps3.get());

        //PlayerMove dove seleziono una cella vuota della board -> TERMINO -> Vado in stato YOUR_TURN
        PlayerMove<DieCoord> pm4 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 0, 0));
        PlayerState ps4 = tcc.handleMove(pm4);

        assertEquals(EnumState.YOUR_TURN, ps4.get());
    }

    @Test
    public void toolCard11_Test_NOREPEAT_End() throws Exception {
        ToolCardController tcc = new ToolCardController(match, new ToolCard(11));
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        currentPlayer.getBoard().setDie(0, 0, new Die(2, Color.RED));
        currentPlayer.getBoard().setDie(1, 1, new Die(3, Color.BLUE));
        currentPlayer.getBoard().setDie(0, 3, new Die(4, Color.RED));

        DiceContainer roundtracker = new DiceContainer(10);
        roundtracker.insert(new Die(4, Color.RED));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove seleziono il dado dal roundtracker -> Vado in stato PICK
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(roundtracker, 0));
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.PICK, ps2.get());

        //PlayerMove dove seleziono un dado della board -> Vado in stato PICK
        PlayerMove<DieCoord> pm3 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 0, 0));
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(EnumState.PICK, ps3.get());

        //PlayerMove dove seleziono una cella vuota della board -> Vado in stato YESNO
        PlayerMove<DieCoord> pm4 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 0, 1));
        PlayerState ps4 = tcc.handleMove(pm4);

        assertEquals(EnumState.YESNO, ps4.get());

        //PlayerMove dove scelgo se selezionare o meno il prossimo dado -> TERMINO -> Vado in stato YOUR_TURN
        PlayerMove<Boolean> pm5 = new PlayerMove<>(currentPlayer, false);
        PlayerState ps5 = tcc.handleMove(pm5);

        assertEquals(EnumState.YOUR_TURN, ps5.get());
    }

    @Test
    public void toolCard11_Test_NOREPEAT_NotEnd() throws Exception {
        ToolCardController tcc = new ToolCardController(match, new ToolCard(11));
        Player currentPlayer = players.get(0);
        currentPlayer.possibleActionsSetUp();

        currentPlayer.getBoard().setDie(0, 0, new Die(2, Color.RED));
        currentPlayer.getBoard().setDie(1, 1, new Die(3, Color.BLUE));
        currentPlayer.getBoard().setDie(0, 3, new Die(4, Color.RED));

        DiceContainer roundtracker = new DiceContainer(10);
        roundtracker.insert(new Die(4, Color.RED));

        //PlayerMove random giusto per fare la prima Function -> Vado in stato PICK
        PlayerMove<Integer> pm1 = new PlayerMove<>(currentPlayer, 10);
        PlayerState ps1 = tcc.handleMove(pm1);

        assertEquals(EnumState.PICK, ps1.get());

        //PlayerMove dove seleziono il dado dal roundtracker -> Vado in stato PICK
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(roundtracker, 0));
        PlayerState ps2 = tcc.handleMove(pm2);

        assertEquals(EnumState.PICK, ps2.get());

        //PlayerMove dove seleziono un dado della board -> Vado in stato PICK
        PlayerMove<DieCoord> pm3 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 0, 0));
        PlayerState ps3 = tcc.handleMove(pm3);

        assertEquals(EnumState.PICK, ps3.get());

        //PlayerMove dove seleziono una cella vuota della board -> Vado in stato YESNO
        PlayerMove<DieCoord> pm4 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 0, 1));
        PlayerState ps4 = tcc.handleMove(pm4);

        assertEquals(EnumState.YESNO, ps4.get());

        //PlayerMove dove scelgo se selezionare o meno il prossimo dado -> TERMINO -> Vado in stato YOUR_TURN
        PlayerMove<Boolean> pm5 = new PlayerMove<>(currentPlayer, true);
        PlayerState ps5 = tcc.handleMove(pm5);

        assertEquals(EnumState.PICK, ps5.get());

        //PlayerMove dove seleziono un dado della board -> Vado in stato PICK
        PlayerMove<DieCoord> pm6 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 0, 3));
        PlayerState ps6 = tcc.handleMove(pm6);

        assertEquals(EnumState.PICK, ps6.get());

        //PlayerMove dove seleziono una cella vuota della board -> Vado in stato YESNO
        PlayerMove<DieCoord> pm7 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 1, 2));
        PlayerState ps7 = tcc.handleMove(pm7);

        assertEquals(EnumState.YOUR_TURN, ps7.get());
    }
}
