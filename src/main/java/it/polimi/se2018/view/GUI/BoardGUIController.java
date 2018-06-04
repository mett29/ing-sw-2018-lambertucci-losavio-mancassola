package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.Board;
import it.polimi.se2018.model.Cell;
import it.polimi.se2018.model.Restriction;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class BoardGUIController {
    @FXML
    private GridPane gridPane;

    private Board board;
    public BoardGUIController(Board board){
        this.board = board;
    }

    @FXML
    public void initialize(){
        for(int y = 0; y < 4; y++){
            for(int x = 0; x < 5; x ++){
                AnchorPane cell = new AnchorPane();
                cell.setStyle("-fx-background-size: cover, auto; -fx-background-repeat: no-repeat; -fx-background-position: center");

                Cell currentCell = board.getCell(x, 3 - y);
                if(currentCell.getRestriction() != null){
                    setCellStyle(cell, currentCell.getRestriction());
                }

                gridPane.add(cell, x, y);
            }
        }
    }

    private void setCellStyle(AnchorPane cell, Restriction restriction){
        String sRestriction = restriction.toString();
        switch(sRestriction){
            case "r":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/red.png')");
                break;
            case "b":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/blue.png')");
                break;
            case "y":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/yellow.png')");
                break;
            case "p":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/purple.png')");
                break;
            case "g":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/green.png')");
                break;
            case "1":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/1.png')");
                break;
            case "2":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/2.png')");
                break;
            case "3":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/3.png')");
                break;
            case "4":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/4.png')");
                break;
            case "5":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/5.png')");
                break;
            case "6":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/6.png')");
                break;
            default:
                // do nothing
        }
    }
}
