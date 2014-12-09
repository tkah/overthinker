package overthinker.net;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 * This class is used to send a new gravity tilt change request over the network. This class is only constructed by the
 * Otherthinker.
 *
 * @author Peter, Torran, Derek, Sid, and Josh.
 */
@Serializable
public class ChangeMapTiltRequest extends AbstractMessage {
    private boolean right = false;
    private boolean left = false;
    private boolean forward = false;
    private boolean back = false;

    /**
     * Used to construct a new message. Empty constructor required by Serializable.
     */
    public ChangeMapTiltRequest(){}

    /**
     * Returns the right gravity state.
     *
     * @return - is tilt to the right.
     */
    public boolean isRight() {
        return right;
    }

    /**
     * Sets the right gravity state.
     *
     * @param right - if gravity is to the right
     */
    public void setRight(boolean right) {
        this.right = right;
    }

    /**
     * Returns the left gravity state.
     *
     * @return - is tilt to the left.
     */
    public boolean isLeft() {
        return left;
    }

    /**
     * Sets the left gravity state.
     *
     * @param left - if gravity is to the left
     */
    public void setLeft(boolean left) {
        this.left = left;
    }

    /**
     * Returns the forward gravity state.
     *
     * @return - is tilt to the forward.
     */
    public boolean isForward() {
        return forward;
    }

    /**
     * Sets the forward gravity state.
     *
     * @param forward - if gravity is to the forward
     */
    public void setForward(boolean forward) {
        this.forward = forward;
    }

    /**
     * Returns the back gravity state.
     *
     * @return - is tilt to the back.
     */
    public boolean isBack() {
        return back;
    }

    /**
     * Sets the back gravity state.
     *
     * @param back - if gravity is to the back
     */
    public void setBack(boolean back) {
        this.back = back;
    }
}
