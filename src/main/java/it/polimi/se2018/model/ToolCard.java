package it.polimi.se2018.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//The class that describes the tool card
public class ToolCard implements Card{
    private int id = 0;

    public ToolCard(int id){
        this.id = id;
    }

    public Color getColor() {
        return CardInfos.colors.get(id);
    }

    @Override
    public String getDescription() {
        return CardInfos.descriptions.get(id);
    }

    @Override
    public String getTitle() {
        return CardInfos.titles.get(id);
    }

    private static class CardInfos {
        static final Map<Integer, String> titles;
        static final Map<Integer, String> descriptions;
        static final Map<Integer, Color> colors;

        static {
            Map<Integer, String> tmpTitles = new HashMap<>();
            Map<Integer, String> tmpDescriptions = new HashMap<>();
            Map<Integer, Color> tmpColors = new HashMap<>();

            //TODO initialize CardInfos

            titles = Collections.unmodifiableMap(tmpTitles);
            descriptions = Collections.unmodifiableMap(tmpDescriptions);
            colors = Collections.unmodifiableMap(tmpColors);

        }

    }
}
