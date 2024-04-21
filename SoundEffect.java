import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

class SoundEffect{
    public Clip c;
    public SoundEffect(String filename){
        setClip(filename);
    }
    public void setClip(String filename){
        try{
            File f = new File(filename);
            c = AudioSystem.getClip();
            c.open(AudioSystem.getAudioInputStream(f));
        } catch(Exception e){ System.out.println("error"); }
    }
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