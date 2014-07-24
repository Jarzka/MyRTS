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
import java.util.HashMap;
import java.util.List;

public class WorldController {

    private static final String TAG = WorldController.class.getName();

    /** Contains all units used in the game */
    private UnitContainer unitContainerAllUnits = new UnitContainer();
    /** Contains all units used by a specific player for fast access. Integer = Player number */
    private HashMap<Integer, UnitContainer> unitContainersForSpecificPlayers = new HashMap<Integer, UnitContainer>();
    private ArrayList<AbstractUnit> unitsToBeRemoved = new ArrayList<AbstractUnit>();
    private ArrayList<AbstractAmmunition> ammunitionContainer = new ArrayList<AbstractAmmunition>();
    private ArrayList<AbstractAmmunition> ammunitionToBeRemoved = new ArrayList<AbstractAmmunition>();
    private ArrayList<AbstractEffect> effectsContainer = new ArrayList<AbstractEffect>();
    private ArrayList<AbstractEffect> effectsToBeRemoved = new ArrayList<AbstractEffect>();
    private ArrayList<AudioEffect> audioEffectContainer = new ArrayList<AudioEffect>();
    private ArrayList<AudioEffect> audioEffectsToBeRemoved = new ArrayList<AudioEffect>();
    private long nextFreeId = 0;

    /** If true, this is a predicted world that is used in rendering process. This world should be updated only
     * "visually" */
    private boolean isPredictedWorld = false;

    private long worldUpdateTick = 0;

    private GameplayScreen gameplayScreen;

    private double hudSize = 1; // TODO Hud needs to be implemented

    public final int TILE_SIZE_PIXELS = 256;

