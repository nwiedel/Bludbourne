package com.nicolas.bludbourne.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nicolas.bludbourne.Entity;
import com.nicolas.bludbourne.MapManager;

public class MainGameScreen implements Screen {

    public static final String TAG = MainGameScreen.class.getSimpleName();

    private static class VIEWPORT{
        static float viewportWidth;
        static float viewportHeight;
        static float virtualWidth;
        static float virtualHeight;
        static float physicalWidth;
        static float physicalHeight;
        static float aspectRatio;
    }

    private PlayerController controller;
    private TextureRegion currentPlayerFrame;
    private Sprite currentPlayerSprite;

    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private static MapManager mapMgr;

    private static Entity player;

    public MainGameScreen(){
        mapMgr = new MapManager();
    }

    @Override
    public void show() {
        // camera setup
        setupViewport(10, 10);

        // get the current size
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEWPORT.viewportWidth, VIEWPORT.viewportHeight);

        mapRenderer = new OrthogonalTiledMapRenderer(mapMgr.getCurrentMap(), MapManager.UNIT_SCALE);
        mapRenderer.setView(camera);

        Gdx.app.debug(TAG, "UnitScale value is: " + mapRenderer.getUnitScale());

        player = new Entity();
        player.init(mapMgr.getPlayerStartUnitScaled().x, mapMgr.getPlayerStartUnitScaled().y);

        currentPlayerSprite = player.getFrameSprite();

        comtroller = new PlayerController();
        Gdx.input.setInputProcessor(controller);
    }

    @Override
    public void hide() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.39f, 0.58f, 0.93f, 0f);

        // lock the center of the camera to the player position
        camera.position.set(currentPlayerSprite.getX(), currentPlayerSprite.getY(),0);
        camera.update();

        player.update(delta);
        currentPlayerFrame = player.getFrame();

        updatePortalLayerActivation(player.boundingBox);

        if(!isCollisionWithMapLayer(player.boundingBox)){
            player.setNextPositionToCurrent();
        }
        controller.update();

        mapRenderer.setView(camera);
        mapRenderer.render();

        mapRenderer.getBatch().begin();
        mapRenderer.getBatch().draw(currentPlayerFrame,
            currentPlayerSprite.getX(), currentPlayerSprite.getY());
        mapRenderer.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        player.dispose();
        controller.dispose();
        Gdx.input.setInputProcessor(null);
        mapRenderer.dispose();
    }

    private void setupViewport(int width, int height){
        // Make the viewport a percentage of the total display area.
        VIEWPORT.virtualWidth = width;
        VIEWPORT.virtualHeight = height;

        // Current viewport dimension
        VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
        VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;

        // Pixel dimension of display
        VIEWPORT.physicalWidth = Gdx.graphics.getWidth();
        VIEWPORT.physicalHeight = Gdx.graphics.getHeight();

        // Aspect ratio for the current viewport
        VIEWPORT.aspectRatio = VIEWPORT.viewportWidth / VIEWPORT.virtualHeight;

        // update viewport if there could be skewing
        if(VIEWPORT.viewportWidth / VIEWPORT.virtualHeight >= VIEWPORT.aspectRatio){
            // Letterbox left and right
            VIEWPORT.viewportWidth = VIEWPORT.viewportHeight * (VIEWPORT.physicalWidth/VIEWPORT.physicalHeight);
            VIEWPORT.viewportHeight = VIEWPORT.virtualHeight;
        }else{
            // Letterbox above and below
            VIEWPORT.viewportWidth = VIEWPORT.virtualWidth;
            VIEWPORT.viewportHeight = VIEWPORT.viewportWidth * (VIEWPORT.physicalHeight/VIEWPORT.physicalWidth);
        }

        Gdx.app.debug(TAG, "WorldRenderer virtual: (" + VIEWPORT.virtualWidth + "," + VIEWPORT.virtualHeight + ")");
        Gdx.app.debug(TAG, "WorldRenderer viewport: (" + VIEWPORT.viewportWidth + "," + VIEWPORT.viewportHeight + ")");
        Gdx.app.debug(TAG, "WorldRenderer physical: (" + VIEWPORT.physicalWidth + "," + VIEWPORT.physicalHeight + ")");

    }

    private boolean isCollisionWithMapLayer(Rectangle boundingBox){
        MapLayer mapCollisionLayer = mapMgr.getCollisionLayer();

        if (mapCollisionLayer == null){
            return false;
        }

        Rectangle rectangle = null;

        for (MapObject object : mapCollisionLayer.getObjects()){
            if(object instanceof RectangleMapObject){
                rectangle =(((RectangleMapObject) object).getRectangle());
                if (boundingBox.overlaps(rectangle)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean updatePortalLayerActivation(Rectangle boundingBox){
        MapLayer mapPortalLayer = mapMgr.getPortalLayer();

        if (mapPortalLayer == null){
            return false;
        }

        Rectangle rectangle = null;

        for (MapObject object : mapPortalLayer.getObjects()){
            if (object instanceof RectangleMapObject){
                rectangle = ((RectangleMapObject)object).getRectangle();
                if (boundingBox.overlaps(rectangle)){
                    String mapName = object.getName();
                    if (mapName == null){
                        return false;
                    }
                    mapMgr.setClosestStartPositionFromScaledUnits(player.getCurrentPosition());
                    mapMgr.loadMap(mapName);
                    player.init(mapMgr.getPlayerStartUnitScaled().x, mapMgr.getPlayerStartUnitScaled().y);
                    mapRenderer.setMap(mapMgr.getCurrentMap());
                    Gdx.app.debug(TAG, "Portal activated!");
                    return true;
                }
            }
        }
        return false;
    }
}
