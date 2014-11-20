package overthinker.net.message;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import overthinker.Model;

/**
 * Created by Peter on 11/13/2014.
 */
@Serializable
public class ModelChangeRequest extends AbstractMessage{
    private Model model;
    private long version;

    public ModelChangeRequest () {}

    public ModelChangeRequest(Model model, long version){
        this.model = model;
        this.version = version;
    }

    public Model getModel() {
        return model;
    }

    public long getVersion() {
        return version;
    }
}
