package it.polimi.se2018.model;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Function;

import static java.lang.Integer.min;

/**
 * This class describes the object PublicObjCard
 * Implements {@link ObjCard}
 * @author MicheleLambertucci, mett29
 */
public class PublicObjCard implements ObjCard{
    private int id;
    public PublicObjCard(int id){
        this.id = id;
    }

    @Override
    public String getDescription() {
        return CardInfos.descriptions.get(id);
    }

    @Override
    public String getTitle() {
        return CardInfos.titles.get(id);
    }

    /**
     * This method calls the related bonusFunction according to the PublicObjCard
     * @param board Board to be judged
     * @return the calculated score
     */
    @Override
    public int getBonus(Board board) {
        Function<Board, Integer> bonusFunction = CardInfos.bonuses.get(id);
        return bonusFunction.apply(board);
    }

    /**
     * Static class containing all the PublicObjCard's infos
     */
    private static class CardInfos {
        private CardInfos(){}

        static final Map<Integer, String> titles;
        static final Map<Integer, String> descriptions;
        static final Map<Integer, Function<Board, Integer>> bonuses;

        static {
            Map<Integer, String> tmpTitles = new HashMap<>();
            Map<Integer, String> tmpDescriptions = new HashMap<>();
            Map<Integer, Function<Board, Integer>> tmpBonuses = new HashMap<>();

            tmpTitles.put(0, "Colori diversi - Riga");
            tmpDescriptions.put(0, "Righe senza colori ripetuti");
            tmpBonuses.put(0, (Board board) -> {
                int sum = 0;
                for(Cell[] row : board.getRows()){
                    if(differentColors(Arrays.asList(row)) && fullList(Arrays.asList(row))){
                        sum += 6;
                    }
                }
                return sum;
            });
            tmpTitles.put(1, "Colori diversi - Colonna");
            tmpDescriptions.put(1, "Colonne senza colori ripetuti");
            tmpBonuses.put(1, (Board board) -> {
                int sum = 0;
                for(Cell[] column : board.getColumns()){
                    if(differentColors(Arrays.asList(column)) && fullList(Arrays.asList(column))){
                        sum += 5;
                    }
                }
                return sum;
            });
            tmpTitles.put(2, "Sfumature diverse - Riga");
            tmpDescriptions.put(2, "Righe senza sfumature ripetute");
            tmpBonuses.put(2, (Board board) -> {
                int sum = 0;
                for(Cell[] row : board.getRows()){
                    if(differentValues(Arrays.asList(row)) && fullList(Arrays.asList(row))){
                        sum += 5;
                    }
                }
                return sum;
            });
            tmpTitles.put(3, "Sfumature diverse - Colonna");
            tmpDescriptions.put(3, "Colonne senza sfumature ripetute");
            tmpBonuses.put(3, (Board board) -> {
                int sum = 0;
                for(Cell[] column : board.getColumns()){
                    if(differentValues(Arrays.asList(column)) && fullList(Arrays.asList(column))) {
                        sum += 4;
                    }
                }
                return sum;
            });
            tmpTitles.put(4, "Sfumature Chiare");
            tmpDescriptions.put(4, "Set di 1 & 2 ovunque");
            tmpBonuses.put(4, (Board board) -> {
                int n1 = numberOf(1, board);
                int n2 = numberOf(2, board);
                int score = min(n1, n2);
                return score * 2;
            });
            tmpTitles.put(5, "Sfumature Medie");
            tmpDescriptions.put(5, "Set di 3 & 4 ovunque");
            tmpBonuses.put(5,  (Board board) -> {
                int n1 = numberOf(3, board);
                int n2 = numberOf(4, board);
                int score = min(n1, n2);
                return score * 2;
            });
            tmpTitles.put(6, "Sfumature Scure");
            tmpDescriptions.put(6, "Set di 5 & 6 ovunque");
            tmpBonuses.put(6, (Board board) -> {
                int n1 = numberOf(5, board);
                int n2 = numberOf(6, board);
                int score = min(n1, n2);
                return score * 2;
            });
            tmpTitles.put(7, "Sfumature Diverse");
            tmpDescriptions.put(7, "Set di dadi di ogni valore ovunque");
            tmpBonuses.put(7, (Board board) -> {
                ArrayList<Integer> counter = new ArrayList<>();
                for(int i = 0; i < 6; i++){
                    counter.add(i, numberOf(i+1, board));
                }
                int score = Collections.min(counter);
                return score * 5;
            });
            tmpTitles.put(8, "Diagonali colorate");
            tmpDescriptions.put(8, "Numero di dadi dello stesso colore diagonalmente adiacenti");
            tmpBonuses.put(8, (Board board) -> {
                int score = 0;
                for(int x = 0; x < 5; x++){
                    for(int y = 0; y < 4; y++){
                        if(hasDiagonalSameColor(x, y, board)){
                            score++;
                        }
                    }
                }
                return score;
            });
            tmpTitles.put(9, "Varietà di Colore");
            tmpDescriptions.put(9, "Set di dadi di ogni colore ovunque");
            tmpBonuses.put(9, (Board board) -> {
                ArrayList<Integer> counter = new ArrayList<>();
                for(int i = 0; i < Color.values().length; i++){
                    counter.add(i, numberOf(Color.values()[i], board));
                }
                int score = Collections.min(counter);
                return score * 4;
            });
            titles = Collections.unmodifiableMap(tmpTitles);
            descriptions = Collections.unmodifiableMap(tmpDescriptions);
            bonuses = Collections.unmodifiableMap(tmpBonuses);
        }

