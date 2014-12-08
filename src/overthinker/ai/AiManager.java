package overthinker.ai;

import com.jme3.ai.navmesh.NavMesh;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.ai.navmesh.Path;
import com.jme3.ai.steering.behaviour.Persuit;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import overthinker.client.GamePlayAppState;

/**
 * Created by jdrid_000 on 12/7/2014.
 */
public class AiManager extends AbstractAppState
{
  private SimpleApplication app;
  private NavMeshPathfinder navi;
  private AiNode aiPlayer;
  private Persuit p;

  @Override
  public void initialize(AppStateManager stateManager, Application app)
  {
    super.initialize(stateManager, app);
    this.app = (SimpleApplication) app;
    this.aiPlayer = new AiNode(1, app.getAssetManager());

    stateManager.getState(GamePlayAppState.class).bulletAppState.getPhysicsSpace().addAll(aiPlayer);
    stateManager.getState(GamePlayAppState.class).getLocalRootNode().attachChild(aiPlayer);

    aiPlayer.phys.warp(new Vector3f(0, 150, 0));

    NavMesh navMesh = stateManager.getState(GamePlayAppState.class).navMesh;
    navi = new NavMeshPathfinder(navMesh);
    navi.computePath(new Vector3f(-100, 0, -100));
    p = new Persuit();
    System.out.println("HG");
  }

  @Override
  public void update(float tpf)
  {
    //followPath(tpf);
    steerTarget(tpf);
  }

  private void steerTarget(float tpf)
  {
    Vector3f target = ((Node) app.getStateManager().getState(
          GamePlayAppState.class).getPlayerNode()).getWorldTranslation();
    Vector3f go = p.calculateForce(aiPlayer.getWorldTranslation(), Vector3f.ZERO, 10, 10, tpf,
                                   Vector3f.UNIT_XYZ.mult(8f), target);
    aiPlayer.phys.setWalkDirection(go);
  }

  private void followPath(float tpf)
  {
    Path.Waypoint waypoint = navi.getNextWaypoint();
    if (waypoint == null)
    {
      return;
    }
    Vector3f vector = waypoint.getPosition().subtract(aiPlayer.getWorldTranslation());
    if (vector.length() > 1)
    {
      System.out.println(vector.normalize());
      aiPlayer.phys.setWalkDirection(vector.normalize().multLocal(20));
    }
    else
    {
      navi.goToNextWaypoint();
    }
  }
}
