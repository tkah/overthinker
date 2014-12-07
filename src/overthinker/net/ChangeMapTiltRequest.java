package overthinker.net;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * Created by Peter on 12/5/2014.
 */
@Serializable
public class ChangeMapTiltRequest extends AbstractMessage {
    private boolean right = false;
    private boolean left = false;
    private boolean forward = false;
    private boolean back = false;

    public ChangeMapTiltRequest(){}

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public boolean isBack() {
        return back;
    }

    public void setBack(boolean back) {
        this.back = back;
    }
}
