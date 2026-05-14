package com.example.csit228capstonesirjaysimulator.scene;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.SubScene;
import com.example.csit228capstonesirjaysimulator.database.UserDatabaseService;
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
    private boolean isAnimating = false, isExiting = false;
    private String professorCourse = UserDatabaseService.getInstance().getCurrentUser().getCourse();

    private Runnable onFinished;

    private ImageView background, ppt, sirJay, skipButton;

    private StackPane sirBubble, treBubble, hertzBubble, coolAndNormalBubble;

    private Text sirText, treText, hertzText, coolAndNormalText, clickEnterText;

    private Image imgNormalSir, imgSirJudgy, imgSirTalking;
    private Image imgPptQuiz;
    private Image daaTitle, daaSlide1, daaSlide2, daaSlide3;
    private Image oopTitle, oopSlide1, oopSlide2, oopSlide3;

    private Timeline pptTimeline;
    private Image imgSkipButton;

    private Rectangle topCurtain, bottomCurtain;

    private Timeline mouthFlicker;
    private PauseTransition lectureTimer;
    private TranslateTransition walk, bobbing;
    private PauseTransition delay, delay2;

    public IntroCutScene(Runnable onFinished){
        this.onFinished = onFinished;

        initUI();

        skipButton.setOnMouseClicked(e -> {cleanUpAudio();});

        this.getInput().addAction(new UserAction("Advance") {
            @Override
            protected void onActionBegin() {
                if (!isAnimating) {
                    advanceScene();
                }
            }
        }, KeyCode.ENTER);

        playMusic("classroom.wav");
        advanceScene();
    }

    private void playMusic(String file) {
        Music music = FXGL.getAssetLoader().loadMusic(file);
        FXGL.getAudioPlayer().playMusic(music);
    }

    private void playSound(String file){
        Sound sound = FXGL.getAssetLoader().loadSound(file);
        FXGL.getAudioPlayer().playSound(sound);
    }

    private void cleanUpAudio(){
        isExiting = true;
        isAnimating = false;

        FXGL.getGameTimer().clear();

        if (mouthFlicker != null) mouthFlicker.stop();
        if (lectureTimer != null) lectureTimer.stop();
        if (walk != null) walk.stop();
        if (bobbing != null) bobbing.stop();
        if (pptTimeline != null) pptTimeline.stop();

        FXGL.getAudioPlayer().stopAllMusic();
        FXGL.getAudioPlayer().stopAllSounds();

        onFinished.run();
    }

    private void initUI(){
        background = new ImageView(new Image("assets/textures/studentView.png"));
        background.setFitHeight(720);
        background.setFitWidth(1280);

        imgNormalSir = new Image("assets/textures/sir_serato2.png");
        imgSirJudgy = new Image("assets/textures/sir_serato1.png");
        imgSirTalking = new Image("assets/textures/sir_serato_talking.png");

        imgPptQuiz = new Image("assets/textures/ppt_quiz.png");

        daaTitle = new Image("assets/textures/img_daa_title.png");
        daaSlide1 = new Image("assets/textures/img_daa_ppt1.png");
        daaSlide2 = new Image("assets/textures/img_daa_ppt2.png");
        daaSlide3 = new Image("assets/textures/img_daa_ppt3.png");

        oopTitle = new Image("assets/textures/img_oop_title.png");
        oopSlide1 = new Image("assets/textures/img_oop_ppt1.png");
        oopSlide2 = new Image("assets/textures/img_oop_ppt2.png");
        oopSlide3 = new Image("assets/textures/img_oop_ppt3.png");

        imgSkipButton = new Image("assets/textures/button-right.png");

        ppt = new ImageView(daaTitle);
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
        coolAndNormalText.setFont(Font.font("Arial", 36));

        clickEnterText = new Text("Click Enter to proceed");
        clickEnterText.setFont(Font.loadFont(
                getClass().getResourceAsStream("/fonts/Jellee-Roman.otf"),
                20));
        clickEnterText.setFill(Color.YELLOW);
        clickEnterText.setVisible(false);
        clickEnterText.setTranslateX(975);
        clickEnterText.setTranslateY(650);

        sirBubble = makeSpeechBubble(new Rectangle(250, 50, Color.WHITE), sirText, 16);
        treBubble = makeSpeechBubble(new Rectangle(100, 50, Color.WHITE), treText, 16);
        hertzBubble = makeSpeechBubble(new Rectangle(140, 50, Color.WHITE), hertzText, 16);
        coolAndNormalBubble = makeSpeechBubble(new Rectangle(1200, 100, Color.WHITE), coolAndNormalText, 36);

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

        getContentRoot().getChildren().addAll(background, ppt, sirJay,  sirBubble, treBubble, hertzBubble, coolAndNormalBubble, skipButton, clickEnterText, topCurtain, bottomCurtain);
    }

    @Override
    public void onCreate() {
        this.getContentRoot().setFocusTraversable(true);
        this.getContentRoot().requestFocus();
    }

    StackPane makeSpeechBubble(Rectangle shape, Text label, int fontSize){
        shape.setArcWidth(20);
        shape.setArcHeight(20);

        Font customFont = Font.loadFont(
                getClass().getResourceAsStream("/fonts/Jellee-Roman.otf"),
                fontSize
        );

        label.setFont(customFont);
        label.setFill(Color.BLACK);

        StackPane stack = new StackPane(shape, label);
        stack.setVisible(false);
        return stack;
    }

    private void applyInputDelay(double seconds) {
        isAnimating = true;
        PauseTransition delay = new PauseTransition(Duration.seconds(seconds));
        delay.setOnFinished(e -> isAnimating = false);
        delay.play();
    }

    private void advanceScene(){
        if(isExiting) return;

        switch (currentSceneStep){
            case 0:
                openCurtains();
                startSirWalk();
                currentSceneStep++;
                break;

            case 1:
                sirBubble.setVisible(true);
                clickEnterText.setVisible(true);
                playSound("howsurdayeverybody2.wav");
                currentSceneStep++;
                applyInputDelay(0.5);
                break;

            case 2:
                sirBubble.setVisible(false);
                coolAndNormalBubble.setVisible(true);
                clickEnterText.setVisible(false);
                playSound("coolandnormal.wav");
                currentSceneStep++;
                applyInputDelay(0.5);
                break;

            case 3:
                coolAndNormalBubble.setVisible(false);
                sirText.setText("Good");
                sirBubble.setVisible(true);

                Rectangle goodRect = (Rectangle) sirBubble.getChildren().getFirst();
                goodRect.setWidth(100);

                playSound("good2.wav");
                currentSceneStep++;
                applyInputDelay(0.5);
                break;

            case 4:
                Rectangle rect = (Rectangle) sirBubble.getChildren().getFirst();
                if(professorCourse.equals("CS244")){
                    sirText.setText("Okay. Our discussion for today: Trees!");
                    rect.setWidth(350);
                } else {
                    sirText.setText("Okay. Our discussion for today: Exceptions and Assertions!");
                    rect.setWidth(475);
                }
                sirBubble.setVisible(true);

                if(professorCourse.equals("CS244")){
                    ppt.setImage(daaTitle);
                    playSound("daa_intro.wav");
                } else {
                    ppt.setImage(oopTitle);
                    playSound("oop_intro.wav");
                }

                ppt.setVisible(true);
                currentSceneStep++;
                applyInputDelay(0.5);
                break;

            case 5:
                sirBubble.setVisible(false);
                if(professorCourse.equals("CS244")){
                    startDaaLecture();
                } else {
                    startOopLecture();
                }
                break;

            case 6:
                ppt.setImage(imgPptQuiz);
                sirText.setText("Quiz! Get one-fourth sheet of paper.");

                playSound("quiz.wav");

                sirBubble.setVisible(true);
                Rectangle rect2 = (Rectangle) sirBubble.getChildren().getFirst();
                rect2.setWidth(400);
                currentSceneStep++;
                break;

            case 7:
                sirBubble.setVisible(false);
                treBubble.setVisible(true);

                sirJay.setImage(imgSirJudgy);
                sirJay.setScaleX(-1);
                playSound("waitsasir.wav");

                currentSceneStep++;
                break;

            case 8:
                treBubble.setVisible(false);
                hertzBubble.setVisible(true);

                sirJay.setScaleX(1);
                playSound("onefourth.wav");

                currentSceneStep++;
                break;

            case 9:
                hertzBubble.setVisible(false);
                cleanUpAudio();
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

        });

        parallel.play();
    }

    private void startOopLecture() {
        isAnimating = true;

        mouthFlicker = new Timeline(
                new KeyFrame(Duration.seconds(0.10), e -> {
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
        playMusic("oop_discussion.wav");

        pptTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> ppt.setImage(oopSlide1)),

                new KeyFrame(Duration.seconds(4.0), e -> ppt.setImage(oopSlide2)),

                new KeyFrame(Duration.seconds(8.5), e -> ppt.setImage(oopSlide3))
        );
        pptTimeline.play();

        lectureTimer = new PauseTransition(Duration.seconds(13.0));
        lectureTimer.setOnFinished(e -> {
            mouthFlicker.stop();
            sirJay.setImage(imgNormalSir);
            isAnimating = false;
            currentSceneStep++;
        });

        lectureTimer.play();
    }

    private void startDaaLecture(){
        isAnimating = true;

        mouthFlicker = new Timeline(
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
        playMusic("daa_discussion.mp3");

        pptTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> ppt.setImage(daaSlide1)),

                new KeyFrame(Duration.seconds(4.0), e -> ppt.setImage(daaSlide2)),

                new KeyFrame(Duration.seconds(8.5), e -> ppt.setImage(daaSlide3))
        );
        pptTimeline.play();

        lectureTimer = new PauseTransition(Duration.seconds(13.0));
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

        bobbing = new TranslateTransition(Duration.seconds(0.2), sirJay);
        bobbing.setFromY(340);
        bobbing.setToY(330);
        bobbing.setCycleCount(Animation.INDEFINITE);
        bobbing.setAutoReverse(true);
        bobbing.play();

        delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(e -> {
            sirJay.setVisible(true);
        });
        delay.play();

        walk = new TranslateTransition(Duration.seconds(3.5), sirJay);
        walk.setFromX(1100);
        walk.setToX(750);
        walk.setInterpolator(Interpolator.EASE_OUT);

        walk.setOnFinished(ev -> {
            bobbing.stop();
            sirJay.setTranslateY(340);
            isAnimating = false;
            advanceScene();
        });

        delay2 = new PauseTransition(Duration.seconds(2.5));
        delay2.setOnFinished(e -> {
            bobbing.play();
            walk.play();
        });
        delay2.play();
    }


}
