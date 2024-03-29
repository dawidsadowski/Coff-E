package sample;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

import java.util.concurrent.TimeUnit;

public class Controller {
    public Label waterAmountLabel;
    public Label waterAmountTextLabel;

    public enum CoffeeType {BLACK, WHITE}

    final public double MIN_ROTATE_VALUE = 0, MAX_ROTATE_VALUE = 360;
    public double waterAmount = 50;
    public ScrollPane screenScrollPane;
    public ImageView screenFirstLayer;
    public Label coffeeLeftLabel;
    double xOffset, yOffset;
    public AnchorPane backgroundAnchorPane;
    double knobCurrentRotate, mouseCurrentX;
    @FXML
    public ToggleButton powerButton;
    public ImageView textImage;
    public ImageView knobLight;
    public ImageView menuView;
    public ImageView coffeView;

    public int flag = 0;

    private final FadeTransition fadeIn = new FadeTransition(Duration.millis(3000));
    private final FadeTransition fadeIn2 = new FadeTransition(Duration.millis(150));
    private final FadeTransition fadeIn3 = new FadeTransition(Duration.millis(200));

    private int coffeeLeft = 20;
    public CoffeeType coffeeType = null;

    public void initialize() {
        fadeIn.setNode(screenScrollPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.setCycleCount(1);
        fadeIn.setAutoReverse(false);

        fadeIn2.setNode(textImage);
        fadeIn2.setFromValue(0.2);
        fadeIn2.setToValue(1.0);
        fadeIn2.setCycleCount(1);
        fadeIn2.setAutoReverse(false);

        fadeIn3.setNode(knobLight);
        fadeIn3.setFromValue(0.2);
        fadeIn3.setToValue(1.0);
        fadeIn3.setCycleCount(1);
        fadeIn3.setAutoReverse(false);

        setCoffeeLevel(coffeeLeft);
    }

    @FXML
    private void handleButton(ActionEvent event) {
        if (event.getSource().equals(powerButton)) {
            if (flag == 0) {

                (new Thread(() -> {
                    try {
                        TimeUnit.MILLISECONDS.sleep(400);
                        textImage.setImage(new Image(getClass().getResourceAsStream("assets/brand-logo-on.png")));
                        knobLight.setImage(new Image(getClass().getResourceAsStream("assets/volume-indicator-on.png")));
                        playSound();
                        fadeIn2.playFromStart();
                        fadeIn3.playFromStart();
                        screenScrollPane.setVisible(true);
                        fadeIn.playFromStart();
                        TimeUnit.MILLISECONDS.sleep(4000);

                        for (int i = 0; screenScrollPane.getVvalue() < 0.45; i++) {
                            double radians = Math.toRadians(i);
                            TimeUnit.MILLISECONDS.sleep(1);
                            screenScrollPane.setVvalue(screenScrollPane.getVvalue() + Math.sin(radians / 1000));
                        }

                        Platform.runLater(() -> screenScrollPane.setVvalue(screenScrollPane.getVvalue()));

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })).start();

                flag = 1;

            } else {
                textImage.setImage(new Image(getClass().getResourceAsStream("assets/brand-logo-off.png")));
                knobLight.setImage(new Image(getClass().getResourceAsStream("assets/volume-indicator-off.png")));
                screenScrollPane.setVisible(false);
                flag = 0;
                screenScrollPane.setVvalue(0);
            }
        }
    }

    @FXML
    public void chooseBlackCoffee(MouseEvent event) {
        coffeeType = CoffeeType.BLACK;
        handleButton2(event);
    }

    @FXML
    public void chooseWhiteCoffee(MouseEvent event) {
        coffeeType = CoffeeType.WHITE;
        handleButton2(event);
    }

    @FXML
    public void handleButton2(MouseEvent event) {
        Platform.runLater(() -> {
            waterAmountTextLabel.setText("Ilość kawy:");
            waterAmountLabel.setText((int) waterAmount + " ml");
        });

        (new Thread(() -> {
            try {

                TimeUnit.MILLISECONDS.sleep(1000);
                for (int i = 0; screenScrollPane.getVvalue() < 0.9; i++) {
                    double radians = Math.toRadians(i);
                    TimeUnit.MILLISECONDS.sleep(1);
                    screenScrollPane.setVvalue(screenScrollPane.getVvalue() + Math.sin(radians / 1000));
                }

                Platform.runLater(() -> screenScrollPane.setVvalue(screenScrollPane.getVvalue()));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })).start();
    }

    public void handleButton3() {
        (new Thread(() -> {
            try {

                Platform.runLater(() -> {
                    waterAmountTextLabel.setText("Przygotowuję");
                    waterAmountLabel.setText((int) waterAmount + " ml kawy...");
                });

                TimeUnit.MILLISECONDS.sleep(1000);

                if (coffeeType == CoffeeType.BLACK) {
                    changeCoffeViewBlack();
                }
                if (coffeeType == CoffeeType.WHITE) {
                    changeCoffeViewWhite();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })).start();
    }


    public void setRotate(MouseEvent event) {
        knobCurrentRotate = knobLight.getRotate();
        mouseCurrentX = event.getSceneX();
    }

    public void controlKnob(MouseEvent event) {
        double rotation = knobCurrentRotate + (event.getSceneX() - mouseCurrentX);
        rotation = rotation < MIN_ROTATE_VALUE ? MIN_ROTATE_VALUE : Math.min(rotation, MAX_ROTATE_VALUE);
        knobLight.setRotate(rotation);

        waterAmount = 50 + (rotation * 250 / 360);

        waterAmountLabel.setText((int) waterAmount + " ml");
    }

    public void savePositionOffset(MouseEvent event) {
        xOffset = backgroundAnchorPane.getScene().getWindow().getX() - event.getScreenX();
        yOffset = backgroundAnchorPane.getScene().getWindow().getY() - event.getScreenY();
    }

    public void moveWindow() {
        // backgroundAnchorPane.getScene().getWindow().setX(event.getScreenX() + xOffset);
        // backgroundAnchorPane.getScene().getWindow().setY(event.getScreenY() + yOffset);
    }

    public void doNotScroll(ScrollEvent event) {
        event.consume();
    }

    public void playSound() {
        AudioClip note = new AudioClip(this.getClass().getResource("assets/startup_sound.mp3").toString());
        note.play();
    }

    private void setCoffeeLevel(int value) {
        Platform.runLater(() -> coffeeLeftLabel.setText("|".repeat(value)));
    }

    public void changeCoffeViewBlack() {
        (new Thread(() -> {
            try {
                AudioClip note = new AudioClip(this.getClass().getResource("assets/espresso_sound.mp3").toString());
                note.play();
                TimeUnit.MILLISECONDS.sleep(20000);
                coffeView.setImage(new Image(getClass().getResourceAsStream("assets/cup-coffe-black-level-1.png")));
                if (waterAmount > 100) {
                    setCoffeeLevel(--coffeeLeft);
                    TimeUnit.MILLISECONDS.sleep(5000);
                    coffeView.setImage(new Image(getClass().getResourceAsStream("assets/cup-coffe-black-level-2.png")));
                }
                if (waterAmount > 150) {
                    setCoffeeLevel(--coffeeLeft);
                    TimeUnit.MILLISECONDS.sleep(5000);
                    coffeView.setImage(new Image(getClass().getResourceAsStream("assets/cup-coffe-black-level-3.png")));
                }
                if (waterAmount > 200) {
                    setCoffeeLevel(--coffeeLeft);
                    TimeUnit.MILLISECONDS.sleep(5000);
                    coffeView.setImage(new Image(getClass().getResourceAsStream("assets/cup-coffe-black-level-4.png")));
                }
                TimeUnit.MILLISECONDS.sleep(5000);
                note.stop();
                for (int i = 0; screenScrollPane.getVvalue() > 0.45; i++) {
                    double radians = Math.toRadians(i);
                    TimeUnit.MILLISECONDS.sleep(1);
                    screenScrollPane.setVvalue(screenScrollPane.getVvalue() - Math.sin(radians / 1000));
                }

                Platform.runLater(() -> screenScrollPane.setVvalue(screenScrollPane.getVvalue()));

                TimeUnit.MILLISECONDS.sleep(3000);
                coffeView.setImage(new Image(getClass().getResourceAsStream("assets/coffee-empty-cup.png")));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })).start();
    }

    public void changeCoffeViewWhite() {
        (new Thread(() -> {
            try {
                AudioClip note = new AudioClip(this.getClass().getResource("assets/espresso_sound.mp3").toString());
                note.play();
                TimeUnit.MILLISECONDS.sleep(20000);
                coffeView.setImage(new Image(getClass().getResourceAsStream("assets/cup-coffe-white-level-1.png")));
                if (waterAmount > 100) {
                    setCoffeeLevel(--coffeeLeft);
                    TimeUnit.MILLISECONDS.sleep(5000);
                    coffeView.setImage(new Image(getClass().getResourceAsStream("assets/cup-coffe-white-level-2.png")));
                }
                if (waterAmount > 150) {
                    setCoffeeLevel(--coffeeLeft);
                    TimeUnit.MILLISECONDS.sleep(5000);
                    coffeView.setImage(new Image(getClass().getResourceAsStream("assets/cup-coffe-white-level-3.png")));
                }
                if (waterAmount > 200) {
                    setCoffeeLevel(--coffeeLeft);
                    TimeUnit.MILLISECONDS.sleep(5000);
                    coffeView.setImage(new Image(getClass().getResourceAsStream("assets/cup-coffe-white-level-4.png")));
                }
                TimeUnit.MILLISECONDS.sleep(5000);
                note.stop();
                for (int i = 0; screenScrollPane.getVvalue() > 0.45; i++) {
                    double radians = Math.toRadians(i);
                    TimeUnit.MILLISECONDS.sleep(1);
                    screenScrollPane.setVvalue(screenScrollPane.getVvalue() - Math.sin(radians / 1000));
                }

                Platform.runLater(() -> screenScrollPane.setVvalue(screenScrollPane.getVvalue()));

                TimeUnit.MILLISECONDS.sleep(3000);
                coffeView.setImage(new Image(getClass().getResourceAsStream("assets/coffee-empty-cup.png")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        })).start();
    }
}
