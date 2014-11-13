package overthinker.client;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.post.FilterPostProcessor;
import com.jme3.system.JmeContext;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.water.WaterFilter;
import overthinker.net.message.NewClientRequestMessage;
import overthinker.net.message.NewClientResponseMessage;


import java.io.IOException;

/**
 * Created by Peter on 11/12/2014.
 */
public class ClientMain extends SimpleApplication {
    private Client netClient = null;
    private ClientGameData clientGameData;
    private WaterFilter water;
    private FilterPostProcessor fpp;
    private TerrainQuad terrain;
    private Material mat_terrain;
    private BulletAppState bulletAppState;
    private CharacterControl player;
    private ClientInputListener inputListener = new ClientInputListener(this);
    public boolean left = false, right = false, up = false, down = false;

    public static void main(String[] args) {
        ClientMain app = new ClientMain();
        app.start(JmeContext.Type.Display); // standard display type
    }

    public void simpleInitApp() {
        initNetClient();
        setUpClient();
    }

    private void setUpClient() {

        netClient.send(new NewClientRequestMessage());
        while(clientGameData == null)
        {
            System.out.println("Waiting For Game Data...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Received game data");
//        bulletAppState = new BulletAppState();
//        stateManager.attach(bulletAppState);
//        viewPort.setBackgroundColor(clientGameData.getBackgroundColor());
//        flyCam.setMoveSpeed(100);
//        setUpKeys();
//        setUpLight();
//
//        fpp = new FilterPostProcessor(assetManager);
//        water = new WaterFilter(rootNode, clientGameData.getLightDir());
//        water.setWaterHeight(clientGameData.getWaterHeight());
//        fpp.addFilter(water);
//        viewPort.addProcessor(clientGameData.getFpp());
//        mat_terrain = new Material(assetManager,
//                "Common/MatDefs/Terrain/Terrain.j3md");
//
//        /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
//        mat_terrain.setTexture("Alpha", assetManager.loadTexture(
//                "overthinker/assets/terrains/maze1color.png"));
//
//        /** 1.2) Add GRASS texture into the red layer (Tex1). */
//        Texture grass = assetManager.loadTexture(
//                "Textures/Terrain/splat/grass.jpg");
//        grass.setWrap(Texture.WrapMode.Repeat);
//        mat_terrain.setTexture("Tex1", grass);
//        mat_terrain.setFloat("Tex1Scale", 64f);
//
//        /** 1.3) Add DIRT texture into the green layer (Tex2) */
//        Texture dirt = assetManager.loadTexture(
//                "Textures/Terrain/splat/dirt.jpg");
//        dirt.setWrap(Texture.WrapMode.Repeat);
//        mat_terrain.setTexture("Tex2", dirt);
//        mat_terrain.setFloat("Tex2Scale", 32f);
//
//        /** 1.4) Add ROAD texture into the blue layer (Tex3) */
//        Texture rock = assetManager.loadTexture(
//                "Textures/Terrain/splat/road.jpg");
//        rock.setWrap(Texture.WrapMode.Repeat);
//        mat_terrain.setTexture("Tex3", rock);
//        mat_terrain.setFloat("Tex3Scale", 128f);
//
//        /** 2. Create the height map */
//        AbstractHeightMap heightmap = null;
//        Texture heightMapImage = assetManager.loadTexture(
//                "overthinker/assets/terrains/maze1.jpg");
//        heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
//
//        /*HillHeightMap heightmap = null;
//        HillHeightMap.NORMALIZE_RANGE = 100; // optional
//        try {
//            heightmap = new HillHeightMap(513, 1000, 5, 1000, (byte) 3); // byte 3 is a random seed
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }*/
//
//        heightmap.load();
//
//        /** 3. We have prepared material and heightmap.
//         * Now we create the actual terrain:
//         * 3.1) Create a TerrainQuad and name it "my terrain".
//         * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
//         * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
//         * 3.4) As LOD step scale we supply Vector3f(1,1,1).
//         * 3.5) We supply the prepared heightmap itself.
//         */
//        int patchSize = 65;
//        terrain = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());
//
//        /** 4. We give the terrain its material, position & scale it, and attach it. */
//        terrain.setMaterial(mat_terrain);
//        terrain.setLocalTranslation(0, 0, 0);
//        terrain.setLocalScale(2f, 1f, 2f);

    }

    private void initNetClient() {
        try {
            netClient = Network.connectToServer("localhost", 6143);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientNetListener listener = new ClientNetListener(this);

        Serializer.registerClass(NewClientRequestMessage.class);
        Serializer.registerClass(NewClientResponseMessage.class);

        netClient.addMessageListener(listener, NewClientResponseMessage.class);

        netClient.start();
    }

    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }

    /** We over-write some navigational key mappings here, so we can
     * add physics-controlled walking and jumping: */
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(inputListener, "Left");
        inputManager.addListener(inputListener, "Right");
        inputManager.addListener(inputListener, "Up");
        inputManager.addListener(inputListener, "Down");
        inputManager.addListener(inputListener, "Jump");
    }

    public void setClientGameData(ClientGameData clientGameData) {
        this.clientGameData = clientGameData;
    }

    public CharacterControl getPlayer() {
        return player;
    }
}
