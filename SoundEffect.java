/*
 * Program: SoundEffect.java
 * Author: Mr. McKenzie, modified by Noah Levy

 * This class makes it easy to load sound files and save them as an object. The sound file
 * can be played, stopped, and checked if it;s running.
 */

import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

class SoundEffect{
    //set clip object
    public Clip c;
    public SoundEffect(String filename){
        setClip(filename);
    }
    //try to load clip at given file path
    public void setClip(String filename){
        try{
            File f = new File(filename);
            c = AudioSystem.getClip();
            c.open(AudioSystem.getAudioInputStream(f));
        } catch(Exception e){ System.out.println("error"); }
    }
    //play, stop and check if playing
    public void play(){
        c.setFramePosition(0);
        c.start();
    }
    public void stop(){
        c.stop();
    }
    public boolean isPlaying() {
        return c.isRunning();
    }
}