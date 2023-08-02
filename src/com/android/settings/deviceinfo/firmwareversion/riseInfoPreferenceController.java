/*
 * Copyright (C) 2020 Wave-OS
 * Copyright (C) 2023 the RisingOS Android Project
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

package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.widget.TextView;
import android.text.TextUtils;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;

public class riseInfoPreferenceController extends AbstractPreferenceController {

    private static final String KEY_RISING_INFO = "rising_info";
    private static final String KEY_RISING_DEVICE = "rising_device";
    private static final String KEY_RISING_VERSION = "rising_version";
    private static final String KEY_BUILD_STATUS = "rom_build_status";
    private static final String KEY_BUILD_VERSION = "rising_build_version";
    
    private static final String PROP_RISING_VERSION = "ro.rising.version";
    private static final String PROP_RISING_VERSION_CODE = "ro.rising.code";
    private static final String PROP_RISING_RELEASETYPE = "ro.rising.releasetype";
    private static final String PROP_RISING_MAINTAINER = "ro.rising.maintainer";
    private static final String PROP_RISING_DEVICE = "ro.rising.device";
    private static final String PROP_RISING_BUILD_TYPE = "ro.rising.packagetype";
    private static final String PROP_RISING_BUILD_VERSION = "ro.rising.build.version";
    
    public riseInfoPreferenceController(Context context) {
        super(context);
    }

    private String getPropertyOrDefault(String propName) {
        return SystemProperties.get(propName, this.mContext.getString(R.string.device_info_default));
    }

    private String getDeviceName() {
        String device = getPropertyOrDefault(PROP_RISING_DEVICE);
        return device.isEmpty() ? Build.MANUFACTURER + " " + Build.MODEL : device;
    }

    private String getRisingBuildVersion() {
        return getPropertyOrDefault(PROP_RISING_BUILD_VERSION);
    }
    
    private String getRisingVersion() {
        return getPropertyOrDefault(PROP_RISING_VERSION) + " | " +
               getPropertyOrDefault(PROP_RISING_VERSION_CODE) + " | " +
               getPropertyOrDefault(PROP_RISING_BUILD_TYPE);
    }

    private String getRisingReleaseType() {
        final String releaseType = getPropertyOrDefault(PROP_RISING_RELEASETYPE);
        return releaseType.substring(0, 1).toUpperCase() +
               releaseType.substring(1).toLowerCase();
    }
    
    private String getRisingbuildStatus() {
        final String buildType = getPropertyOrDefault(PROP_RISING_RELEASETYPE).toLowerCase();
        return buildType.equals("official") ?
            this.mContext.getString(R.string.build_is_official_title) :
            this.mContext.getString(R.string.build_is_community_title);
    }

    private String getRisingMaintainer() {
        final String RisingMaintainer = getPropertyOrDefault(PROP_RISING_MAINTAINER);
        final String buildType = getPropertyOrDefault(PROP_RISING_RELEASETYPE).toLowerCase();
        
        if (RisingMaintainer.equalsIgnoreCase("Unknown")) {
            return buildType.equals("official") ? 
                this.mContext.getString(R.string.build_is_official_summary_oopsie) :
                this.mContext.getString(R.string.build_is_community_summary_oopsie);
        }

        return buildType.equals("official") ?
            this.mContext.getString(R.string.build_is_official_summary, RisingMaintainer) :
            this.mContext.getString(R.string.build_is_community_summary, RisingMaintainer);
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);

        final String RisingMaintainer = getRisingMaintainer();
        final String isOfficial = getPropertyOrDefault(PROP_RISING_RELEASETYPE).toLowerCase();

        screen.findPreference(KEY_BUILD_STATUS).setTitle(getRisingbuildStatus());
        screen.findPreference(KEY_BUILD_STATUS).setSummary(RisingMaintainer);
        screen.findPreference(KEY_BUILD_VERSION).setSummary(getRisingBuildVersion());
        screen.findPreference(KEY_RISING_VERSION).setSummary(getRisingVersion());
        screen.findPreference(KEY_RISING_DEVICE).setSummary(getDeviceName());
        screen.findPreference(KEY_BUILD_STATUS).setIcon(isOfficial.equals("official") ? R.drawable.verified : R.drawable.unverified);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_RISING_INFO;
    }
}
