package overthinker.client;

import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.water.WaterFilter;
import overthinker.server.ServerModel;
import overthinker.levels.Level;
import overthinker.net.ModelChangeRequest;
import overthinker.net.ModelUpdate;
import overthinker.net.NewClientRequest;
import overthinker.net.NewClientResponse;

import java.io.IOException;

/**
 * Created by Peter on 11/12/2014.
 */
public class ClientMain extends SimpleApplication implements ActionListener, AnalogListener {

    private ServerModel model;
    private Client netClient = null;
    private Level level;

    public static void main(String[] args) {
        ClientMain app = new ClientMain();
        app.start(); // standard display type
    }

    public void simpleInitApp() {
        initNetClient();

        /** Set up Physics */
        stateManager.attach(level.getBulletAppState());
        rootNode.attachChild(level.getResources());
        viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

        setUpKeys();
        setUpLight();
        setUpLandscape();
        setUpWater();
        setUpPlayer();
        setUpCamera();
        createSphereResources();

        Globals.setUpTimer();
        Globals.startTimer();
        initAudio();
    }

    private void initAudio(){

        //collect object
        level.setAudioCollect(new AudioNode(assetManager, level.getAudioCollectLocation() ,false));
        level.getAudioCollect().setPositional(false);
        level.getAudioCollect().setVolume(2);
        rootNode.attachChild(level.getAudioCollect());

        //walking sounds
        level.setAudioFootsteps(new AudioNode(assetManager, level.getAudioFootstepsLocation() ,true));
        level.getAudioFootsteps().setPositional(false);
        level.getAudioFootsteps().setLooping(true);
        level.getAudioFootsteps().setVolume(2);
        rootNode.attachChild(level.getAudioFootsteps());


        //jumping sound
        level.setAudioJump(new AudioNode(assetManager, level.getAudioJumpLocation() ,false));
        level.getAudioJump().setPositional(false);
        level.getAudioJump().setLooping(false);
        level.getAudioJump().setVolume(2);
        rootNode.attachChild(level.getAudioJump());

        //ambient map sounds
        level.setAudioOcean(new AudioNode(assetManager, level.getAudioOceanLocation(),true));
        level.getAudioOcean().setLooping(true);
        level.getAudioOcean().setPositional(true);
        level.getAudioOcean().setVolume(1);
        rootNode.attachChild(level.getAudioOcean());
        level.getAudioOcean().play();
    }

    /** Method to add sounds when buttons are pressed **/
    private void addMovementSound(boolean emmit){
        if(emmit){
            level.getAudioFootsteps().play();
        }else{
            level.getAudioFootsteps().stop();
        }
    }

