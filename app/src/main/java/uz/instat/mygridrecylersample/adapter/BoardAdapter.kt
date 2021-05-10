package uz.instat.mygridrecylersample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.annotation.IdRes
import androidx.appcompat.widget.LinearLayoutCompat
import com.woxthebox.draglistview.DragItemAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_grid.*
import uz.instat.mygridrecylersample.R
import uz.instat.mygridrecylersample.data.entity.IconModel

class BoardAdapter(
    private val list: ArrayList<IconModel>,
    @IdRes private val handleResId: Int,
    private val dragOnLongPress: Boolean,
    private val anim: Animation
) : DragItemAdapter<IconModel, BoardAdapter.BoardVH>() {

    init {
        itemList = list
    }

    class BoardVH(
        override val containerView: View,
        @IdRes handleResId: Int,
        dragOnLongPress: Boolean,
        val anim: Animation
    ) : DragItemAdapter.ViewHolder(containerView, handleResId, dragOnLongPress), LayoutContainer {
        private val handle = containerView.findViewById<LinearLayoutCompat>(handleResId)

        fun onBind(model: IconModel) {
            tv_name.text = model.name
            containerView.tag = model
        }

        fun onDragStarted() {
            handle.startAnimation(anim)
        }

        fun onDragStopped() {
            handle.clearAnimation()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardVH {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false)
        return BoardVH(view, handleResId, dragOnLongPress, anim)
    }

    override fun onBindViewHolder(holder: BoardVH, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.onBind(list[position])
    }

    override fun onBindViewHolder(holder: BoardVH, position: Int, payloads: MutableList<Any>) {
        when {
            payloads.isEmpty() -> super.onBindViewHolder(holder, position, payloads)
            payloads[0] is DragPayload.DragStartedAnim ->
                holder.onDragStarted()
            payloads[0] is DragPayload.DragEndedAnim ->
                holder.onDragStopped()
        }
    }

    fun onAnimate(rowPosition: Int) {
        when (rowPosition) {
            0 -> notifyItemRangeChanged(1, itemCount, DragPayload.DragStartedAnim)
            itemCount - 1 -> notifyItemRangeChanged(0, itemCount - 1, DragPayload.DragStartedAnim)
            else -> {
                notifyItemRangeChanged(0, rowPosition, DragPayload.DragStartedAnim)
                notifyItemRangeChanged(rowPosition + 1, itemCount, DragPayload.DragStartedAnim)
            }
        }
    }

    override fun getUniqueItemId(position: Int): Long {
        return list[position].id
    }

    sealed class DragPayload {
        object DragStartedAnim : DragPayload()
        object DragEndedAnim : DragPayload()
    }

}