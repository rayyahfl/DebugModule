package com.raizlabs.android.debugmodule.preference;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.debugmodule.R;

/**
 * Description:
 */
public class PreferenceView extends LinearLayout {

    TextView title;

    EditText valueChooser;

    Switch booleanSwitch;

    PreferenceChangeListener preferenceChangeListener;

    PreferenceBuilder preferenceBuilder;

    public PreferenceView(Context context) {
        super(context);
        init(context);
    }

    public PreferenceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PreferenceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PreferenceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_debug_module_preference, this, true);
        title = (TextView) findViewById(R.id.view_debug_module_preference_title);
        valueChooser = (EditText) findViewById(R.id.view_debug_module_preference_value);
        // tapping enter changes value
        valueChooser.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    if(preferenceBuilder != null) {
                        try {
                            preferenceBuilder.applyPreference(preferenceBuilder.toValue(valueChooser.getText().toString()),
                                                     preferenceChangeListener);
                        } catch (NumberFormatException n) {
                            Toast.makeText(v.getContext(), n.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        booleanSwitch = (Switch) findViewById(R.id.view_debug_module_preference_booleanSwitch);
    }

    void populate(PreferenceBuilder preference, PreferenceChangeListener changeListener) {
        preferenceBuilder = preference;
        preferenceChangeListener = changeListener;
        title.setText(preference.getTitle());

        if(preferenceBuilder.getPrefType().equals(Boolean.class)) {
            valueChooser.setVisibility(GONE);
            booleanSwitch.setVisibility(VISIBLE);

            // safe switch
            booleanSwitch.setOnCheckedChangeListener(null);
            booleanSwitch.setChecked((Boolean) preference.getPreference());
            booleanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(preferenceBuilder != null && preferenceBuilder.getPrefType().equals(Boolean.class)) {
                        try {
                            preferenceBuilder.applyPreference(isChecked, preferenceChangeListener);
                        } catch (NumberFormatException n) {
                            Toast.makeText(buttonView.getContext(), n.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else {
            valueChooser.setText(String.valueOf(preference.getPreference()));
            booleanSwitch.setVisibility(GONE);
            valueChooser.setVisibility(VISIBLE);
            Class type = preference.getPrefType();
            if (type.equals(Boolean.class)) {
                valueChooser.setInputType(InputType.TYPE_CLASS_TEXT);
            } else if (type.equals(Integer.class)) {
                valueChooser.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            } else if (type.equals(Float.class)) {
                valueChooser.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            } else if (type.equals(Long.class)) {
                valueChooser.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
            } else if (type.equals(String.class)) {
                valueChooser.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        }
    }
}
