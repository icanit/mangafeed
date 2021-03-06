package eu.kanade.tachiyomi.ui.download

import android.os.Bundle
import android.view.*
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.download.DownloadService
import eu.kanade.tachiyomi.data.download.model.Download
import eu.kanade.tachiyomi.ui.base.fragment.BaseRxFragment
import eu.kanade.tachiyomi.ui.main.MainActivity
import eu.kanade.tachiyomi.widget.NpaLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_download_queue.*
import nucleus.factory.RequiresPresenter
import rx.Subscription

/**
 * Fragment that shows the currently active downloads.
 * Uses R.layout.fragment_download_queue.
 */
@RequiresPresenter(DownloadPresenter::class)
class DownloadFragment : BaseRxFragment<DownloadPresenter>() {

    /**
     * Adapter containing the active downloads.
     */
    private lateinit var adapter: DownloadAdapter

    /**
     * Menu item to start the queue.
     */
    private var startButton: MenuItem? = null

    /**
     * Menu item to pause the queue.
     */
    private var pauseButton: MenuItem? = null

    /**
     * Menu item to clear the queue.
     */
    private var clearButton: MenuItem? = null

    /**
     * Subscription to know if the download queue is running.
     */
    private var queueStatusSubscription: Subscription? = null

    /**
     * Whether the download queue is running or not.
     */
    private var isRunning: Boolean = false

    companion object {
        /**
         * Creates a new instance of this fragment.
         *
         * @return a new instance of [DownloadFragment].
         */
        @JvmStatic
        fun newInstance(): DownloadFragment {
            return DownloadFragment()
        }
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_download_queue, container, false)
    }

    override fun onViewCreated(view: View, savedState: Bundle?) {
        setToolbarTitle(R.string.label_download_queue)

        // Check if download queue is empty and update information accordingly.
        setInformationView()

        // Initialize adapter.
        adapter = DownloadAdapter(activity)
        recycler.adapter = adapter

        // Set the layout manager for the recycler and fixed size.
        recycler.layoutManager = NpaLinearLayoutManager(activity)
        recycler.setHasFixedSize(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.download_queue, menu)

        // Set start button visibility.
        startButton = menu.findItem(R.id.start_queue).apply {
            isVisible = !isRunning && !presenter.downloadQueue.isEmpty()
        }

        // Set pause button visibility.
        pauseButton = menu.findItem(R.id.pause_queue).apply {
            isVisible = isRunning
        }

        // Set clear button visibility.
        clearButton = menu.findItem(R.id.clear_queue).apply {
            if (adapter.itemCount > 0) {
                isVisible = true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.start_queue -> DownloadService.start(activity)
            R.id.pause_queue -> DownloadService.stop(activity)
            R.id.clear_queue -> {
                DownloadService.stop(activity)
                presenter.clearQueue()
                clearButton?.isVisible = false
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        queueStatusSubscription = presenter.downloadManager.runningSubject
                .subscribe { onQueueStatusChange(it) }
    }

    override fun onPause() {
        queueStatusSubscription?.unsubscribe()
        super.onPause()
    }

    /**
     * Called when the queue's status has changed. Updates the visibility of the buttons.
     *
     * @param running whether the queue is now running or not.
     */
    private fun onQueueStatusChange(running: Boolean) {
        isRunning = running
        startButton?.isVisible = !running && !presenter.downloadQueue.isEmpty()
        pauseButton?.isVisible = running
        clearButton?.isVisible = running

        // Check if download queue is empty and update information accordingly.
        setInformationView()
    }

    /**
     * Called from the presenter to assign the downloads for the adapter.
     *
     * @param downloads the downloads from the queue.
     */
    fun onNextDownloads(downloads: List<Download>) {
        adapter.setItems(downloads)
    }

    /**
     * Called from the presenter when the status of a download changes.
     *
     * @param download the download whose status has changed.
     */
    fun onUpdateProgress(download: Download) {
        getHolder(download)?.notifyProgress()
    }

    /**
     * Called from the presenter when a page of a download is downloaded.
     *
     * @param download the download whose page has been downloaded.
     */
    fun onUpdateDownloadedPages(download: Download) {
        getHolder(download)?.notifyDownloadedPages()
    }

    /**
     * Returns the holder for the given download.
     *
     * @param download the download to find.
     * @return the holder of the download or null if it's not bound.
     */
    private fun getHolder(download: Download): DownloadHolder? {
        return recycler.findViewHolderForItemId(download.chapter.id) as? DownloadHolder
    }

    /**
     * Set information view when queue is empty
     */
    private fun setInformationView() {
        (activity as MainActivity).updateEmptyView(presenter.downloadQueue.isEmpty(),
                R.string.information_no_downloads, R.drawable.ic_file_download_black_128dp)
    }

}
