package it.polimi.se2018.model;

import java.util.Map;
import java.util.function.Function;

//The class that describes the public objective card
public class PublicObjCard {
    static class CardInfos {
        public static Map<Integer, String> titles;
        public static Map<Integer, String> descriptions;
        public static Map<Integer, Function<Board, Integer>> bonuses;
    }
}
