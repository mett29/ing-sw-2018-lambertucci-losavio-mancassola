package it.polimi.se2018.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class PlacementErrorTest {
    @Test
    public void typeCheckTest() throws Exception {
        PlacementError color = new PlacementError(Flag.COLOR);
        PlacementError value = new PlacementError(Flag.VALUE);
        PlacementError notempty = new PlacementError(Flag.NOTEMPTY);
        PlacementError notonedge = new PlacementError(Flag.EDGE);
        PlacementError none = new PlacementError();

        assertTrue(color.isColorError());
        assertTrue(value.isValueError());
        assertTrue(notempty.isNotEmptyError());
        assertTrue(notonedge.isNotOnEdgeError());
        assertFalse(none.hasError());
    }

}