package uz.instat.mygridrecylersample.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.woxthebox.draglistview.BoardView
import com.woxthebox.draglistview.ColumnProperties
import kotlinx.android.synthetic.main.board_layout.*
import kotlinx.android.synthetic.main.column_header.view.*
import uz.instat.mygridrecylersample.R
import uz.instat.mygridrecylersample.adapter.BoardAdapter
import uz.instat.mygridrecylersample.data.entity.IconModel
import uz.instat.mygridrecylersample.util.NetworkStatus

class BoardFragment : Fragment(),
    BoardView.BoardListener {
    private lateinit var boardAdapter: BoardAdapter
    private lateinit var wobbleAnim: Animation
    private val viewModel: BoardViewModel by viewModels()
    private val columnCount = 5


    private var iconList = arrayListOf<IconModel>()
    private val adapterList = arrayListOf<BoardAdapter>()

    private val observerIconsStatus = Observer<NetworkStatus> {
        when (it) {
            is NetworkStatus.LOADING -> {
                progressbar.isVisible = true
            }
            is NetworkStatus.ERROR -> {
            }
            else -> {
                progressbar.isVisible = false
            }
        }
    }
    private val observerUpdateIconStatus = Observer<NetworkStatus> {
        when (it) {
            is NetworkStatus.LOADING -> {
                progressbar.isVisible = true
            }
            is NetworkStatus.ERROR -> {
            }
            else -> {
                progressbar.isVisible = false
            }
        }
    }
    private val observerIcons = Observer<List<IconModel>> {
        iconList.addAll(it)
        resetBoard()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadIcons()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.board_layout, container, false)
        val v = view.findViewById<BoardView>(R.id.board_view)
        v.setSnapToColumnsWhenScrolling(true)

        v.run {
            setSnapToColumnsWhenScrolling(true)
            setSnapToColumnWhenDragging(true)
            setSnapDragItemToTouch(true)
            setSnapToColumnInLandscape(false)
            setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER)
        }
        v.setBoardListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.liveIconsStatus.observe(viewLifecycleOwner, observerIconsStatus)
        viewModel.liveUpdateIconStatus.observe(viewLifecycleOwner, observerUpdateIconStatus)
        viewModel.liveIcons.observe(viewLifecycleOwner, observerIcons)
    }

    private fun resetBoard() {
        board_view.clearBoard()
        board_view.setCustomDragItem(null)
        board_view.setCustomColumnDragItem(null)

        wobbleAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.wobble)
        (0 until columnCount).forEach { it ->
            val iconsByColumn = arrayListOf<IconModel>()
            iconList.forEach { iconModel ->
                if (iconModel.columnId == it.toLong()) {
                    iconsByColumn.add(iconModel)
                }
            }
            iconsByColumn.sortBy { it.rowId }
            addColumn(iconsByColumn)
        }
    }

    private fun addColumn(iconList: ArrayList<IconModel>) {
        boardAdapter = BoardAdapter(iconList, R.id.item_layout, true, wobbleAnim)
        adapterList.add(boardAdapter)

        val footer = View.inflate(activity, R.layout.column_header, null)
        footer.item_count.text = "${iconList[0].columnId}"

        val layoutManager = GridLayoutManager(context, 4)
        val columnProperties = ColumnProperties.Builder.newBuilder(boardAdapter)
            .setLayoutManager(layoutManager)
            .setHasFixedItemSize(false)
            .setColumnBackgroundColor(Color.TRANSPARENT)
            .setItemsSectionBackgroundColor(Color.TRANSPARENT)
            .setFooter(footer)
            .build()

        board_view.addColumn(columnProperties)
    }

    override fun onItemDragStarted(column: Int, row: Int) {
//        adapterList[column].onAnimate(row)
    }

    override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {
        viewModel.updateIcon(
            fromColumn.toLong(),
            fromRow.toLong(),
            toColumn.toLong(),
            toRow.toLong()
        )
    }

    override fun onItemChangedPosition(oldColumn: Int, oldRow: Int, newColumn: Int, newRow: Int) {

    }

    override fun onItemChangedColumn(oldColumn: Int, newColumn: Int) {

    }

    override fun onFocusedColumnChanged(oldColumn: Int, newColumn: Int) {

    }

    override fun onColumnDragStarted(position: Int) {

    }

    override fun onColumnDragChangedPosition(oldPosition: Int, newPosition: Int) {

    }

    override fun onColumnDragEnded(position: Int) {

    }

}