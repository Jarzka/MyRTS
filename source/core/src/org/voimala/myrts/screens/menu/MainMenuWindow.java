package org.voimala.myrts.screens.menu;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class MainMenuWindow extends Window {

    private Skin skin;
    private Table table;

    public MainMenuWindow(String title, Skin skin) {
        super(title, skin);
        this.skin = skin;

        //initialize();
    }

    public void initialize() {
        buildWidgets();
        finalizeWindow();
    }

    private void buildWidgets() {
        table = new Table();

        table.pad(10, 10, 0, 10);
        table.add(new Label("Audio", skin, "default-font",
                Color.ORANGE)).colspan(3);
        table.row();
        table.columnDefaults(0).padRight(10);
        table.columnDefaults(1).padRight(10);

        this.add(table);
    }

    private void finalizeWindow() {
        // Visual effects
        setColor(1, 1, 1, 0.8f);

        // Hide options window by default
        setVisible(false);

        // Let TableLayout recalculate widget sizes and positions
        pack();

        // Set position
        setPosition(0, 0);
    }
}
