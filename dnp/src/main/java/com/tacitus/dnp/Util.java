package com.tacitus.dnp;


import android.content.Context;
import android.widget.Toast;

import junit.framework.Assert;

public class Util {

    public static Toast showToast(Context context, Toast toast, int textResource) {
        Assert.assertNotNull(context);
        if(toast == null || !toast.getView().isShown()){
            toast = Toast.makeText(context,
                    textResource, Toast.LENGTH_SHORT);
            toast.show();
        }
        return toast;

    }
}
