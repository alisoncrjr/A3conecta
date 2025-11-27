package Controller;

import Application.App;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.animation.PauseTransition;
import javafx.util.Duration;



public class SplashController {

    @FXML
    private Label welcomeText;

    @FXML
    public void initialize() {

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> {

            App.getNavigationService().showLoginScene();

        });
        delay.play();
    }
}