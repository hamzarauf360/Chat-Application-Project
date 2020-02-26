/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package i150213_assignment_3_practice;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 *
 * @author Hamza
 */
public class audio_recorder {
    private String client_name;
    String filename;
    public audio_recorder(String client_name){
        this.client_name = client_name;
        filename=this.client_name;
        filename+="audio_message.wav";
        System.out.println("The filename for audio clip is: "+filename);
    }
    public void audio_record() throws LineUnavailableException, InterruptedException {
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line is not supported");
        }
        final TargetDataLine targetline = (TargetDataLine) AudioSystem.getLine(info);

        targetline.open();

        System.out.println("Starting recording");
        targetline.start();
        Thread thread = new Thread() {
            @Override
            public void run() {
                AudioInputStream ais = new AudioInputStream(targetline);
                File audiofile = new File(filename);
                try {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audiofile);
                    System.out.println("Recoding ended");

                } catch (IOException ex) {
                    Logger.getLogger(audio_recorder.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        thread.start();

        Thread.sleep(5000);
        targetline.stop();

        targetline.close();

        System.out.println("Recording test ended");
    }

}
