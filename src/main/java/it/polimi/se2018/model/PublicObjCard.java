package it.polimi.se2018.model;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.Function;

import static java.lang.Integer.min;

//The class that describes the public objective card
public class PublicObjCard extends ObjCard{
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

    @Override
    public int getBonus(Board board) {
        Function<Board, Integer> bonusFunction = CardInfos.bonuses.get(id);
        return bonusFunction.apply(board);
    }


    private static class CardInfos {
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
                    if(differentColors(row)){
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
                    if(differentColors(column)){
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
                    if(differentValues(row)){
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
                    if(differentValues(column)){
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
                    counter.add(i, numberOf(i, board));
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

        private static boolean differentColors(Cell[] elems){
            Map<Color, Integer> count = new HashMap<>();
            for(Cell i : elems){
                if(!i.isEmpty()){
                    Color iColor = i.getDie().getColor();
                    int tmpCount = count.getOrDefault(iColor, 0);
                    count.replace(iColor, tmpCount + 1);
                }
            }
            for(int counter : count.values()){
                if(counter > 1) {
                    return false;
                }
            }
            return true;
        }

        private static boolean differentValues(Cell[] elems){
            Map<Integer, Integer> count = new HashMap<>();
            for(Cell i : elems){
                if(!i.isEmpty()){
                    int iValue = i.getDie().getValue();
                    int tmpCount = count.getOrDefault(iValue, 0);
                    count.replace(iValue, tmpCount + 1);
                }
            }
            for(int counter : count.values()){
                if(counter > 1) {
                    return false;
                }
            }
            return true;
        }

        private static int numberOf(int value, Board board){
            int count = 0;
            for(Cell cell : board){
                if(!cell.isEmpty() && cell.getDie().getValue() == value){
                    count++;
                }
            }
            return count;
        }

        private static int numberOf(Color color, Board board){
            int sum = 0;
            for(Cell cell : board){
                if(!cell.isEmpty() && cell.getDie().getColor() == color) {
                    sum++;
                }
            }
            return sum;
        }

        private static boolean hasDiagonalSameColor(int x, int y, Board board){
            Die die = board.getDie(x, y);
            Color color = die.getColor();
            for(int[] i : diagonalNeighbours(x, y)) {
                try {
                    Die iDie = board.getDie(i[0], i[1]);
                    if (color == iDie.getColor()) {
                        return true;
                    }
                } catch (InvalidParameterException){
                    // do nothing
                }
            }
            return false;
        }

        private static ArrayList<int[]> diagonalNeighbours(int x, int y){
            ArrayList<int[]> ret = new ArrayList<>();
            ret.add(new int[] {x-1, y-1});
            ret.add(new int[] {x-1, y+1});
            ret.add(new int[] {x+1, y-1});
            ret.add(new int[] {x+1, y-1});
            return ret;
        }
    }
}
