// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst 

package com.songtzu.cartoon.u.anim;

import android.content.res.Resources;

public class AnimUtils
{

    public AnimUtils()
    {
    }

    public static float calculateValue(float af[], float f, float f1)
    {
        float f2 = f1;
        if(af != null && af.length > 0)
        {
            float f3 = 1.0F / (float)(-1 + af.length);
            int i = (int)(f / f3);
            if(i >= -1 + af.length)
            {
                f2 = af[-1 + af.length];
            } else
            {
                float f4 = f - f3 * (float)i;
                f2 = af[i] + (f4 * (af[i + 1] - af[i])) / f3;
            }
        }
        return f2;
    }

    public static float[] getFloatArray(Resources resources, int i)
    {
        String as[] = resources.getStringArray(i);
        float af[];
        if(as != null && as.length > 0)
        {
            af = new float[as.length];
            for(int j = 0; j < as.length; j++)
                af[j] = Float.parseFloat(as[j]);

        } else
        {
            af = null;
        }
        return af;
    }
}
