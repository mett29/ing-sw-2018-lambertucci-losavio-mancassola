package it.polimi.se2018.view.gui;

import it.polimi.se2018.model.Card;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;

public class CardController {
    private final Card card;
    @FXML
    private TitledPane cPaneName;
    @FXML
    private TextArea description;

    public CardController(Card card){
        this.card = card;
    }

    public void initialize(){
        cPaneName.setText(card.getTitle());
        description.setText(card.getDescription());
    }

}
