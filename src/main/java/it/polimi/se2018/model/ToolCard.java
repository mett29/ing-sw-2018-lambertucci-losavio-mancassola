package it.polimi.se2018.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class represents the object ToolCard
 * @author MicheleLambertucci, mett29
 */
public class ToolCard implements Card{
    private int id = 0;

    public ToolCard(int id){
        this.id = id;
    }

    /**
     * Get the color of the toolcard
     * @return object to get
     */
    public Color getColor() {
        return CardInfos.colors.get(id);
    }

    /**
     * Get the id of the toolcard
     * @return object to get
     */
    public int getId() { return this.id; }

    /**
     * Get the description of the toolcard
     * @return object to get
     */
    @Override
    public String getDescription() {
        return CardInfos.descriptions.get(id);
    }

    /**
     * Get the title of the toolcard
     * @return object to get
     */
    @Override
    public String getTitle() {
        return CardInfos.titles.get(id);
    }

    /**
     * Static class containing all the ToolCard's infos
     */
    private static class CardInfos {
        static final Map<Integer, String> titles;
        static final Map<Integer, String> descriptions;
        static final Map<Integer, Color> colors;

        static {
            Map<Integer, String> tmpTitles = new HashMap<>();
            Map<Integer, String> tmpDescriptions = new HashMap<>();
            Map<Integer, Color> tmpColors = new HashMap<>();

            tmpTitles.put(0, "Pinza Sgrossatrice");
            tmpDescriptions.put(0, "Dopo aver scelto un dado, aumenta o diminuisci il valore del dado scelto di 1. Non puoi cambiare un 6 in 1 o un 1 in 6.");
            tmpColors.put(0, Color.PURPLE);

            tmpTitles.put(1, "Pennello per Eglomise");
            tmpDescriptions.put(1, "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di colore. Devi rispettare tutte le altre restrizioni di piazzamento.");
            tmpColors.put(1, Color.BLUE);

            tmpTitles.put(2, "Alesatore per lamina di rame");
            tmpDescriptions.put(2, "Muovi un qualsiasi dado nella tua vetrata ignorando le restrizioni di valore. Devi rispettare tutte le altre restrizioni di piazzamento.");
            tmpColors.put(2, Color.RED);

            tmpTitles.put(3, "Lathekin");
            tmpDescriptions.put(3, "Muovi esattamente due dadi, rispettando tutte le restrizioni di piazzamento.");
            tmpColors.put(3, Color.YELLOW);

            tmpTitles.put(4, "Taglierina circolare");
            tmpDescriptions.put(4, "Dopo aver scelto un dado, scambia quel dado con un dado sul Tracciato dei Round.");
            tmpColors.put(4, Color.GREEN);

            tmpTitles.put(5, "Pennello per Pasta Salda");
            tmpDescriptions.put(5, "Dopo aver scelto un dado, tira nuovamente quel dado. Se non puoi piazzarlo, riponilo nella Riserva.");
            tmpColors.put(5, Color.PURPLE);

            tmpTitles.put(6, "Martelletto");
            tmpDescriptions.put(6, "Tira nuovamente tutti i dadi della Riserva. Questa carta può essere usata solo durante il tuo secondo turno, prima di scegliere il secondo dado.");
            tmpColors.put(6, Color.BLUE);

            tmpTitles.put(7, "Tenaglia a Rotelle");
            tmpDescriptions.put(7, "Dopo il tuo primo turno scegli immediatamente un altro dado. Salta il tuo secondo turno in questo round.");
            tmpColors.put(7, Color.RED);

            tmpTitles.put(8, "Riga in Sughero");
            tmpDescriptions.put(8, "Dopo aver scelto un dado, piazzalo in una casella che non sia adiacente a un altro dado. Devi rispettare tutte le restrizioni di piazzamento.");
            tmpColors.put(8, Color.YELLOW);

            tmpTitles.put(9, "Tampone Diamantato");
            tmpDescriptions.put(9, "Dopo aver scelto un dado, giralo sulla faccia opposta. 6 diventa 1, 5 diventa 2, 4 diventa 3 ecc.");
            tmpColors.put(9, Color.GREEN);

            tmpTitles.put(10, "Diluente per Pasta Salda");
            tmpDescriptions.put(10, "Dopo aver scelto un dado, riponilo nel Sacchetto, poi pescane uno dal Sacchetto. Scegli il valore del nuovo dado e piazzalo, rispettando tutte le restrizioni di piazzamento.");
            tmpColors.put(10, Color.PURPLE);

            tmpTitles.put(11, "Taglierina Manuale");
            tmpDescriptions.put(11, "Muovi fino a due dadi dello stesso colore di un solo dado sul Tracciato dei Round. Devi rispettare tutte le restrizioni di piazzamento.");
            tmpColors.put(11, Color.BLUE);

            titles = Collections.unmodifiableMap(tmpTitles);
            descriptions = Collections.unmodifiableMap(tmpDescriptions);
            colors = Collections.unmodifiableMap(tmpColors);

        }

    }
}
