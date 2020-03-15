/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.Esaph.EsaphCurvedTopNavigation;

import android.graphics.LinearGradient;

public interface EsaphColorTransitionAnimateAble
{
    public LinearGradient onCalculateGradient(int position, float positionOffset);
    public void onInvalidate();
}
