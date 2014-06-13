package org.voimala.myrts.screens.menu.windows;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import org.voimala.myrts.screens.menu.MenuScreen;

public abstract class AbstractMenuWindow extends Window {

    protected MenuScreen menuScreen;

    public AbstractMenuWindow(String title, Skin skin, MenuScreen menuScreen) {
        super(title, skin);

        this.menuScreen = menuScreen;
    }

    public abstract WindowName getWindowName();

}
