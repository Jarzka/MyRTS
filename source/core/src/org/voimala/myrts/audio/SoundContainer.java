package org.voimala.myrts.audio;

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

    /** @param id Given a sound id the method will randomly pick some version of the sound by adding a random
     *  number at the end of the id (as long as the corresponding file is found)
     *  The id should be in the following format: UNITNAME-COMMAND
     *  Possible commands: select, move, attack
     *  For example id is m4-move, the method can return m4-move1, m4-move2 or m4-move3
     *  @return The asked Sound file. null is returned if the file is not found or last call was made less than 200ms ago. */
    public Sound getRandomUnitCommandSound(final String id) {
        // Do not allow the game to play too many unit command sounds at the same time.
        if (System.currentTimeMillis() < unitCommandSoundLastGetTimestamp + 200) {
            return null;
        }

        if (id.contains("-move") || id.contains("-select") || id.contains("-attack")) {
            return getRandomSound(id);
        }

        Gdx.app.debug(TAG, "WARNING: Audio file not found: " + id);
        return null;
    }

    /** @param id Given a sound id the method will randomly pick some version of the sound by adding a random
     *  number at the end of the id (as long as the corresponding file is found).*/
    public Sound getRandomSound(final String id) {
        // Check all available audio files and choose one randomly
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
