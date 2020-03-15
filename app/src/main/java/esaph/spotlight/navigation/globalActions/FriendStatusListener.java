/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.globalActions;

public interface FriendStatusListener
{
    void onStatusReceived(long CHAT_PARTNER_UID, short Status);
    void onStatusFailed(long CHAT_PARTNER_UID);
}
