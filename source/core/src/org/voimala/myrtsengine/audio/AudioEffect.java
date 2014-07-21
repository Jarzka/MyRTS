package org.voimala.myrtsengine.audio;

import com.badlogic.gdx.audio.Sound;
import org.voimala.myrtsengine.screens.gameplay.world.WorldController;

public class AudioEffect {

    private WorldController worldController;
    private AudioEffectType audioEffectType = AudioEffectType.GLOBAL;
    private float x;
    private float y;
    private String audioEffectName;
    private Sound sound;
    private long soundStartedPlayingTimestamp = 0;

    /** Default constructor creates a global audio effect. */
    public AudioEffect(final WorldController worldController, final String audioEffectName) {
        this.worldController = worldController;
        this.audioEffectName = audioEffectName;
        play();
    }

    /** Creates a local audio effect.*/
    public AudioEffect(final WorldController worldController, final String audioEffectName, final float x, final float y) {
        this.worldController = worldController;
        this.audioEffectName = audioEffectName;
        audioEffectType = AudioEffectType.LOCAL;
        this.x = x;
        this.y = y;
        play();
    }

    public void updateState() {
        if (sound == null) {
            die();
        }

        /* TODO Currently there is no "is playing" method in Libgdx. Neither there is a way to get the duration
         * of the audio file. The only solution to solve this problem at the moment is to just assume that
         * the maximum duration of all audio effects is 10 seconds. */
        if (System.currentTimeMillis() >= soundStartedPlayingTimestamp + 10000) {
            die();
        }
    }

    private void die() {
        // Remove this object from the world
        worldController.tagAudioToBeRemovedInNextWorldUpdate(this);
    }

    private void play() {
        soundStartedPlayingTimestamp = System.currentTimeMillis();
        sound = SoundContainer.getInstance().getSound(audioEffectName);
        if (sound != null) {
            sound.play(0.1f); // TODO Implement master sound and local sound volume.
        }
    }

    public AudioEffectType getAudioEffectType() {
        return audioEffectType;
    }

    public void setAudioEffectType(AudioEffectType audioEffectType) {
        this.audioEffectType = audioEffectType;
    }

    public float getX() {
        return x;
    }

    public void setX(final float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(final float y) {
        this.y = y;
    }
}
