package it.polimi.se2018.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class PlacementErrorTest {
    @Test
    public void typeCheckTest() throws Exception {
        PlacementError color = new PlacementError(Flags.COLOR);
        PlacementError value = new PlacementError(Flags.VALUE);
        PlacementError notempty = new PlacementError(Flags.NOTEMPTY);
        PlacementError notonedge = new PlacementError(Flags.EDGE);
        PlacementError none = new PlacementError();

        assertTrue(color.isColorError());
        assertTrue(value.isValueError());
        assertTrue(notempty.isNotEmptyError());
        assertTrue(notonedge.isNotOnEdgeError());
        assertFalse(none.hasError());
    }

}