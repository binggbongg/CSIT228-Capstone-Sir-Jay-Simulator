package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.SubScene;
import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class IntroCutScene extends SubScene {
    private int currentSceneStep = 0;
    private boolean isAnimating = false;

    private Runnable onFinished;

    private ImageView background, ppt, sirJay, skipButton;

    private StackPane sirBubble, treBubble, hertzBubble, coolAndNormalBubble;

    private Text sirText, treText, hertzText, coolAndNormalText;

    private Image imgNormalSir, imgSirJudgy, imgSirTalking;
    private Image imgPptTitle, imgPptDiscussion, imgPptQuiz;
    private Image imgSkipButton;

    private Rectangle topCurtain, bottomCurtain;

    public IntroCutScene(Runnable onFinished){
        this.onFinished = onFinished;

        background = new ImageView(new Image("assets/textures/studentView.png"));
        background.setFitHeight(720);
        background.setFitWidth(1280);

        imgNormalSir = new Image("assets/textures/sir_serato2.png");
        imgSirJudgy = new Image("assets/textures/sir_serato1.png");
        imgSirTalking = new Image("assets/textures/sir_serato_talking.png");

        imgPptTitle = new Image("assets/textures/ppt_title.png");
        imgPptDiscussion = new Image("assets/textures/ppt_discussion.png");
        imgPptQuiz = new Image("assets/textures/ppt_quiz.png");

        imgSkipButton = new Image("assets/textures/button-right.png");

        ppt = new ImageView(imgPptTitle);
        sirJay = new ImageView(imgNormalSir);
        skipButton = new ImageView(imgSkipButton);

        ppt.setFitHeight(180);
        ppt.setFitWidth(323);

        sirJay.setFitHeight(380);
        sirJay.setFitWidth(250);

        skipButton.setFitWidth(70);
        skipButton.setPreserveRatio(true);

        sirText = new Text("How's your day everybody?");
        treText = new Text("Wait sa sir!");
        hertzText = new Text("One-fourth sir?");
        coolAndNormalText = new Text("Cool and Normal!");

        sirBubble = makeSpeechBubble(new Rectangle(200, 50, Color.WHITE), sirText);
        treBubble = makeSpeechBubble(new Rectangle(80, 50, Color.WHITE), treText);
        hertzBubble = makeSpeechBubble(new Rectangle(120, 50, Color.WHITE), hertzText);
        coolAndNormalBubble = makeSpeechBubble(new Rectangle(1200, 100, Color.WHITE), coolAndNormalText);

        ppt.setTranslateX(470);
        ppt.setTranslateY(250);
        ppt.setVisible(false);

        sirJay.setTranslateX(1100);
        sirJay.setTranslateY(340);
        sirJay.setVisible(false);

        sirBubble.setTranslateX(750);
        sirBubble.setTranslateY(180);

        treBubble.setTranslateX(100);
        treBubble.setTranslateY(600);

        hertzBubble.setTranslateX(1000);
        hertzBubble.setTranslateY(600);

        coolAndNormalBubble.setTranslateX(50);
        coolAndNormalBubble.setTranslateY(575);

        skipButton.setTranslateX(1150);
        skipButton.setTranslateY(40);

        topCurtain = new Rectangle(1280, 360, Color.BLACK);
        bottomCurtain = new Rectangle(1280, 360, Color.BLACK);

        topCurtain.setTranslateY(0);
        bottomCurtain.setTranslateY(360);

        getContentRoot().getChildren().addAll(background, ppt, sirJay,  sirBubble, treBubble, hertzBubble, coolAndNormalBubble, skipButton, topCurtain, bottomCurtain);

        skipButton.setOnMouseClicked(e -> {
            FXGL.getAudioPlayer().stopAllMusic();
            FXGL.getAudioPlayer().stopAllSounds();
            onFinished.run();
        });

        this.getInput().addAction(new UserAction("Advance") {
            @Override
            protected void onActionBegin() {
                if (!isAnimating) {
                    advanceScene();
                }
            }
        }, KeyCode.ENTER);

        Music bgMusic = FXGL.getAssetLoader().loadMusic("classroom.wav");
        FXGL.getAudioPlayer().playMusic(bgMusic);
        FXGL.getSettings().setGlobalMusicVolume(0.2);
        advanceScene();
    }

    @Override
    public void onCreate() {
        this.getContentRoot().setFocusTraversable(true);
        this.getContentRoot().requestFocus();
    }

    StackPane makeSpeechBubble(Rectangle shape, Text label){
        shape.setArcWidth(20);
        shape.setArcHeight(20);

        Font customFont = Font.loadFont(
                getClass().getResourceAsStream("/fonts/Jellee-Roman.otf"),
                14
        );

        label.setFont(customFont);
        label.setFill(Color.BLACK);

        StackPane stack = new StackPane(shape, label);
        stack.setVisible(false);
        return stack;
    }

    private void advanceScene(){
        switch (currentSceneStep){
            case 0:
                openCurtains();
                startSirWalk();
                currentSceneStep++;
                break;

            case 1:
                sirBubble.setVisible(true);

                Sound greeting = FXGL.getAssetLoader().loadSound("howsurdayeverybody2.wav");
                FXGL.getAudioPlayer().playSound(greeting);

                currentSceneStep++;
                break;

            case 2:
                sirBubble.setVisible(false);
                coolAndNormalBubble.setVisible(true);

                Sound response = FXGL.getAssetLoader().loadSound("coolandnormal.wav");
                FXGL.getAudioPlayer().playSound(response);

                currentSceneStep++;
                break;

            case 3:
                coolAndNormalBubble.setVisible(false);
                sirText.setText("Good");
                sirBubble.setVisible(true);

                Rectangle goodRect = (Rectangle) sirBubble.getChildren().getFirst();
                goodRect.setWidth(100);

                Sound good = FXGL.getAssetLoader().loadSound("good2.wav");
                FXGL.getAudioPlayer().playSound(good);

                currentSceneStep++;
                break;

            case 4:
                sirText.setText("Okay. Our discussion for today: Trees!");
                sirBubble.setVisible(true);

                Rectangle rect = (Rectangle) sirBubble.getChildren().getFirst();
                rect.setWidth(350);

                Sound trees = FXGL.getAssetLoader().loadSound("trees.wav");
                FXGL.getAudioPlayer().playSound(trees);

                ppt.setVisible(true);
                currentSceneStep++;
                break;

            case 5:
                sirBubble.setVisible(false);
                startLecture();
                break;

            case 6:
                ppt.setImage(imgPptQuiz);
                sirText.setText("Quiz! Get one-fourth sheet of paper.");

                Sound getQuiz = FXGL.getAssetLoader().loadSound("quiz.wav");
                FXGL.getAudioPlayer().playSound(getQuiz);

                sirBubble.setVisible(true);
                Rectangle rect2 = (Rectangle) sirBubble.getChildren().getFirst();
                rect2.setWidth(500);
                currentSceneStep++;
                break;

            case 7:
                sirBubble.setVisible(false);
                treBubble.setVisible(true);

                sirJay.setImage(imgSirJudgy);
                sirJay.setScaleX(-1);

                Sound wait = FXGL.getAssetLoader().loadSound("waitsasir.wav");
                FXGL.getAudioPlayer().playSound(wait);

                currentSceneStep++;
                break;

            case 8:
                treBubble.setVisible(false);
                hertzBubble.setVisible(true);

                sirJay.setScaleX(1);

                Sound question = FXGL.getAssetLoader().loadSound("onefourth.wav");
                FXGL.getAudioPlayer().playSound(question);

                currentSceneStep++;
                break;

            case 9:
                hertzBubble.setVisible(false);
                FXGL.getAudioPlayer().stopAllMusic();
                onFinished.run();
                break;
        }
    }

    private void openCurtains() {
        isAnimating = true;

        TranslateTransition openTop = new TranslateTransition(Duration.seconds(1.7), topCurtain);
        openTop.setToY(-360);

        TranslateTransition openBottom = new TranslateTransition(Duration.seconds(1.7), bottomCurtain);
        openBottom.setToY(720);

        ParallelTransition parallel = new ParallelTransition(openTop, openBottom);
        parallel.setInterpolator(Interpolator.EASE_BOTH);

        parallel.setOnFinished(e -> {
            getContentRoot().getChildren().removeAll(bottomCurtain, topCurtain);

            isAnimating = false;
        });

        parallel.play();
    }

    private void startLecture() {
        isAnimating = true;
        ppt.setImage(imgPptDiscussion);

        Timeline mouthFlicker = new Timeline(
                new KeyFrame(Duration.seconds(0.15), e -> {
                    if (sirJay.getImage() == imgNormalSir) {
                        sirJay.setImage(imgSirTalking);
                        sirJay.setFitHeight(380);
                        sirJay.setFitWidth(250);
                    } else {
                        sirJay.setImage(imgNormalSir);
                        sirJay.setFitHeight(380);
                        sirJay.setFitWidth(250);
                    }
                })
        );
        mouthFlicker.setCycleCount(Animation.INDEFINITE);
        mouthFlicker.play();
        Music discussion = FXGL.getAssetLoader().loadMusic("mii_discussion_boosted.mp3");
        FXGL.getAudioPlayer().playMusic(discussion);

        PauseTransition lectureTimer = new PauseTransition(Duration.seconds(13.0));
        lectureTimer.setOnFinished(e -> {
            mouthFlicker.stop();
            sirJay.setImage(imgNormalSir);
            isAnimating = false;
            currentSceneStep++;
        });

        lectureTimer.play();
    }

    private void startSirWalk(){
        isAnimating = true;

        sirJay.setTranslateX(1100);
        sirJay.setTranslateY(340);

        TranslateTransition bobbing = new TranslateTransition(Duration.seconds(0.2), sirJay);
        bobbing.setFromY(340);
        bobbing.setToY(330);
        bobbing.setCycleCount(Animation.INDEFINITE);
        bobbing.setAutoReverse(true);
        bobbing.play();

        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(e -> {
            sirJay.setVisible(true);
        });
        delay.play();

        TranslateTransition walk = new TranslateTransition(Duration.seconds(3.5), sirJay);
        walk.setFromX(1100);
        walk.setToX(750);
        walk.setInterpolator(Interpolator.EASE_OUT);

        walk.setOnFinished(ev -> {
            bobbing.stop();
            sirJay.setTranslateY(340);
            isAnimating = false;
            advanceScene();
        });

        PauseTransition delay2 = new PauseTransition(Duration.seconds(2.5));
        delay2.setOnFinished(e -> {
            bobbing.play();
            walk.play();
        });
        delay2.play();
    }
}
