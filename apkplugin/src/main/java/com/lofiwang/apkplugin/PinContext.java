package com.lofiwang.apkplugin;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;

import java.util.Locale;

/**
 * Created by lofiwang
 */
public class PinContext extends ContextWrapper {
    private int mThemeResource;
    private Resources.Theme mTheme;
    private LayoutInflater mInflater;
    private Resources mResources;
    private ClassLoader mClassLoader;
    private String mPluginApkPath;
    private String mPluginPkgName;

    public PinContext(Context base, String pluginApkPath) {
        super(base);
        mPluginApkPath = pluginApkPath;
        AssetManager assetManager = PinUtil.createAssetManager(pluginApkPath);
        mResources = PinUtil.createResources(base, assetManager);
        mTheme = PinUtil.createTheme(base, mResources);
        updateLocaleConfig(getBaseContext(), getBaseContext().getResources());
    }

    public void attachPluginPkgName(String pkgName) {
        mPluginPkgName = pkgName;
    }

    @Override
    public ClassLoader getClassLoader() {
        if (mClassLoader == null) {
            mClassLoader = PinUtil.createDexClassLoader(getBaseContext(), mPluginApkPath);;
        }
        return mClassLoader;
    }

    @Override
    public String getPackageResourcePath() {
        return mPluginApkPath;
    }

    @Override
    public String getPackageCodePath() {
        return mPluginApkPath;
    }

    @Override
    public String getPackageName() {
        return mPluginPkgName;
    }

    @Override
    public AssetManager getAssets() {
        return mResources.getAssets();
    }

    @Override
    public Resources getResources() {
        return mResources;
    }

    @Override
    public void setTheme(int resId) {
        if (mThemeResource != resId) {
            mThemeResource = resId;
            initializeTheme();
        }
    }

    @Override
    public Resources.Theme getTheme() {
        return mTheme == null ? super.getTheme() : mTheme;
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mInflater == null) {
                mInflater = new PinLayoutInflater(this);
            }
            return mInflater;
        }
        return getBaseContext().getSystemService(name);
    }

    protected void onApplyThemeResource(Resources.Theme theme, int resId, boolean first) {
        theme.applyStyle(resId, true);
    }

    private void initializeTheme() {
        final boolean first = mTheme == null;
        if (first) {
            mTheme = getResources().newTheme();
            final Resources.Theme theme = getBaseContext().getTheme();
            if (theme != null) {
                mTheme.setTo(theme);
            }
        }
        onApplyThemeResource(mTheme, mThemeResource, first);
    }

    private void updateLocaleConfig(Context context, Resources resources) {
        try {
            Locale locale = context.getResources().getConfiguration().locale;
            DisplayMetrics metrics = resources.getDisplayMetrics();
            Configuration configuration = resources.getConfiguration();
            configuration.setLocale(locale);
            mResources.updateConfiguration(configuration, metrics);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

