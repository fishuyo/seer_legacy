package example;

import jopenvr.VRControllerState_t;
import openvrprovider.ControllerListener;

import static openvrprovider.OpenVRUtil.switchedDown;
import static openvrprovider.OpenVRUtil.switchedUp;

/* Show how the controller listener works. */
public class SampleControllerListener implements ControllerListener {
    @Override
    public void buttonStateChanged(VRControllerState_t stateBefore, VRControllerState_t stateAfter, int nController) {
        System.out.println("controller state changed.");
        if (switchedDown(k_buttonTrigger, stateBefore.ulButtonPressed, stateAfter.ulButtonPressed)) {
            System.out.println("Trigger down.");
        }
        if (switchedUp(k_buttonTrigger, stateBefore.ulButtonPressed, stateAfter.ulButtonPressed)) {
            System.out.println("Trigger up.");
        }
    }
}
