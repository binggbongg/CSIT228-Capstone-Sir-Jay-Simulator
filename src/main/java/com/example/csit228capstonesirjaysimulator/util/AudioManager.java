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
        String[] lines = {
                "good1.wav",
                "notgood1.wav",
                "tap.mp3",
                "mahbadmahbad2.wav",
                "whatdidijustdo2.wav",
                "anywho1.wav",
                "tsk1.wav",
                "swearbymysword1.wav"};
        for(String s : lines){
            soundMap.put(s, FXGL.getAssetLoader().loadSound(s));
        }
    }

    private void preloadMusic(){
        String[] lines = {"distractor_sfx.wav",
                            "chocolate-milk.mp3"};
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
       Music m = musicMap.get(name);
       if(m != null){
           FXGL.getAudioPlayer().loopMusic(m);
       } else {
           FXGL.getAudioPlayer().loopMusic(FXGL.getAssetLoader().loadMusic(name));
       }
    }

    public void stopAllSounds(){
        FXGL.getAudioPlayer().stopAllSounds();
    }

    public void stopAllMusicPlaying() {
        FXGL.getAudioPlayer().stopAllMusic();
    }

    public void stopMusic(String name){
        Music m = musicMap.get(name);
        FXGL.getAudioPlayer().stopMusic(m);
    }

    public void setMusicVolume (double volume) {
        double clampedVolume = Math.max(0.0, Math.min(1.0, volume));

        // FXGL automatically syncs these specific string variables to its internal audio systems!
        FXGL.set("musicVolume", clampedVolume * 0.6); // Keep music a bit quieter

        System.out.println("Music volume safely adjusted via FXGL Properties to: " + clampedVolume);
    }

    public void setSoundVolume (double volume) {
        double clampedVolume = Math.max(0.0, Math.min(1.0, volume));

        // FXGL automatically syncs these specific string variables to its internal audio systems!
        FXGL.set("soundVolume", clampedVolume);

        System.out.println("Sound volume safely adjusted via FXGL Properties to: " + clampedVolume);
    }

    public void pauseMusic(String name){
        Music m = musicMap.get(name);
        FXGL.getAudioPlayer().pauseMusic(m);
    }

    public void resumeMusic(String name){
        Music m = musicMap.get(name);
        FXGL.getAudioPlayer().resumeMusic(m);
    }
}
