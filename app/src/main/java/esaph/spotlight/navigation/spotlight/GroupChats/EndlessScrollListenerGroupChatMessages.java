/*
 *  Copyright (C) Esaph, Julian Auguscik - All Rights Reserved
 *  * Unauthorized copying of this file, via any medium is strictly prohibited
 *  * Proprietary and confidential
 *  * Written by Julian Auguscik <esaph.re@gmail.com>, March  2020
 *
 */

package esaph.spotlight.navigation.spotlight.GroupChats;

import android.widget.AbsListView;

public abstract class EndlessScrollListenerGroupChatMessages implements AbsListView.OnScrollListener
{
    // The minimum number of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 20;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    public EndlessScrollListenerGroupChatMessages()
    {
    }

    public abstract void loadMore();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        // Do Nothing
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        System.out.println("ESLGCM FIRST VISIBLE ITEM: " + firstVisibleItem);
        System.out.println("ESLGCM VISIBLE ITEM COUNT: " + visibleItemCount);
        System.out.println("ESLGCM TOTAL ITEM COUNT: " + totalItemCount);

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount)
        {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0)
            {
                this.loading = true;
            }
        }
        // If it's still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount))
        {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        // If it isn't currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (firstVisibleItem + visibleItemCount + visibleThreshold) >= totalItemCount ) {
            loading = true;
            loadMore();
        }
    }

}
