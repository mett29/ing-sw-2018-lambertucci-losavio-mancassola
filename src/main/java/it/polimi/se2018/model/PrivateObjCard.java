package it.polimi.se2018.model;

import java.util.*;

/**
 * This class describes the object PrivateObjCard
 * Implements {@link ObjCard}
 * @author MicheleLambertucci
 */
public class PrivateObjCard implements ObjCard {
    private Color cardColor;

    public PrivateObjCard(Color color) {
        cardColor = color;
    }

    /**
     * @return the PrivateObjCard's color
     */
    public Color getColor() {
        return cardColor;
    }

    /**
     * @return the PrivateObjCard's title
     */
    public String getTitle(){
        return "Sfumature " + CardInfos.colorNames.get(cardColor);
    }

    /**
     * @return the PrivateObjCard's description
     */
    public String getDescription(){
        return "Somma dei valori su tutti i dadi " + CardInfos.colorNames.get(cardColor);
    }

    /**
     * This method calculate the bonus according to the PrivateObjCard
     * @param board Board to be judged
     * @return the bonus value
     */
    public int getBonus(Board board){
        int sum = 0;
        for(Cell cell : board){
            if(!cell.isEmpty() && cell.getDie().getColor() == cardColor) {
                sum += cell.getDie().getValue();
            }
        }
        return sum;
    }

    private static class CardInfos {
        private CardInfos(){}

        static final Map<Color, String> colorNames;

        static {
            Map<Color, String> tmpColorNames = new HashMap<>();
            tmpColorNames.put(Color.RED, "Rosse");
            tmpColorNames.put(Color.YELLOW, "Gialle");
            tmpColorNames.put(Color.GREEN, "Verdi");
            tmpColorNames.put(Color.BLUE, "Blu");
            tmpColorNames.put(Color.PURPLE, "Viola");
            colorNames = Collections.unmodifiableMap(tmpColorNames);
        }
    }
}
