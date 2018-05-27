package it.polimi.se2018.model;

import org.junit.Before;
import org.junit.Test;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static org.junit.Assert.*;

public class MatchTest {
    private List<Player> players1, players2, players3;

    private Board b1, b2, b3;

    private ToolCard[] toolcards1, toolcards2;

    private PublicObjCard[] pub1, pub2;

    private Observer observer;

    @Before
    public void setUp() throws Exception {
        players1 = new ArrayList<>();
        players3 = new ArrayList<>();


        Player p1 = new Player("Giovanni");
        Player p2 = new Player("Andrea");
        Player p3 = new Player("Davide");

        Restriction[][] pattern1 = new Restriction[4][5];
        Restriction[][] pattern2 = new Restriction[4][5];

        b1 = new Board(pattern1, 3);
        b2 = new Board(pattern2, 5);
        b3 = new Board(pattern2, 5);

        p1.setBoard(b1);
        p2.setBoard(b2);
        p3.setBoard(b3);

        players1.add(p1);
        players1.add(p2);
        players1.add(p3);

        players3.add(p1);

        ToolCard tc1 = new ToolCard(1);
        ToolCard tc2 = new ToolCard(2);
        ToolCard tc3 = new ToolCard(3);

        toolcards1 = new ToolCard[]{tc1, tc2, tc3};
        toolcards2 = new ToolCard[]{tc1};

        PublicObjCard c1 = new PublicObjCard(1);
        PublicObjCard c2 = new PublicObjCard(2);
        PublicObjCard c3 = new PublicObjCard(3);

        pub1 = new PublicObjCard[]{c1, c2, c3};
        pub2 = new PublicObjCard[]{c1};

        observer = (obs, obj) -> { /*do nothing*/ };
    }

    @Test
    public void creationTest() throws Exception {
        new Match(players1, toolcards1, pub1, observer);

        try {
            new Match(players3, toolcards1, pub1, observer);
            fail("`players.size() == 1");
        } catch(InvalidParameterException e){
            // do nothing
        }

        try {
            new Match(players1, toolcards2, pub1, observer);
            fail("`toolcards2.length != 3`");
        } catch(InvalidParameterException e){
            // do nothing
        }

        try {
            new Match(players1, toolcards1, pub2, observer);
            fail("`pub2.length != 3`");
        } catch(InvalidParameterException e){
            // do nothing
        }
    }

    @Test
    public void getSetTest() throws Exception {
        Match m1 = new Match(players1, toolcards1, pub1, observer);

        assertArrayEquals(players1.toArray(), m1.getPlayers().toArray());
        for(Player p : players1){
            assertEquals(p.getBoard(), m1.getBoard(p));
        }

        assertEquals(10, m1.getRoundTracker().getMaxSize());

        assertArrayEquals(pub1, m1.getPublicObjCards());
        assertArrayEquals(toolcards1, m1.getToolCards());

        DiceContainer dp = new DiceContainer(players1.size() + 1);
        m1.setDraftPool(dp);
        assertEquals(dp, m1.getDraftPool());

        Score s0 = new Score();
        m1.setScore(players1.get(0), s0);
        assertEquals(s0, m1.getScore(players1.get(0)));
    }
}