package overthinker.client;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Created by Torran on 11/30/14.
 */
public class Key extends NonPlayableObjectNode
{
  private ParticleEmitter sparkEmitter;
  private float rotation;
  private float rotSpeed = 300f;
  private float updateHeight;
  private float updateSparkHeight;
  private boolean moveUp = false;

  public Key(String name)
  {
    super(name);

    Box key = new Box(.5f, 1.0f, .5f);
    geo = new Geometry(name, key);
    phy = new RigidBodyControl(0f);
    sparkEmitter = new ParticleEmitter("spark emitter", ParticleMesh.Type.Triangle, 60);

    int id = Integer.parseInt(name.split("_")[1]);
    geo.setUserData("id", id);
  }

  public void createKey(AssetManager assetManager, Vector3f loc)
  {
    Material keyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    keyMat.setColor("Color", ColorRGBA.Yellow);
    geo.setMaterial(keyMat);
    geo.setLocalTranslation(loc);
    geo.setShadowMode(RenderQueue.ShadowMode.Cast);
    attachChild(geo);
    this.loc = loc;

    phy.setSpatial(geo);
    phy.setApplyPhysicsLocal(true);
    phy.setEnabled(true);

    updateHeight = loc.getY();
    updateSparkHeight = loc.getY() + 0.5f;

    Material sparkMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
    sparkMat.setTexture("Texture", assetManager.loadTexture("overthinker/assets/effects/spark.png"));
    sparkEmitter.setLocalTranslation(new Vector3f(loc.getX(), updateSparkHeight, loc.getZ()));
    sparkEmitter.setMaterial(sparkMat);
    sparkEmitter.setImagesX(1);
    sparkEmitter.setImagesY(1);
    sparkEmitter.setStartColor(ColorRGBA.Yellow);
    sparkEmitter.setEndColor(ColorRGBA.Red);
    sparkEmitter.setGravity(0, 50, 0);
    sparkEmitter.setFacingVelocity(true);
    sparkEmitter.setStartSize(.5f);
    sparkEmitter.setEndSize(.5f);
    sparkEmitter.setLowLife(.9f);
    sparkEmitter.setHighLife(1.1f);
    sparkEmitter.setRotateSpeed(4);
    sparkEmitter.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 10, 0));
    sparkEmitter.setSelectRandomImage(true);
    sparkEmitter.setRandomAngle(true);
    sparkEmitter.getParticleInfluencer().setVelocityVariation(1.0f);
    attachChild(sparkEmitter);
  }

  public ParticleEmitter getParticleEmitter()
  {
    return sparkEmitter;
  }

  public void update(float tpf)
  {
    rotation += tpf*rotSpeed;

    if (updateHeight >= loc.getY() + 0.3f) moveUp = false;
    else if (updateHeight <= loc.getY() - 0.3f) moveUp = true;

    if (moveUp)
    {
      updateHeight += tpf;
      updateSparkHeight += tpf;
    }
    else
    {
      updateHeight -= tpf;
      updateSparkHeight -= tpf;
    }

    geo.setLocalTranslation(loc.getX(),updateHeight,loc.getZ());
    sparkEmitter.setLocalTranslation(loc.getX(),updateSparkHeight,loc.getZ());

    Quaternion ballRotate = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD * rotation, new Vector3f(0, 1, 0));
    geo.setLocalRotation(ballRotate);
  }
}
