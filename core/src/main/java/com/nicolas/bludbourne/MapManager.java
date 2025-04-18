package com.nicolas.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;

import java.util.Hashtable;

public class MapManager {

    private static final String TAG = MapManager.class.getSimpleName();

    // All maps for the game
    private Hashtable<String, String> mapTable;
    private Hashtable<String, Vector2> playerStartLocationTable;

    // maps
    private final static String TOP_WORLD = "TOP_WORLD";
    private final static String TOWN = "TOWN";
    private final static String CASTLE_OF_DOOM = "CASTLE_OF_DOOM";

    // Map layers
    private final static String MAP_COLLISION_LAYER = "MAP_COLLISION_LAYER";
    private final static String MAP_SPAWNS_LAYER = "MAP_SPAWNS_LAYER";
    private final static String MAP_PORTAL_LAYER = "MAP_PORTAL_LAYER";

    private final static String PLAYER_START = "PLAYER_START";

    private Vector2 playerStartPositionRect;
    private Vector2 closestPlayerStartPosition;
    private Vector2 convertedUnits;

    private Vector2 playerStart;
    private TiledMap currentMap = null;
    private String currentMapName;
    private MapLayer collisionLayer;
    private MapLayer portalLayer;
    private MapLayer spawnsLayer;

    public final static float UNIT_SCALE = 1/16f;

    public MapManager(){
        playerStart = new Vector2(0, 0);
        mapTable = new Hashtable<>();

        mapTable.put(TOP_WORLD, "maps/topworld.tmx");
        mapTable.put(TOWN, "maps/town.tmx");
        mapTable.put(CASTLE_OF_DOOM, "maps/castle_of_doom");

        playerStartLocationTable = new Hashtable<>();
        playerStartLocationTable.put(TOP_WORLD, playerStart.cpy());
        playerStartLocationTable.put(TOWN, playerStart.cpy());
        playerStartLocationTable.put(CASTLE_OF_DOOM, playerStart.cpy());

        playerStartPositionRect = new Vector2(0, 0);
        closestPlayerStartPosition = new Vector2(0, 0);
        convertedUnits = new Vector2(0, 0);
    }

    public void loadMap(String mapName){
        playerStart.set(0, 0);

        String mapFullPath = mapTable.get(mapName);

        if (mapFullPath == null || mapFullPath.isEmpty()){
            Gdx.app.debug(TAG, "Map is invalid!");
            return;
        }

        if(currentMap != null){
            currentMap.dispose();
        }

        Utility.loadMapAssets(mapFullPath);
        if (Utility.isAssetLoaded(mapFullPath)){
            currentMap = Utility.getMapAsset(mapFullPath);
            currentMapName = mapName;
        }
        else{
            Gdx.app.debug(TAG, "Map not loaded!");
            return;
        }

        collisionLayer = currentMap.getLayers().get(MAP_COLLISION_LAYER);
        if (collisionLayer == null){
            Gdx.app.debug(TAG, "No collision layer!");
        }
        portalLayer = currentMap.getLayers().get(MAP_PORTAL_LAYER);
        if (portalLayer == null){
            Gdx.app.debug(TAG, "No portal layer!");
        }
        spawnsLayer = currentMap.getLayers().get(MAP_SPAWNS_LAYER);
        if (spawnsLayer == null){
            Gdx.app.debug(TAG, "No spawns layer!");
        }
        else{
            Vector2 start = playerStartLocationTable.get(currentMapName);
            if (start.isZero()){
                setClosestStartPosition(playerStart);
                start = playerStartLocationTable.get(currentMapName);
            }
            playerStart.set(start.x, start.y);
        }

        Gdx.app.debug(TAG, "Player Start: (" + playerStart.x + "," + playerStart.y + ")");
    }

    public TiledMap getCurrentMap(){
        if(currentMap == null){
            currentMapName = TOWN;
            loadMap(currentMapName);
        }
        return currentMap;
    }

    public MapLayer getCollisionLayer(){
        return collisionLayer;
    }

    public MapLayer getPortalLayer(){
        return portalLayer;
    }

    public Vector2 getPlayerStartUnitScaled(){
        Vector2 _playerStart = playerStart.cpy();
        _playerStart.set(playerStart.x * UNIT_SCALE, playerStart.y * UNIT_SCALE);
        return _playerStart;
    }

    private void setClosestStartPosition(final Vector2 position){
        // Get last known position on this map
        playerStartPositionRect.set(0, 0);
        closestPlayerStartPosition.set(0, 0);
        float shortestDistance = 0f;

        // Go through all player start positions and choose closest to last known position
        for (MapObject object : spawnsLayer.getObjects()){
            if (object.getName().equalsIgnoreCase(PLAYER_START)){
                ((RectangleMapObject)object).getRectangle().getPosition(playerStartPositionRect);
                float distance = position.dst2(playerStartPositionRect);

                if (distance < shortestDistance || shortestDistance == 0){
                    closestPlayerStartPosition.set(playerStartPositionRect);
                    shortestDistance = distance;
                }
            }
        }
        playerStartLocationTable.put(currentMapName, closestPlayerStartPosition.cpy());
    }

    public void setClosestStartPositionFromScaledUnits(Vector2 position){
        if(UNIT_SCALE <= 0){
            return;
        }

        convertedUnits.set(position.x / UNIT_SCALE, position.y / UNIT_SCALE);
        setClosestStartPosition(convertedUnits);
    }
}