    private ActionListener jumpActionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float v) {
            if (name.equals("Jump") && keyPressed)
                level.getAudioJump().playInstance();

        }
    };

    /**
     * Custom actions for mouse actions
     * @param binding - name of key binding
     * @param value   - movement value
     * @param tpf     - time per frame
     */
    public void onAnalog(String binding, float value, float tpf)
    {
        if (binding.equals("TurnLeft"))
        {
            Quaternion turn = new Quaternion();
            turn.fromAngleAxis(FastMath.PI * value, Vector3f.UNIT_Y);
            level.getPlayerControl().setViewDirection(turn.mult(level.getPlayerControl().getViewDirection()));
        }
        else if (binding.equals("TurnRight"))
        {
            Quaternion turn = new Quaternion();
            turn.fromAngleAxis(-FastMath.PI * value, Vector3f.UNIT_Y);
            level.getPlayerControl().setViewDirection(turn.mult(level.getPlayerControl().getViewDirection()));
        }
        else if (binding.equals("MouseDown")) checkVertAngle(value);
        else if (binding.equals("MouseUp")) checkVertAngle(-value);

        level.getPivot().getLocalRotation().fromAngleAxis(level.getVerticalAngle(), Vector3f.UNIT_X);
    }
    private void checkVertAngle(float value)
    {
        float angle = FastMath.PI * value;
        level.setVerticalAngle(level.getVerticalAngle() + angle);
        if (level.getVerticalAngle() > level.getMaxVerticalAngle())
        {
            level.setVerticalAngle(level.getMaxVerticalAngle());
        }
        else if (level.getVerticalAngle() < level.getMinVerticalAngle())
        {
            level.setVerticalAngle(level.getMinVerticalAngle());
        }
    }
    private void createSphereResources() {
        for (int i = 0; i < level.getSphere_resource_count(); i++)
        {
            int x = Globals.getRandInt(Globals.MAP_WIDTH * 2) - Globals.MAP_WIDTH;
            int z = Globals.getRandInt(Globals.MAP_HEIGHT * 2) - Globals.MAP_HEIGHT;
            SphereResource sRes = new SphereResource(level.getSphere_resource_radius(), x, z, i, assetManager);
            level.getBulletAppState().getPhysicsSpace().add(sRes.getSphereResourcePhy());
            level.getSphereResource().add(sRes);
            level.getResources().attachChild(sRes.getGeometry());
        }
    }

    private void setUpCamera()  {
        flyCam.setMoveSpeed(100);

        // For third person cam
        // pivot node allows for mouse tracking of player character
        mouseInput.setCursorVisible(false);
        level.setCamNode(new CameraNode("Camera Node", cam));
        level.getCamNode().setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        level.getCamNode().setLocalTranslation(new Vector3f(0, 4, -18));
        level.getPivot().attachChild(level.getCamNode());
        Quaternion quat = new Quaternion();
        quat.lookAt(Vector3f.UNIT_Z, Vector3f.UNIT_Y);
        level.getPlayerNode().attachChild(level.getPivot());
        level.getCamNode().setEnabled(true);
        flyCam.setEnabled(false);
        level.getPivot().getLocalRotation().fromAngleAxis(level.getVerticalAngle(), Vector3f.UNIT_X);

        // For first person camera
        //camNode.lookAt(playerNode.getLocalTranslation(), Vector3f.UNIT_Y);
    }

    private void setUpPlayer()   {
        level.setPlayerSphere(new Sphere(32, 32, level.getPlayer_sphere_start_radius()));

        // Tutorial pond ball
        level.setPlayerG(new Geometry("Shiny rock", level.getPlayerSphere()));
        level.getPlayerSphere().setTextureMode(Sphere.TextureMode.Projected);
        TangentBinormalGenerator.generate(level.getPlayerSphere());
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
        mat.setTexture("NormalMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png"));
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 64f);
        level.getPlayerG().setMaterial(mat);
        level.getPlayerNode().attachChild(level.getPlayerG());

        level.setSphereShape(new SphereCollisionShape(level.getPlayer_sphere_start_radius()));
        level.setPlayerControl(new CharacterControl(level.getSphereShape(), 0.05f));
        level.getPlayerControl().setJumpSpeed(20);
        level.getPlayerControl().setFallSpeed(30);
        level.getPlayerControl().setGravity(30);
        level.getPlayerControl().setPhysicsLocation(new Vector3f(level.getSpawnX(),
                level.getSpawnY(), level.getSpawnZ()));
        level.getPlayerNode().setLocalTranslation(new Vector3f(level.getSpawnX(),
                level.getSpawnY(), level.getSpawnZ()));
        level.getPlayerNode().addControl(level.getPlayerControl());
        rootNode.attachChild(level.getPlayerNode());
        level.getBulletAppState().getPhysicsSpace().add(level.getPlayerControl());
    }

    private void setUpWater(){
        level.setFpp(new FilterPostProcessor(assetManager));
        level.setWater(new WaterFilter(rootNode, level.getLightDir()));
        level.getWater().setWaterHeight(level.getWaterHeight());
        level.getWater().setDeepWaterColor(new ColorRGBA(0.0f, 0.5f, 0.5f, 1.0f));
        level.getFpp().addFilter(level.getWater());
        viewPort.addProcessor(level.getFpp());
    }

    private void setUpLandscape() {
        /** Create terrain material and load four textures into it. */
        level.setMat_terrain(new Material(assetManager, level.getMatTerrainLocation()));

        /** Add ALPHA map (for red-blue-green coded splat textures) */
        level.getMat_terrain().setTexture("Alpha", assetManager.loadTexture(level.getMatTerrainAlphaTextureLocation()));

        /** Add GRASS texture into the red layer (Tex1). */
        Texture grass = assetManager.loadTexture(
                "Textures/Terrain/splat/grass.jpg");
        grass.setWrap(Texture.WrapMode.Repeat);
        level.getMat_terrain().setTexture("Tex1", grass);
        level.getMat_terrain().setFloat("Tex1Scale", 64f);

        /** Add DIRT texture into the green layer (Tex2) */
        Texture dirt = assetManager.loadTexture(
                "Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(Texture.WrapMode.Repeat);
        level.getMat_terrain().setTexture("Tex2", dirt);
        level.getMat_terrain().setFloat("Tex2Scale", 32f);

        /** Add ROAD texture into the blue layer (Tex3) */
        Texture rock = assetManager.loadTexture(
                "Textures/Terrain/splat/road.jpg");
        rock.setWrap(Texture.WrapMode.Repeat);
        level.getMat_terrain().setTexture("Tex3", rock);
        level.getMat_terrain().setFloat("Tex3Scale", 128f);

        /** Create the height map */
        AbstractHeightMap heightmap = null;
        Texture heightMapImage = assetManager.loadTexture(
                level.getHeightMapLocation());
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage());

        // Height Map Randomization
        /*HillHeightMap heightmap = null;
        HillHeightMap.NORMALIZE_RANGE = 100; // optional
        try {
            heightmap = new HillHeightMap(513, 1000, 5, 1000, (byte) 3); // byte 3 is a random seed
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/

        heightmap.load();

        /** We have prepared material and heightmap.
         * Now we create the actual terrain:
         * -Create a TerrainQuad and name it "my terrain".
         * -A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
         * -We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
         * -As LOD step scale we supply Vector3f(1,1,1).
         * -We supply the prepared heightmap itself.
         */
        int patchSize = 65;
        level.setTerrain(new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap()));

        /** We give the terrain its material, position & scale it, and attach it. */
        level.getTerrain().setMaterial(level.getMat_terrain());
        level.getTerrain().setLocalTranslation(0, 0, 0);
        level.getTerrain().setLocalScale(2f, 1f, 2f);
        rootNode.attachChild(level.getTerrain());

        /** The LOD (level of detail) depends on were the camera is: */
        TerrainLodControl control = new TerrainLodControl(level.getTerrain(), getCamera());
        level.getTerrain().addControl(control);

        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.
        CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape(level.getTerrain());
        level.setLandscape(new RigidBodyControl(sceneShape, 0));
        level.getTerrain().addControl(level.getLandscape());
        level.getBulletAppState().getPhysicsSpace().add(level.getLandscape());
    }

    private void initNetClient() {

        try {
            netClient = Network.connectToServer("localhost", 6143);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientNetListener listener = new ClientNetListener(this);

        Serializer.registerClass(ModelChangeRequest.class);
        Serializer.registerClass(ModelUpdate.class);
        Serializer.registerClass(NewClientRequest.class);
        Serializer.registerClass(NewClientResponse.class);

        netClient.addMessageListener(listener, ModelUpdate.class);
        netClient.addMessageListener(listener, NewClientResponse.class);

        netClient.start();
        netClient.send(new NewClientRequest());

        while(level == null)
        {
            System.out.println("Waiting For Model Data...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        //dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        dl.setDirection(level.getLightDir().normalizeLocal());
        rootNode.addLight(dl);
    }

    /** We over-write some navigational key mappings here, so we can
     * add physics-controlled walking and jumping: */
    private void setUpKeys() {
        // Mouse pivoting for 3rd person cam
        inputManager.addMapping("TurnLeft", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("TurnRight", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("MouseDown", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("MouseUp", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addListener(this, "TurnLeft");
        inputManager.addListener(this, "TurnRight");
        inputManager.addListener(this, "MouseDown");
        inputManager.addListener(this, "MouseUp");

        // Basic character movement
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
        inputManager.addListener(jumpActionListener,"Jump");


        // Tilting map, to be replaced by headset commands
        inputManager.addMapping("MapTiltBack", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("MapTiltLeft", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("MapTiltRight", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("MapTiltForward", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener(this, "MapTiltBack");
        inputManager.addListener(this, "MapTiltLeft");
        inputManager.addListener(this, "MapTiltRight");
        inputManager.addListener(this, "MapTiltForward");

        // Lower water level, to be replaced by headset commands
        inputManager.addMapping("SlowWater", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addListener(this, "SlowWater");
    }

    /** These are our custom actions triggered by key presses.
     * We do not walk yet, we just keep track of the direction the user pressed. */
    public void onAction(String binding, boolean isPressed, float tpf) {
        if (binding.equals("Left")) level.setLeft(isPressed);
        else if (binding.equals("Right")) level.setRight(isPressed);
        else if (binding.equals("Up")) level.setUp(isPressed);
        else if (binding.equals("Down")) level.setDown(isPressed);
        else if (binding.equals("SlowWater")) level.setSlowWater(isPressed);
        else if (binding.equals("MapTiltBack")) level.setMapTiltBack(isPressed);
        else if (binding.equals("MapTiltForward")) level.setMapTiltForward(isPressed);
        else if (binding.equals("MapTiltLeft")) level.setMapTiltLeft(isPressed);
        else if (binding.equals("MapTiltRight")) level.setMapTiltRight(isPressed);
        else if (binding.equals("Jump"))
        {
            if (isPressed) level.getPlayerControl().jump();
        }
    }

    /**
     * This is the main event loop--walking happens here.
     * We check in which direction the player is walking by interpreting
     * the camera direction forward (camDir) and to the side (camLeft).
     * The setWalkDirection() command is what lets a physics-controlled player walk.
     * We also make sure here that the camera moves with player.
     */
    @Override
    public void simpleUpdate(float tpf){
        // Raise Water Level, to be controlled by EEG
        if (!level.isSlowWater()) level.getWater().setWaterHeight(level.getWater().getWaterHeight() + level.getWater_height_default_rate());
        else level.getWater().setWaterHeight(level.getWater().getWaterHeight() + level.getWater_height_player_rate());

        // Tilt Map, to be controlled by EEG gyroscope
        if (!level.isMapTiltLeft() && !level.isMapTiltRight() && !level.isMapTiltBack() && !level.isMapTiltForward()) // EEG not rotating, move back to normal
        {
            if (level.getTiltMapX() < 0 && level.getTiltMapY() > 0)
            {
                level.setTiltMapX(level.getTiltMapX() + level.getMap_tilt_rate());
                level.setTiltMapY(level.getTiltMapY() - level.getMap_tilt_rate());
            }
            if (level.getTiltMapX() < 0 && level.getTiltMapY() < 0)
            {
                level.setTiltMapX(level.getTiltMapX() + level.getMap_tilt_rate());
                level.setTiltMapY(level.getTiltMapY() + level.getMap_tilt_rate());
            }
            else if (level.getTiltMapX() > 0 && level.getTiltMapY() < 0)
            {
                level.setTiltMapX(level.getTiltMapX() - level.getMap_tilt_rate());
                level.setTiltMapY(level.getTiltMapY() - level.getMap_tilt_rate());
            }
            else if (level.getTiltMapX() > 0 && level.getTiltMapY() > 0)
            {
                level.setTiltMapX(level.getTiltMapX() - level.getMap_tilt_rate());
                level.setTiltMapY(level.getTiltMapY() - level.getMap_tilt_rate());
            }
            else if (level.getTiltMapX() < 0) level.setTiltMapX(level.getTiltMapX() + level.getMap_tilt_rate());
            else if (level.getTiltMapX() > 0) level.setTiltMapX(level.getTiltMapX() - level.getMap_tilt_rate());
            else if (level.getTiltMapX() > 0) level.setTiltMapY(level.getTiltMapY() - level.getMap_tilt_rate());
            else if (level.getTiltMapX() < 0) level.setTiltMapY(level.getTiltMapY() + level.getMap_tilt_rate());
        }

        //TODO: CHECK FOR CONSISTANCY
        if (level.isMapTiltForward()&&level.isMapTiltLeft())
        {
            level.setTiltMapY(level.getTiltMapY() + level.getMap_tilt_rate());
            level.setTiltMapX(level.getTiltMapX() + level.getMap_tilt_rate());
        }
        else if (level.isMapTiltForward()&&level.isMapTiltRight())
        {
            level.setTiltMapY(level.getTiltMapY() - level.getMap_tilt_rate());
            level.setTiltMapX(level.getTiltMapX() - level.getMap_tilt_rate());
        }
        else if (level.isMapTiltBack()&&level.isMapTiltLeft())
        {
            level.setTiltMapY(level.getTiltMapY() - level.getMap_tilt_rate());
            level.setTiltMapX(level.getTiltMapX() + level.getMap_tilt_rate());
        }
        else if (level.isMapTiltBack()&&level.isMapTiltRight())
        {
            level.setTiltMapY(level.getTiltMapY() - level.getMap_tilt_rate());
            level.setTiltMapX(level.getTiltMapX() - level.getMap_tilt_rate());
        }
        else if (level.isMapTiltLeft()) level.setTiltMapX(level.getTiltMapX() + level.getMap_tilt_rate());
        else if (level.isMapTiltRight()) level.setTiltMapX(level.getTiltMapX() - level.getMap_tilt_rate());
        else if (level.isMapTiltForward()) level.setTiltMapY(level.getTiltMapY() + level.getMap_tilt_rate());
        else if (level.isMapTiltBack()) level.setTiltMapY(level.getTiltMapY() - level.getMap_tilt_rate());
        tiltMap();

        // Control Movement and Player Rotation
        level.getCamDir().set(cam.getDirection()).multLocal(0.6f); //20f for BetterCharacterControl
        level.getCamDir().setY(0); // Keep from flying into space when camera angle looking skyward
        level.getCamLeft().set(cam.getLeft()).multLocal(0.4f); //20f for BetterCharacterControl
        level.getWalkDirection().set(0, 0, 0);

        if (level.isLeft() || level.isRight() || level.isUp() || level.isDown())
        {
            level.setRotation(level.getRotation()+3);
        }
        if (level.isLeft()) moveBall(0, -1.0f, level.getCamLeft());
        if (level.isRight()) moveBall(0, 1.0f, level.getCamLeft().negate());
        if (level.isUp()) moveBall(1.0f, 0, level.getCamDir());
        if (level.isDown()) moveBall(-1.0f,0, level.getCamDir().negate());
        if (level.isUp()&&level.isRight()) moveBall(1.0f, 1.0f, null);
        if (level.isUp()&&level.isLeft()) moveBall(1.0f, -1.0f, null);
        if (level.isDown()&&level.isRight()) moveBall(-1.0f, 1.0f, null);
        if (level.isDown()&&level.isLeft()) moveBall(-1.0f, -1.0f, null);

        addMovementSound((level.isUp() || level.isDown() || level.isLeft() || level.isRight()));

        level.getPlayerControl().setWalkDirection(level.getWalkDirection());

        // Collision Scaling
        if (level.isPlayerNeedsScaling()) scalePlayer();
        boolean clear = true;
        for (SphereResource s : level.getSphereResourcesToShrink())
        {
            clear = false;
            if (s.getShrink()) s.setSphereToDisappear();
            else s.getGeometry().removeFromParent();
        }
        if (clear) level.getSphereResourcesToShrink().clear();

        CollisionResults results = new CollisionResults();
        level.getResources().collideWith(level.getPlayerG().getWorldBound(), results);


        if (results.size() > 0)
        {
            level.getAudioCollect().play();
            CollisionResult closest = results.getClosestCollision();
            System.out.println("What was hit? " + closest.getGeometry().getName());

            boolean isHit = closest.getGeometry().getUserData("isHit");
            if (!isHit)
            {
                int sResId = closest.getGeometry().getUserData("id");
                closest.getGeometry().setUserData("isHit", true);
                SphereResource s = level.getSphereResource().get(sResId);
                s.setShrink(true);
                level.getSphereResourcesToShrink().add(s);
                level.setScaleStartTime(Globals.getTotSecs());
                level.setPlayerNeedsScaling(true);
                scalePlayer();

            }
        }
        //move the audio with the camera
        listener.setLocation(cam.getLocation());
        listener.setRotation(cam.getRotation());
    }

    private void moveBall(float x, float z, Vector3f c)
    {
        Quaternion ballRotate = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * level.getRotation(), new Vector3f(x, 0,z));
        level.getPlayerG().setLocalRotation(ballRotate);
        if (c != null) level.getWalkDirection().addLocal(c);
    }

    private void tiltMap() {
        level.setMapTilt(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * level.getTiltMapX(), new Vector3f(0, 0, 1.0f)));
        Quaternion q = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * level.getTiltMapY(), new Vector3f(1.0f, 0, 0));
        Quaternion m = level.getMapTilt().mult(q);
        level.getTerrain().setLocalRotation(m);
        level.getLandscape().setPhysicsRotation(m);
    }

    private void scalePlayer()
    {
        int curTime = Globals.getTotSecs();
        int duration;

        level.getPlayerNode().scale(Globals.SCALE_BY);
        level.getSphereShape().setScale(level.getPlayerNode().getWorldScale());
        duration = curTime - level.getScaleStartTime();
        if (duration >= Globals.SCALE_ANIM_TIME) level.setPlayerNeedsScaling(false);
    }

    public CharacterControl getPlayer() {
        return level.getPlayerControl();
    }


    public void setLevel(Level level){
        this.level = level;
    }

    public Level getLevel(){
        return level;
    }
}
