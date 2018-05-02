package it.polimi.se2018.model;

import java.util.*;

//The class that describes the private objective card
public class PrivateObjCard extends ObjCard {
    private Color cardColor;

    public PrivateObjCard(Color color) {
        cardColor = color;
    }

    //gets the color of the card
    public Color getColor() {
        return cardColor;
    }

    public String getTitle(){
        return "Sfumature " + CardInfos.colorNames.get(cardColor);
    }

    public String getDescription(){
        return "Somma dei valori su tutti i dadi " + CardInfos.colorNames.get(cardColor);
    }

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
