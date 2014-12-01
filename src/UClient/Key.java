package UClient;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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

  public Key(String name)
  {
    super(name);

    Box key = new Box(.5f, 1.0f, .5f);
    geo = new Geometry(name, key);
    phy = new RigidBodyControl(0f);
    sparkEmitter = new ParticleEmitter("spark emitter", ParticleMesh.Type.Triangle, 60);
  }

  public void createKey(AssetManager assetManager, Vector3f loc)
  {
    Material keyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    keyMat.setColor("Color", ColorRGBA.Yellow);
    geo.setMaterial(keyMat);
    geo.setLocalTranslation(loc);
    geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    attachChild(geo);

    phy.setSpatial(geo);
    phy.setApplyPhysicsLocal(true);
    phy.setEnabled(true);

    Material sparkMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
    sparkMat.setTexture("Texture", assetManager.loadTexture("assets/effects/spark.png"));
    sparkEmitter.setLocalTranslation(new Vector3f(loc.getX(), loc.getY() + 0.5f, loc.getZ()));
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
}
