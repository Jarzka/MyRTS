package org.voimala.myrts.graphics;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.HashMap;

public class SpriteContainer {

    private static SpriteContainer instanceOfThis = null;
    private HashMap<String, Sprite> sprites = new HashMap<String, Sprite>();

    private SpriteContainer() {}

    public static SpriteContainer getInstance() {
        if (instanceOfThis == null) {
            instanceOfThis = new SpriteContainer();
        }

        return instanceOfThis;
    }

    public Sprite getSprite(final String id) {
        return sprites.get(id);
    }

    public void addSprite(final String id, final Sprite sprite) {
        sprites.put(id, sprite);
    }

    public static void freeResources() {
        // TODO
    }
}
