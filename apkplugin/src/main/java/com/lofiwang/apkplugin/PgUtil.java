package com.lofiwang.apkplugin;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.io.File;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * Created by lofiwang
 */
public class PgUtil {
    private static final String TAG = "PgUtil";

    public static DexClassLoader createDexClassLoader(Context context, String apkPath) {
        // 4.1以后不能够将optimizedDirectory设置到sd卡目录， 否则抛出异常.
        File optimizedDirectoryFile = context.getDir("dex", Context.MODE_PRIVATE);
        return new DexClassLoader(apkPath, optimizedDirectoryFile.getAbsolutePath(), null, context.getClassLoader());
    }

    public static AssetManager createAssetManager(String apkPath) {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
            addAssetPath.invoke(assetManager, apkPath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Resources createResources(Context context, AssetManager assetManager) {
        Resources superRes = context.getResources();
        superRes.getDisplayMetrics();
        superRes.getConfiguration();
        return new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
    }

    public static Resources.Theme createTheme(Context context, Resources resources) {
        Resources.Theme theme = resources.newTheme();
        theme.setTo(context.getTheme());
        return theme;
    }
}
