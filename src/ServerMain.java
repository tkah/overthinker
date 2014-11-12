import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.JmeContext;

import java.io.IOException;

/**
 * Created by sidholman on 11/11/14.
 */
public class ServerMain extends SimpleApplication{

    public static void main(String[] args) {
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() {
        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);


        Server myServer = null;
        try {
            myServer = Network.createServer(6143);
            myServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
