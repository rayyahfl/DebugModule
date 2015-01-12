package com.raizlabs.android.debugmodule;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author: andrewgrosner
 * Description: The main attacher to the {@link android.support.v4.app.FragmentActivity}.
 * Call {@link #attach(android.support.v4.app.FragmentActivity)} in your
 * {@link android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)} method.
 */
public class Debugger {

    private static Debugger debugger;

    public static Debugger getInstance() {
        if (debugger == null) {
            debugger = new Debugger();
        }

        return debugger;
    }

    private HashMap<String, Critter> mCritters = new HashMap<>();

    /**
     * Attaches itself to the activity as an overlay. Call this in {@link android.app.Activity#onResume()}. Make sure to attach
     * {@link com.raizlabs.android.debugmodule.Critter} before calling this method. The overlay is a right sided {@link android.support.v4.widget.DrawerLayout}
     *
     * @param activity The activity to attach to
     */
    public void attach(FragmentActivity activity) {
        // only attach if debug build
        FrameLayout root = (FrameLayout) activity.findViewById(android.R.id.content);
        View injectedView = LayoutInflater.from(activity).inflate(R.layout.view_debug_module_debugger, root, true);

        if (activity.getWindow().hasFeature(Window.FEATURE_ACTION_BAR_OVERLAY)) {
            TypedValue tv = new TypedValue();
            int actionBarHeight = 0;
            if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
            }
            injectedView.setPadding(injectedView.getPaddingLeft(), actionBarHeight,
                    injectedView.getPaddingRight(), injectedView.getPaddingBottom());
        }
        // Add the debug menu
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        Fragment fragment = new DebugMenuFragment();
        transaction.replace(R.id.view_debug_module_menu_drawer, fragment).commit();
    }

    /**
     * Attaches an array of {@link com.raizlabs.android.debugmodule.Critter} to use when we are in debug mode
     *
     * @param critterName The name of the critter
     * @param critter     A critter to attach
     */
    public Debugger use(String critterName, Critter critter) {
        mCritters.put(critterName, critter);
        return this;
    }

    public Critter getCritter(String critterName) {
        return mCritters.get(critterName);
    }

    public ArrayList<Critter> getCritters() {
        return new ArrayList<>(mCritters.values());
    }

    HashMap<String, Critter> getCritterMap() {
        return mCritters;
    }
}