        /**
         * This method checks if a row or a columns is full of dice
         * @param elems
         * @return true if full
         */
        private static boolean fullList(List<Cell> elems) {
            for (Cell elem : elems)
                if (elem.isEmpty()) return false;
            return true;
        }

        /**
         * Check if there are not two or more dice with the same color in container
         * @param elems List to be checked
         * @return true if there is no duplicate, false otherwise
         */
        private static boolean differentColors(List<Cell> elems) {
            Map<Color, Integer> count = new EnumMap<>(Color.class);
            for(Color color : Color.values()){
                count.put(color, numberOf(color, elems));
            }
            for(int counter : count.values()){
                if(counter > 1) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Check if there is no duplicate value in list
         * @param elems List to be checked
         * @return true if there is no duplicate, false otherwise
         */
        private static boolean differentValues(List<Cell> elems){
            Map<Integer, Integer> count = new HashMap<>();
            for(int i = 1; i < 7; i++){
                count.put(i, numberOf(i, elems));
            }
            for(int counter : count.values()){
                if(counter > 1) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Count occurrencies of dice with a specified value in a container
         * @param value number whose occurrencies are to be counted
         * @param container iterable to count in
         * @return number of occurrencies
         */
        private static int numberOf(int value, Iterable<Cell> container){
            int count = 0;
            for(Cell cell : container){
                if(!cell.isEmpty() && cell.getDie().getValue() == value){
                    count++;
                }
            }
            return count;
        }

        /**
         * Count occurrencies of dice with a specified color in a container
         * @param color color whose occurrencies are to be counted
         * @param container iterable to count in
         * @return number of occurrencies
         */
        private static int numberOf(Color color, Iterable<Cell> container){
            int sum = 0;
            for(Cell cell : container){
                if(!cell.isEmpty() && cell.getDie().getColor() == color) {
                    sum++;
                }
            }
            return sum;
        }

        /**
         * Check if a die on a board has at least one diagonal neighbour of the same color
         * @param x horizontal coordinate of cell to be checked
         * @param y vertical coordinate of cell to be checked
         * @param board board to be checked
         * @return true if ha at least one diagonal neighbour, false otherwise
         */
        private static boolean hasDiagonalSameColor(int x, int y, Board board){
            Die die = board.getDie(x, y);
            if (die != null) {
                Color color = die.getColor();
                for (int[] i : diagonalNeighbours(x, y)) {
                    try {
                        Die iDie = board.getDie(i[0], i[1]);
                        if (iDie != null && color == iDie.getColor()){
                            return true;
                        }
                    } catch (InvalidParameterException e) {
                        // do nothing
                    }
                }
            }
            return false;
        }

        /**
         * Get a list of all diagonal neighbours coordinates
         * @param x horizontal coordinate
         * @param y vertical coordinate
         * @return list of diagonal neighbours coordinate
         */
        private static List<int[]> diagonalNeighbours(int x, int y){
            List<int[]> ret = new ArrayList<>();
            if (x - 1 >= 0 && y - 1 >= 0) ret.add(new int[] {x-1, y-1});
            if (x - 1 >= 0 && y + 1 <= 3) ret.add(new int[] {x-1, y+1});
            if (x + 1 <= 4 && y - 1 >= 0) ret.add(new int[] {x+1, y-1});
            if (x + 1 <= 4 && y + 1 <= 3) ret.add(new int[] {x+1, y+1});
            return ret;
        }
    }
}
