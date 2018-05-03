package it.polimi.se2018.model;

//The class that describes the player
public class Player {
    private Board board;
    private int token;
    private String name;
    private PrivateObjCard privateObjCard;
    private ToolCard activatedToolcard;

    public Player() {
        board = null;
        token = 0;
        name = null;
        privateObjCard = null;
        activatedToolcard = null;
    }

    public Player(Board newBoard, int newToken, String newName, PrivateObjCard newPrivateObjCard, ToolCard newActivatedToolcard) {
        board = newBoard;
        token = newToken;
        name = newName;
        privateObjCard = newPrivateObjCard;
        activatedToolcard = newActivatedToolcard;
    }
    
    /**
     * Getter of the board of the player
     * @return the current player board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Getter of the remaining tokens of the player
     * @return the current player tokens
     */
    public int getToken() {
        return token;
    }

    /**
     * Setter of the tokens of the player
     * @param newToken object to set
     */
    public void setToken(int newToken) {
        token = newToken;
    }

    /**
     * Getter of the name chosen by the player
     * @return the current player name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter of the private objective card that the player received at the start of the match
     * @return the current player private objective card
     */
    public PrivateObjCard getPrivateObjCard() {
        return privateObjCard;
    }

    /**
     * Setter of the private objective card of the player
     * @param card object to set
     */
    public void setPrivateObjCard(PrivateObjCard card) {
        privateObjCard = card;
    }

    /**
     * Getter of the toolcard activated by the player
     * @return the current toolcard used
     */
    public ToolCard getActivatedToolcard() {
        return activatedToolcard;
    }

    /**
     * Setter of the toolcard activated by the player
     * @param newActivatedToolcard object to set
     */
    public void setActivatedToolcard(ToolCard newActivatedToolcard) {
        activatedToolcard = newActivatedToolcard;
    }
}
