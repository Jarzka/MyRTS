package org.voimala.myrtsengine.screens.gameplay.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import org.voimala.myrtsengine.audio.AudioEffect;
import org.voimala.myrtsengine.screens.gameplay.GameplayScreen;
import org.voimala.myrtsengine.screens.gameplay.ammunition.AbstractAmmunition;
import org.voimala.myrtsengine.screens.gameplay.units.AbstractUnit;
import org.voimala.myrtsengine.screens.gameplay.units.UnitContainer;
import org.voimala.myrtsengine.screens.gameplay.units.infantry.M4Unit;
import org.voimala.myrtsengine.screens.gameplay.units.turrets.AbstractTurret;

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
    private ArrayList<AbstractAmmunition> ammunitionContainer = new ArrayList<AbstractAmmunition>();
    private ArrayList<AbstractAmmunition> ammunitionToBeRemoved = new ArrayList<AbstractAmmunition>();
    private ArrayList<AudioEffect> audioEffectContainer = new ArrayList<AudioEffect>();
    private ArrayList<AudioEffect> audioEffectsToBeRemoved = new ArrayList<AudioEffect>();

    private GameplayScreen gameplayScreen;
    private OrthographicCamera worldCamera;

    private double hudSize = 1; // TODO Hud needs to be implemented

    public final int TILE_SIZE_PIXELS = 256;

    public WorldController() {
        initialize();
    }

    private void initialize() {
        initializeContainers();
        initializeCamera();
        initializeMap();
    }

    private void initializeContainers() {
        for (int i = 1; i <= 8; i++) {
            unitContainersForSpecificPlayers.put(i, new UnitContainer());
        }
    }

    private void initializeCamera() {
        worldCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        worldCamera.lookAt(0, 0, 0);
        worldCamera.translate(800, 800);
        worldCamera.zoom = 4;
        worldCamera.update();
    }

    private void initializeMap() {
        // TODO For now we just create a simple test map.
        // The final implementation should load the map from hard disk.
        createTestUnit();
    }

    private void createTestUnit() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 6; j++) {
                M4Unit unit = new M4Unit(this);
                unit.setPosition(new Vector2(500 + TILE_SIZE_PIXELS * i, 500 + TILE_SIZE_PIXELS  * j));
                unit.setTeam(1);
                unit.setPlayerNumber(1);
                unit.setAngle(0);
                storeUnitInContainer(unit);
            }
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                M4Unit unit = new M4Unit(this);
                unit.setPosition(new Vector2(4000 + TILE_SIZE_PIXELS * i, 4000 + TILE_SIZE_PIXELS  * j));
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
        removeTaggedObjects();
        updateUnits(deltaTime);
        updateAmmunition(deltaTime);
        updateAudioEffects();
    }

    /** If objects were removed during world update, it would cause problems since the WorldController would be
     * still looping trough all objects. That's way objects that need to me removed will be added to a special
     * container. Objects in that container will be deleted before the next world update. */
    private void removeTaggedObjects() {
        for (AudioEffect audioEffect : audioEffectsToBeRemoved) {
            audioEffectContainer.remove(audioEffect);
        }
        audioEffectsToBeRemoved.clear();

        for (AbstractAmmunition ammunition : ammunitionToBeRemoved) {
            ammunitionContainer.remove(ammunition);
        }
        ammunitionToBeRemoved.clear();
    }

    private void updateUnits(final float deltaTime) {
        for (AbstractUnit unit : unitContainerAllUnits.getUnits()) {
            unit.updateState(deltaTime);
        }
    }

    private void updateAmmunition(final float deltaTime) {
        for (AbstractAmmunition ammunition : ammunitionContainer) {
            //ammunition.updateState(deltaTime); // TODO Goes out of sync
        }
    }

    private void updateAudioEffects() {
        for (AudioEffect audioEffect : audioEffectContainer) {
            audioEffect.updateState();
        }
    }

    public OrthographicCamera getWorldCamera() {
        return worldCamera;
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

    public void tagAudioToBeRemovedInNextWorldUpdate(final AudioEffect audioEffectToBeRemoved) {
        audioEffectsToBeRemoved.add(audioEffectToBeRemoved);
    }

    public void tagAmmunitionToBeRemovedInNextWorldUpdate(AbstractAmmunition ammunitionToBeRemoved) {
        this.ammunitionToBeRemoved.add(ammunitionToBeRemoved);
    }

    public String getGameStateHash() {
        // TODO Make sure that every client has a same container (units are in the same order etc.)
        String hash = "";

        // Final hash for production version
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
        //return md5(hash);
    }

    // TODO http://javarevisited.blogspot.fi/2013/03/generate-md5-hash-in-java-string-byte-array-example-tutorial.html
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
}
