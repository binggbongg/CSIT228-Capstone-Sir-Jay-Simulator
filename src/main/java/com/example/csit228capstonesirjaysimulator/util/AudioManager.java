package com.example.csit228capstonesirjaysimulator.util;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;

import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static AudioManager instance;
    private final Map<String, Sound> soundMap = new HashMap<>();
    private final Map<String, Music> musicMap = new HashMap<>();

    private AudioManager(){
        preloadSounds();
        preloadMusic();
    }

    public static AudioManager getInstance(){
        if(instance == null){
            instance =  new AudioManager();
        }

        return instance;
    }

    private void preloadSounds(){
        String[] lines = {"good1.wav", "notgood1.wav", "tap.mp3", "mahbadmahbad2.wav", "whatdidijustdo2.wav", "anywho1.wav"};
        for(String s : lines){
            soundMap.put(s, FXGL.getAssetLoader().loadSound(s));
        }
    }

    private void preloadMusic(){
        String[] lines = {"distractor_sfx.wav"};
        for(String s : lines){
            musicMap.put(s, FXGL.getAssetLoader().loadMusic(s));
        }
    }

    public void playSound(String name){
        Sound s = soundMap.get(name);
        if(s != null){
            FXGL.getAudioPlayer().playSound(s);
        } else {
            FXGL.getAudioPlayer().playSound(FXGL.getAssetLoader().loadSound(name));
        }
    }

    public void playMusic(String name){
        FXGL.getAudioPlayer().loopMusic(FXGL.getAssetLoader().loadMusic(name));
    }

    public void stopAllSounds(){
        FXGL.getAudioPlayer().stopAllSounds();
    }

    public void stopMusic() {
        FXGL.getAudioPlayer().stopAllMusic();
    }
}
