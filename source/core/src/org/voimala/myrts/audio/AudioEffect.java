package org.voimala.myrts.audio;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.screens.gameplay.world.WorldController;

public class AudioEffect {

    private WorldController worldController;
    private AudioEffectType audioEffectType = AudioEffectType.GLOBAL;
    private Vector2 position;
    private Sound sound;
    private long soundStartedPlayingTimestamp = 0;
    private float volume;

    /** Default constructor creates a global audio effect.
     * @param volume Between 0 and 1. */
    public AudioEffect(final WorldController worldController, final Sound sound, final float volume) {
        this.worldController = worldController;
        this.sound = sound;
        this.volume = volume;
        play();
    }

    /** Creates a local audio effect.
     * @param volume Between 0 and 1. */
    public AudioEffect(final WorldController worldController, Sound sound, final float volume, final Vector2 position) {
        this.worldController = worldController;
        this.sound = sound;
        audioEffectType = AudioEffectType.LOCAL;
        this.position = position;
        this.volume = volume;
        play();
    }

    public void updateState() {
        // TODO Implement master sound and local sound volume.

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
        if (sound != null) {
            sound.play(volume);
        }
    }

    public AudioEffectType getAudioEffectType() {
        return audioEffectType;
    }

    public void setAudioEffectType(AudioEffectType audioEffectType) {
        this.audioEffectType = audioEffectType;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(final Vector2 position) {
        this.position = position;
    }
}
