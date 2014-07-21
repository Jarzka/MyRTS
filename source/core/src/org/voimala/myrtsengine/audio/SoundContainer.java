package org.voimala.myrtsengine.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;

public class SoundContainer {

    private static final String TAG = SoundContainer.class.getName();
    private static SoundContainer instanceOfThis = null;
    private HashMap<String, Sound> sounds = new HashMap<String, Sound>();

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
