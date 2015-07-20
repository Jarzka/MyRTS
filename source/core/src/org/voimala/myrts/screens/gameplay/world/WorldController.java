package org.voimala.myrts.screens.gameplay.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrts.audio.AudioEffect;
import org.voimala.myrts.screens.gameplay.effects.AbstractEffect;
import org.voimala.myrts.screens.gameplay.GameplayScreen;
import org.voimala.myrts.screens.gameplay.ammunition.AbstractAmmunition;
import org.voimala.myrts.screens.gameplay.units.AbstractUnit;
import org.voimala.myrts.screens.gameplay.units.UnitContainer;
import org.voimala.myrts.screens.gameplay.units.infantry.M4Unit;
import org.voimala.myrts.screens.gameplay.units.turrets.AbstractTurret;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class WorldController {

    private static final String TAG = WorldController.class.getName();

    /* Containers */
    private UnitContainer unitContainer = new UnitContainer();
    private ArrayList<AbstractUnit> unitsToBeRemoved = new ArrayList<AbstractUnit>();
    private ArrayList<AbstractAmmunition> ammunitionContainer = new ArrayList<AbstractAmmunition>();
    private ArrayList<AbstractAmmunition> ammunitionToBeRemoved = new ArrayList<AbstractAmmunition>();
    private ArrayList<AbstractEffect> effectsContainer = new ArrayList<AbstractEffect>();
    private ArrayList<AbstractEffect> effectsToBeRemoved = new ArrayList<AbstractEffect>();
    private ArrayList<AudioEffect> audioEffectContainer = new ArrayList<AudioEffect>();
    private ArrayList<AudioEffect> audioEffectsToBeRemoved = new ArrayList<AudioEffect>();
    private long nextFreeId = 0;

    private long worldUpdateTick = 0;

    private GameplayScreen gameplayScreen;

    private double hudSize = 1; // TODO Hud needs to be implemented

    public final int TILE_SIZE_PIXELS = 256;

    public WorldController() {
        initialize();
    }

    /** Copy constructor. */
    public WorldController(final WorldController source) {
        /* NOTE: initialize() is not called because at the moment (24.7.2014) it is used only for creating test map.*/

        this.gameplayScreen = source.getGameplayScreen();

        String sourceWorldHash = source.getGameStateHash();

        // Clone containers
        for (AbstractUnit unit : source.getUnitContainer().getAllUnits()) {
            try {
                AbstractUnit unitClone = unit.clone();
                unitClone.setWorldController(this);


                unitContainer.addUnit(unitClone);
            } catch (CloneNotSupportedException e) {
                Gdx.app.debug(TAG, "WARNING: CloneNotSupportedException when cloning Unit: " + e.getMessage());
            }
        }

        // Set turret targets to match corresponding units in the cloned world.
        for (AbstractUnit clonedUnit : unitContainer.getAllUnits()) {
            // Find source unit for this cloned unit
            AbstractUnit unitSource = source.getUnitContainer().findUnitById(clonedUnit.getObjectId());

            for (AbstractTurret clonedTurret : clonedUnit.getTurrets()) {
                // Find source turret for this turret
                AbstractTurret turretSource = null;
                for (AbstractTurret turret : unitSource.getTurrets()) {
                    if (turret.getObjectId() == clonedTurret.getObjectId()) {
                        turretSource = turret;
                        break;
                    }
                }

                if (turretSource.hasTarget()) {
                    clonedTurret.setTarget(unitContainer.findUnitById(turretSource.getTarget().getObjectId()));
                }
            }
        }


        for (AbstractAmmunition ammunition : source.getAmmunitionContainer()) {
            try {
                AbstractAmmunition ammunitionClone = ammunition.clone();
                ammunitionClone.setWorldController(this);
                ammunitionContainer.add(ammunitionClone);
            } catch (CloneNotSupportedException e) {
                Gdx.app.debug(TAG, "WARNING: CloneNotSupportedException when cloning Ammunition: " + e.getMessage());
            }
        }

        for (AbstractEffect effect : source.getEffectsContainer()) {
            try {
                AbstractEffect effectClone = effect.clone();
                effectClone.setWorldController(this);
                effectsContainer.add(effectClone);
            } catch (CloneNotSupportedException e) {
                Gdx.app.debug(TAG, "WARNING: CloneNotSupportedException when cloning Ammunition: " + e.getMessage());
            }
        }

        // Makes sure the original world was not modified
        if (!sourceWorldHash.equals(source.getGameStateHash())) {
            Gdx.app.debug(TAG, "ERROR: Original world was modifed during clone()!");
        }
    }

    private void initialize() {
        initializeMap();
    }

    private void initializeMap() {
        // TODO For now we just create a simple test map.
        // The final implementation should load the map from hard disk.
        //createTestWorldSimple();
        //createTestWorldNormal();
        createTestWorldNormalWithInputsGoesSometimesOutOfSync();
        //createTestWorldStreeTest();
    }

    private void createTestWorldSimple() {
        M4Unit unit = new M4Unit(this);
        unit.setPosition(new Vector2(500 + TILE_SIZE_PIXELS, 500 + TILE_SIZE_PIXELS));
        unit.setTeam(1);
        unit.setPlayerNumber(1);
        unit.setAngle(0);
        unit.getMovement().addPathPoint(new Vector2(3600, 3600));
        unitContainer.addUnit(unit);

        M4Unit unit2 = new M4Unit(this);
        unit2.setPosition(new Vector2(4000 + TILE_SIZE_PIXELS, 4000 + TILE_SIZE_PIXELS));
        unit2.setPlayerNumber(2);
        unit2.setTeam(2);
        unit2.setAngle(180);
        unit2.getTurrets().get(0).setAngle(unit2.getAngle());
        unitContainer.addUnit(unit2);
    }

    private void createTestWorldNormal() {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                M4Unit unit = new M4Unit(this);
                unit.setPosition(new Vector2(500 + TILE_SIZE_PIXELS * i, 500 + TILE_SIZE_PIXELS  * j));
                unit.setTeam(1);
                unit.setPlayerNumber(1);
                unit.setAngle(0);
                unitContainer.addUnit(unit);
            }
        }

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                M4Unit unit = new M4Unit(this);
                unit.setPosition(new Vector2(5000 + TILE_SIZE_PIXELS * i, 5000 + TILE_SIZE_PIXELS  * j));
                unit.setPlayerNumber(2);
                unit.setTeam(2);
                unit.setAngle(180);
                unitContainer.addUnit(unit);
            }
        }
    }

    /** Usually goes out of sync around SimTick 30 but can also go out of sync around SimTick 60. This is not
     * deterministic. */
    private void createTestWorldNormalWithInputsGoesSometimesOutOfSync() {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                M4Unit unit = new M4Unit(this);
                unit.setPosition(new Vector2(500 + TILE_SIZE_PIXELS * i, 500 + TILE_SIZE_PIXELS  * j));
                unit.setTeam(1);
                unit.setPlayerNumber(1);
                unit.setAngle(0);
                unit.getMovement().addPathPoint(new Vector2(5000, 5000));
                unitContainer.addUnit(unit);
            }
        }

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 12; j++) {
                M4Unit unit = new M4Unit(this);
                unit.setPosition(new Vector2(5000 + TILE_SIZE_PIXELS * i, 5000 + TILE_SIZE_PIXELS  * j));
                unit.setPlayerNumber(2);
                unit.setTeam(2);
                unit.setAngle(180);
                unitContainer.addUnit(unit);
            }
        }
    }

    private void createTestWorldStreeTest() {
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 25; j++) {
                M4Unit unit = new M4Unit(this);
                unit.setPosition(new Vector2(500 + TILE_SIZE_PIXELS * i, 500 + TILE_SIZE_PIXELS  * j));
                unit.setTeam(1);
                unit.setPlayerNumber(1);
                unit.setAngle(0);
                unitContainer.addUnit(unit);
            }
        }

        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 25; j++) {
                M4Unit unit = new M4Unit(this);
                unit.setPosition(new Vector2(9000 + TILE_SIZE_PIXELS * i, 9000 + TILE_SIZE_PIXELS  * j));
                unit.setPlayerNumber(2);
                unit.setTeam(2);
                unit.setAngle(180);
                unitContainer.addUnit(unit);
            }
        }
    }

    public void updateWorld(final float deltaTime) {
        Gdx.app.debug(TAG, "About to update world at WorldTick " + worldUpdateTick);
        updateUnits(deltaTime);
        updateAmmunition(deltaTime);
        updateAudioEffects();
        updateEffects(deltaTime);

        removeTaggedObjects();

        worldUpdateTick++;
        Gdx.app.debug(TAG, "World updated. WorldTick incremented and is now " + worldUpdateTick);
    }

    /** If objects were removed directly during world update, it would cause problems since the WorldController would be
     * still looping trough all objects. That's why objects that need to me removed are be added to a special
     * container and removed after world has been updated. */
    private void removeTaggedObjects() {
        for (AudioEffect audioEffect : audioEffectsToBeRemoved) {
            audioEffectContainer.remove(audioEffect);
        }
        audioEffectsToBeRemoved.clear();

        for (AbstractAmmunition ammunition : ammunitionToBeRemoved) {
            ammunitionContainer.remove(ammunition);
        }
        ammunitionToBeRemoved.clear();

        for (AbstractEffect effect : effectsToBeRemoved) {
            effectsContainer.remove(effect);
        }
        effectsToBeRemoved.clear();

        for (AbstractUnit unit : unitsToBeRemoved) {
            unitContainer.removeUnit(unit);
        }
        unitsToBeRemoved.clear();
    }

    private void updateUnits(final float deltaTime) {
        for (AbstractUnit unit : unitContainer.getAllUnits()) {
            unit.updateState(deltaTime);
        }
    }

    private void updateAmmunition(final float deltaTime) {
        for (AbstractAmmunition ammunition : ammunitionContainer) {
            ammunition.updateState(deltaTime);
        }
    }

    private void updateAudioEffects() {
        for (AudioEffect audioEffect : audioEffectContainer) {
            audioEffect.updateState();
        }
    }

    private void updateEffects(final float deltaTime) {
        for (AbstractEffect effect : effectsContainer) {
            effect.updateState(deltaTime);
        }
    }

    public GameplayScreen getGameplayScreen() {
        return gameplayScreen;
    }

    public void setGameplayScreen(final GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
    }

    public UnitContainer getUnitContainer() {
        return unitContainer;
    }

    public List<AbstractAmmunition> getAmmunitionContainer() {
        return ammunitionContainer;
    }

    public ArrayList<AudioEffect> getAudioEffectContainer() {
        return audioEffectContainer;
    }

    public List<AbstractEffect> getEffectsContainer() {
        return effectsContainer;
    }

    public void tagAudioToBeRemovedInNextWorldUpdate(final AudioEffect audioEffectToBeRemoved) {
        audioEffectsToBeRemoved.add(audioEffectToBeRemoved);
    }

    public void tagAmmunitionToBeRemovedInNextWorldUpdate(AbstractAmmunition ammunitionToBeRemoved) {
        this.ammunitionToBeRemoved.add(ammunitionToBeRemoved);
    }

    public void tagEffectToBeRemoved(final AbstractEffect effect) {
        this.effectsToBeRemoved.add(effect);
    }

    public void tagUnitToBeRemoved(final AbstractUnit unit) {
        this.unitsToBeRemoved.add(unit);
    }

    // TODO Time consuming process, do only once per second? Tai kokeile StringBuilderia?
    public String getGameStateHash() {
        StringBuilder hashBuilder = new StringBuilder();

        for (AbstractUnit unit : getUnitContainer().getAllUnits()) {
            hashBuilder.append("unit id: " + unit.getObjectId() + " ");
            hashBuilder.append("x: " + unit.getObjectId() + unit.getX() + " ");
            hashBuilder.append("y: " + unit.getY() + " ");
            hashBuilder.append("angle: " + unit.getAngleInRadians() + " ");
            hashBuilder.append("player: " + unit.getPlayerNumber() + " ");
            hashBuilder.append("team:" + unit.getTeam() + " ");
            for (AbstractTurret turret : unit.getTurrets()) {
                hashBuilder.append("turret id: " + turret.getObjectId() + " ");
                hashBuilder.append("x: " + turret.getX() + " ");
                hashBuilder.append("y: " + turret.getY() + " ");
                hashBuilder.append("angle: " + turret.getAngleInRadians() + " ");
                if (turret.hasTarget()) {
                    hashBuilder.append("target id: " + turret.getTarget().getObjectId());
                } else {
                    hashBuilder.append("target id: null");
                }
            }
            hashBuilder.append("\n");
        }

        for (AbstractAmmunition ammunition : ammunitionContainer) {
            hashBuilder.append("ammunition id: " + ammunition.getObjectId() + " ");
            hashBuilder.append("x: " + ammunition.getX() + " ");
            hashBuilder.append("y: " + ammunition.getY() + " ");
            hashBuilder.append("angle: " + ammunition.getAngleInRadians() + " ");
            hashBuilder.append("\n");
        }

        return hashBuilder.toString(); // Slow, used for testing purposes only
        //return md5(hashBuilder.toString()); // Fast, production ready
    }

    public static String md5(String message){
        String digest = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));

            //converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2*hash.length);
            for(byte b : hash){
                sb.append(String.format("%02x", b&0xff));
            }

            digest = sb.toString();

        } catch (UnsupportedEncodingException e) {
            Gdx.app.debug(TAG, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Gdx.app.debug(TAG, e.getMessage());
        }

        return digest;
    }

    public long getNextFreeId() {
        Gdx.app.debug(TAG, "Thread " + Thread.currentThread().getName() + " called getNextFreeId()");
        return nextFreeId++;
    }

    public long getWorldUpdateTick() {
        return worldUpdateTick;
    }
}
