package com.tacitus.dnp;


import android.os.Environment;

import java.io.File;

public class Path {

    public static String getPublicRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getPublicAppPath() {
        return getPublicRootPath() + File.separatorChar + "DNP";
    }

    public static boolean createPublicAppPath() {
        File dir = new File(getPublicAppPath());
        return dir.mkdir();
    }

}