    /**
     * Copy constructor.
     */
    public WorldController(final WorldController source) {
        initializeContainers();

        this.gameplayScreen = source.getGameplayScreen();

        String sourceWorldHash = source.getGameStateHash();

        // Clone containers
        for (AbstractUnit unit : source.getAllUnits()) {
            try {
                AbstractUnit unitClone = unit.clone();
                unitClone.setWorldController(this);


                storeUnitInContainer(unitClone);
            } catch (CloneNotSupportedException e) {
                // This should never happen. Continue without cloning this object.
            }
        }

        // Set turret targets to match corresponding units in the cloned world.
        for (AbstractUnit clonedUnit : getAllUnits()) {
            // Find source unit for this cloned unit
            AbstractUnit unitSource = source.getUnitContainerAllUnits().findUnitById(clonedUnit.getObjectId());

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
                    clonedTurret.setTarget(unitContainerAllUnits.findUnitById(turretSource.getTarget().getObjectId()));
                }
            }
        }


        for (AbstractAmmunition ammunition : source.getAmmunitionContainer()) {
            try {
                AbstractAmmunition ammunitionClone = ammunition.clone();
                ammunitionClone.setWorldController(this);
                ammunitionContainer.add(ammunitionClone);
            } catch (CloneNotSupportedException e) {
                // This should never happen. Continue without cloning this object.
            }
        }

        for (AbstractEffect effect : source.getEffectsContainer()) {
            try {
                AbstractEffect effectClone = effect.clone();
                effectClone.setWorldController(this);
                effectsContainer.add(effectClone);
            } catch (CloneNotSupportedException e) {
                // This should never happen. Continue without cloning this object.
            }
        }

        // Makes sure the original world was not modified
        if (!sourceWorldHash.equals(source.getGameStateHash())) {
            Gdx.app.debug(TAG, "ERROR: Original world was modifed during clone()!");
        }
    }

    public WorldController() {
        initialize();
    }

    private void initialize() {
        initializeContainers();
        initializeMap();
    }

    private void initializeContainers() {
        for (int i = 1; i <= 8; i++) {
            unitContainersForSpecificPlayers.put(i, new UnitContainer());
        }
    }


    private void initializeMap() {
        // TODO For now we just create a simple test map.
        // The final implementation should load the map from hard disk.
        //createTestWorldSimple();
        //createTestWorldNormal();
        createTestWorldStreeTest();
    }

    private void createTestWorldSimple() {
        M4Unit unit = new M4Unit(this);
        unit.setPosition(new Vector2(500 + TILE_SIZE_PIXELS, 500 + TILE_SIZE_PIXELS));
        unit.setTeam(1);
        unit.setPlayerNumber(1);
        unit.setAngle(0);
        unit.getMovement().addPathPoint(new Vector2(3600, 3600));
        storeUnitInContainer(unit);

        M4Unit unit2 = new M4Unit(this);
        unit2.setPosition(new Vector2(4000 + TILE_SIZE_PIXELS, 4000 + TILE_SIZE_PIXELS));
        unit2.setPlayerNumber(2);
        unit2.setTeam(2);
        unit2.setAngle(180);
        unit2.getTurrets().get(0).setAngle(unit2.getAngle());
        storeUnitInContainer(unit2);
    }

    private void createTestWorldNormal() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                M4Unit unit = new M4Unit(this);
                unit.setPosition(new Vector2(500 + TILE_SIZE_PIXELS * i, 500 + TILE_SIZE_PIXELS  * j));
                unit.setTeam(1);
                unit.setPlayerNumber(1);
                unit.setAngle(0);
                storeUnitInContainer(unit);
            }
        }

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                M4Unit unit = new M4Unit(this);
                unit.setPosition(new Vector2(5000 + TILE_SIZE_PIXELS * i, 5000 + TILE_SIZE_PIXELS  * j));
                unit.setPlayerNumber(2);
                unit.setTeam(2);
                unit.setAngle(180);
                storeUnitInContainer(unit);
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
                storeUnitInContainer(unit);
            }
        }

        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 25; j++) {
                M4Unit unit = new M4Unit(this);
                unit.setPosition(new Vector2(9000 + TILE_SIZE_PIXELS * i, 9000 + TILE_SIZE_PIXELS  * j));
                unit.setPlayerNumber(2);
                unit.setTeam(2);
                unit.setAngle(180);
                storeUnitInContainer(unit);
            }
        }
    }

    public void storeUnitInContainer(AbstractUnit unit) {
        if (getUnitContainerForSpecificPlayer(unit.getPlayerNumber()) != null) {
            getUnitContainerForSpecificPlayer(unit.getPlayerNumber()).addUnit(unit);
        }

        unitContainerAllUnits.addUnit(unit);
    }

    public UnitContainer getUnitContainerForSpecificPlayer(final int playerNumber) {
        return unitContainersForSpecificPlayers.get(playerNumber);
    }

    public UnitContainer getUnitContainerAllUnits() {
        return unitContainerAllUnits;
    }

    public void updateWorld(final float deltaTime) {
        updateUnits(deltaTime);
        updateAmmunition(deltaTime);
        updateAudioEffects();
        updateEffects(deltaTime);

        removeTaggedObjects();

        if (!isPredictedWorld) {
            finishWorldUpdating();
        }

        worldUpdateTick++;
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
            if (!isPredictedWorld()) {
                Gdx.app.debug(TAG, "About to remove ammunition. id: " + ammunition.getObjectId());
                Gdx.app.debug(TAG, "World is prediced: " + isPredictedWorld());
            }

            ammunitionContainer.remove(ammunition);
        }
        ammunitionToBeRemoved.clear();

        for (AbstractEffect effect : effectsToBeRemoved) {
            effectsContainer.remove(effect);
        }
        effectsToBeRemoved.clear();

        for (AbstractUnit unit : unitsToBeRemoved) {
            if (!isPredictedWorld()) {
                Gdx.app.debug(TAG, "About to remove unit. id: " + unit.getObjectId());
                Gdx.app.debug(TAG, "World is prediced: " + isPredictedWorld());
            }
            removeUnitFromWorld(unit);
        }
        unitsToBeRemoved.clear();
    }

    private void removeUnitFromWorld(final AbstractUnit unit) {
        unitContainersForSpecificPlayers.get(unit.getPlayerNumber()).removeUnit(unit);
        unitContainerAllUnits.removeUnit(unit);
    }

    private void updateUnits(final float deltaTime) {
        for (AbstractUnit unit : unitContainerAllUnits.getUnits()) {
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

    private void finishWorldUpdating() {
        getGameplayScreen().getWorldRenderer().notifyWorldUpdated();
    }

    public GameplayScreen getGameplayScreen() {
        return gameplayScreen;
    }

    public void setGameplayScreen(final GameplayScreen gameplayScreen) {
        this.gameplayScreen = gameplayScreen;
    }

    public List<AbstractUnit> getAllUnits() {
        return unitContainerAllUnits.getUnits();
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

    public String getGameStateHash() {
        String hash = "";

        for (AbstractUnit unit : getAllUnits()) {
            StringBuilder hashBuilder = new StringBuilder();
            hashBuilder.append("unit:" + unit.getObjectId() + " ");
            hashBuilder.append("x:" + unit.getObjectId() + unit.getX() + " ");
            hashBuilder.append("y:" + unit.getY() + " ");
            hashBuilder.append("angle:" + unit.getAngleInRadians() + " ");
            hashBuilder.append("player:" + unit.getPlayerNumber() + " ");
            hashBuilder.append("team:" + unit.getTeam() + " ");
            for (AbstractTurret turret : unit.getTurrets()) {
                hashBuilder.append("turret:" + turret.getObjectId() + " ");
                hashBuilder.append("x:" + turret.getX() + " ");
                hashBuilder.append("y:" + turret.getY() + " ");
                hashBuilder.append("angle:" + turret.getAngleInRadians() + " ");
                if (turret.hasTarget()) {
                    hashBuilder.append("target:" + turret.getTarget().getObjectId());
                } else {
                    hashBuilder.append("target:null");
                }
            }
            hash += "\n";
            hash += hashBuilder.toString();
        }

        for (AbstractAmmunition ammunition : ammunitionContainer) {
            StringBuilder hashBuilder = new StringBuilder();
            hashBuilder.append("ammunition:" + ammunition.getObjectId() + " ");
            hashBuilder.append("x:" + ammunition.getX() + " ");
            hashBuilder.append("y:" + ammunition.getY() + " ");
            hashBuilder.append("angle:" + ammunition.getAngleInRadians() + " ");
            hash += "\n";
            hash += hashBuilder.toString();
        }

        return hash; // Used for testing purposes only
        //return md5(hash); // For production
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

    public boolean isPredictedWorld() {
        return isPredictedWorld;
    }

    public void setPredictedWorld(final boolean isPredictedWorld) {
        this.isPredictedWorld = isPredictedWorld;
    }

    public long getNextFreeId() {
        return nextFreeId++;
    }

    public long getWorldUpdateTick() {
        return worldUpdateTick;
    }

    /** When done, will return a world that is the same as target. */
    public static WorldController synchronizeWorlds(WorldController source, final WorldController target) {
        // TODO Currently this is just a dumb clone operation. This is a VERY time consuming process.
        WorldController newSource = new WorldController(target);
        return newSource;
    }
}
