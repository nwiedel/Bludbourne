package com.nicolas.bludbourne;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class Utility {

    public static final String TAG = Utility.class.getSimpleName();

    public static final AssetManager assetManager = new AssetManager();

    public static InternalFileHandleResolver filePathResolver = new InternalFileHandleResolver();

    public static void unloadAsset(String assetFilenamePath){

        if(assetManager.isLoaded(assetFilenamePath)){
            assetManager.unload(assetFilenamePath);
        } else {
            Gdx.app.debug(TAG, "Asset is not loaded; Nothing to unload: " + assetFilenamePath);
        }
    }

    public static float loadCompleted(){
        return assetManager.getProgress();
    }

    public static int numAssetsQueued(){
        return assetManager.getQueuedAssets();
    }

    public static boolean updateAssetLoading(){
        return assetManager.update();
    }

    public static boolean isAssetLoaded(String fileName){
        return assetManager.isLoaded(fileName);
    }

    /* läd eine tiledmap */
    public static void loadMapAssets(String mapFilenamePath){
        if(mapFilenamePath == null || mapFilenamePath.isEmpty()){
            return;
        }

        // load asset
        if (filePathResolver.resolve(mapFilenamePath).exists()){
            assetManager.setLoader(TiledMap.class, new TmxMapLoader(filePathResolver));

            assetManager.load(mapFilenamePath, TiledMap.class);

            // Until we add loading screen, just block until we load the map
            assetManager.finishLoadingAsset(mapFilenamePath);
            Gdx.app.debug(TAG, "Map Loaded!: " + mapFilenamePath);
        } else {
            Gdx.app.debug(TAG, "Map does!: " + mapFilenamePath);
        }
    }

    /* getter für tiledmap */
    public static TiledMap getMapAsset(String mapFilenamePath){
        TiledMap map = null;

        // once the assetManager has done loading
        if(assetManager.isLoaded(mapFilenamePath)){
            map = assetManager.get(mapFilenamePath, TiledMap.class);
        } else {
            Gdx.app.debug(TAG, "Map is not loaded: " + mapFilenamePath);
        }
        return map;
    }

    /* läd texture assets */
    public static void loadTextureAsset(String textureFilenamePath){
        if(textureFilenamePath == null || textureFilenamePath.isEmpty()){
            return;
        }
        // load asset
        if(filePathResolver.resolve(textureFilenamePath).exists()){
            assetManager.setLoader(Texture.class, new TextureLoader(filePathResolver));

            assetManager.load(textureFilenamePath, Texture.class);
            // Until we add loading screen, just block until we load the texture
            assetManager.finishLoadingAsset(textureFilenamePath);
        }
        else {
            Gdx.app.debug(TAG, "Texture does not exist: " + textureFilenamePath);
        }
    }

    /* getter für texture assets */
    public static Texture getTextureAsset(String textureFilenamePath){
        Texture texture = null;

        // once asset manager has done loading
        if (assetManager.isLoaded(textureFilenamePath)){
            texture = assetManager.get(textureFilenamePath, Texture.class);
        }
        else {
            Gdx.app.debug(TAG, "Texture is not loaded: " + textureFilenamePath);
        }
        return texture;
    }
}
