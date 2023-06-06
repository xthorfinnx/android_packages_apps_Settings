/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings;


import android.content.Context;
import android.content.res.TypedArray;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.VisibleForTesting;

public class DefaultRingtonePreference extends RingtonePreference {
    private static final String TAG = "DefaultRingtonePreference";

    private Position position;

    public DefaultRingtonePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    public void onPrepareRingtonePickerIntent(Intent ringtonePickerIntent) {
        super.onPrepareRingtonePickerIntent(ringtonePickerIntent);

        /*
         * Since this preference is for choosing the default ringtone, it
         * doesn't make sense to show a 'Default' item.
         */
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
    }

    @Override
    protected void onSaveRingtone(Uri ringtoneUri) {
        if (ringtoneUri == null) {
            setActualDefaultRingtoneUri(ringtoneUri);
            return;
        }

        String mimeType = mUserContext.getContentResolver().getType(ringtoneUri);
        if (mimeType == null) {
            Log.e(TAG, "onSaveRingtone for URI:" + ringtoneUri
                    + " ignored: failure to find mimeType (no access from this context?)");
            return;
        }

        if (!(mimeType.startsWith("audio/") || mimeType.equals("application/ogg"))) {
            Log.e(TAG, "onSaveRingtone for URI:" + ringtoneUri
                    + " ignored: associated mimeType:" + mimeType + " is not an audio type");
            return;
        }

        setActualDefaultRingtoneUri(ringtoneUri);
    }

    @VisibleForTesting
    void setActualDefaultRingtoneUri(Uri ringtoneUri) {
        RingtoneManager.setActualDefaultRingtoneUri(mUserContext, getRingtoneType(), ringtoneUri);
    }

    @Override
    protected Uri onRestoreRingtone() {
        return RingtoneManager.getActualDefaultRingtoneUri(mUserContext, getRingtoneType());
    }

    private void init(Context context, AttributeSet attrs) {
        // Retrieve and set the layout resource based on position
        // otherwise do not set any layout
        position = getPosition(context, attrs);
        if (position != null) {
            int layoutResId = getLayoutResourceId(position);
            setLayoutResource(layoutResId);
        }
    }

    private Position getPosition(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AdaptivePreference);
        String positionAttribute = typedArray.getString(R.styleable.AdaptivePreference_position);
        typedArray.recycle();

        Position positionFromAttribute = Position.fromAttribute(positionAttribute);
        if (positionFromAttribute != null) {
            return positionFromAttribute;
        }

        return null;
    }

    private int getLayoutResourceId(Position position) {
        switch (position) {
            case TOP:
                return R.layout.arc_card_about_top;
            case BOTTOM:
                return R.layout.arc_card_about_bottom;
            case MIDDLE:
                return R.layout.arc_card_about_middle;
            default:
                return R.layout.arc_card_about_middle;
        }
    }

    private enum Position {
        TOP,
        MIDDLE,
        BOTTOM;

        public static Position fromAttribute(String attribute) {
            if (attribute != null) {
                switch (attribute.toLowerCase()) {
                    case "top":
                        return TOP;
                    case "bottom":
                        return BOTTOM;
                    case "middle":
                        return MIDDLE;
                        
                }
            }
            return null;
        }
    }
}
