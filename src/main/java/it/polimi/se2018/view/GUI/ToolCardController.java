package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.ToolCard;
import it.polimi.se2018.network.client.Client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;

public class ToolCardController {

    private final ToolCard toolCard;
    private final int index;
    private final Client client;

    @FXML
    private TitledPane tcPaneName;
    @FXML
    private Button useBtn;
    @FXML
    private TextArea description;

    public ToolCardController(ToolCard toolCard, int toolcardIndex, Client client) {
        this.toolCard = toolCard;
        this.index = toolcardIndex;
        this.client = client;
    }

    public void initialize(){
        tcPaneName.setText(toolCard.getTitle());
        description.setText(toolCard.getDescription());

        useBtn.setOnMouseClicked(e -> {
            client.activateToolCard(index);
            useBtn.setDisable(true);
        });
    }
}
