package android.bluetooth;

import android.util.Log;

/**
 * Created by qinlin1 on 2016/4/12.
 */
public class BluetoothDevice {

    public static final int BOND_NONE = 10;
    public static final int BOND_BONDED = 12;

    public static final int ERROR = Integer.MIN_VALUE;
    public static final String EXTRA_RSSI = "android.bluetooth.device.extra.RSSI";
    public static final String EXTRA_NAME = "android.bluetooth.device.extra.NAME";
    public static final String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
    public static final String EXTRA_BOND_STATE = "android.bluetooth.device.extra.BOND_STATE";
    public static final String ACTION_FOUND =
            "android.bluetooth.device.action.FOUND";
    public static final String ACTION_BOND_STATE_CHANGED =
            "android.bluetooth.device.action.BOND_STATE_CHANGED";

    public String getName() {
        return "";
    }
    public boolean createBond(){
        return false;
    }
    public boolean removeBond() {
        return false;
    }
    public int getBondState(){
        return 0;
    }
}
