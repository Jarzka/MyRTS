package org.voimala.myrtsengine.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import org.voimala.utility.RandomNumberGenerator;

import java.util.HashMap;

public class SoundContainer {

    private static final String TAG = SoundContainer.class.getName();
    private static SoundContainer instanceOfThis = null;
    private HashMap<String, Sound> sounds = new HashMap<String, Sound>();
    private long unitCommandSoundLastGetTimestamp = 0;

    public static SoundContainer getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new SoundContainer();
        }

        return instanceOfThis;
    }

    private SoundContainer() {
    }

    public Sound getSound(final String id) {
        if (sounds.get(id) != null) {
            return sounds.get(id);
        }

        Gdx.app.debug(TAG, "WARNING: Audio Effect " + id + " not found");
        return null;
    }

    /** @param id The id should be in the following format: UNITNAME-COMMAND For example: m4-move */
    public Sound getUnitCommandSound(final String id) {
        // Do not allow the game to play too many unit command sounds at the same time.
        if (System.currentTimeMillis() < unitCommandSoundLastGetTimestamp + 200) {
            return null;
        }

        // TODO Use RegEx here?
        if (id.contains("-move") || id.contains("-select") || id.contains("-attack")) {
            // Check the number of available audio files and choose one randomly
            int maxIndex = 1;
            while(true) {
                if (sounds.get(id + maxIndex) != null) {
                    maxIndex++;
                    continue;
                }

                maxIndex--;
                break;
            }

            unitCommandSoundLastGetTimestamp = System.currentTimeMillis();
            Sound sound = sounds.get(id + RandomNumberGenerator.random(1, maxIndex));
            return sound;
        }

        Gdx.app.debug(TAG, "WARNING: Audio file not found: " + id);
        return null;
    }

    public void addSound(final String id, final Sound sound) {
        sounds.put(id, sound);
    }

    public void freeResources() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }

        Gdx.app.debug(TAG, "Sprites disposed.");
    }


}
