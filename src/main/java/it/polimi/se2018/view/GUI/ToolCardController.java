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
    private final Runnable disableComponents;

    @FXML
    private TitledPane tcPaneName;
    @FXML
    private Button useBtn;
    @FXML
    private TextArea description;

    /**
     * Create a javafx controller for "ToolCardGUI.fxml"
     * @param toolCard The toolcard whose description will be displayed
     * @param toolcardIndex The index of the toolcard that will be activated when pressing "Usa".
     *                      (Needed to craft the toolcard activation request message)
     * @param client Reference to the client (to send the toolcard activation request message to)
     * @param afterActivation Callback that will be triggered once the request is sent (after clicking "Usa")
     */
    public ToolCardController(ToolCard toolCard, int toolcardIndex, Client client, Runnable afterActivation) {
        this.toolCard = toolCard;
        this.index = toolcardIndex;
        this.client = client;
        this.disableComponents = afterActivation;
    }

    public void initialize(){
        tcPaneName.setText(toolCard.getTitle());
        description.setText(toolCard.getDescription());

        useBtn.setOnMouseClicked(e -> {
            client.activateToolCard(index);
            useBtn.setDisable(true);
            disableComponents.run();
        });
    }
}
