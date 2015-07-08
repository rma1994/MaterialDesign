package br.com.nwk.materialdesign.util;

import android.content.Context;
import android.content.res.Resources;

/**
 * Created by rma19_000 on 08/07/2015.
 */
public class ToPixels {

    public int toPixels(Context context, float dip) {
        Resources r = context.getResources();
        float densid = r.getDisplayMetrics().density;
        int px = (int) (dip * densid + 0.5f);
        return px;
    }
}
