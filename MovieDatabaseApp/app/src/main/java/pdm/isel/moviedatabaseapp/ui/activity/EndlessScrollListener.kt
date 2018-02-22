package pdm.isel.moviedatabaseapp.ui.activity

import android.widget.AbsListView

abstract class EndlessScrollListener : AbsListView.OnScrollListener {
    private var visibleThreshold = 20
    private var currentPage = 0
    private var previousTotalItemCount = 0
    private var loading = true
    private var  startingPageIndex : Int = 0

    constructor() {}

    constructor(visibleThreshold: Int) {
        this.visibleThreshold = visibleThreshold
    }

    constructor(visibleThreshold: Int, startPage: Int) {
        this.visibleThreshold = visibleThreshold
        this.startingPageIndex = startPage
        this.currentPage = startPage
    }


    override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }

        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
            currentPage++
        }

        if (!loading && firstVisibleItem + visibleItemCount + visibleThreshold >= totalItemCount) {
            loading = onLoadMore(currentPage, totalItemCount)
        }
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int): Boolean

    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
        // Don't take any action on changed
    }
}
