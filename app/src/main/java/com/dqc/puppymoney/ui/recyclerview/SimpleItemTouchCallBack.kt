import android.util.Log
import androidx.annotation.NonNull
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.dqc.puppymoney.interfaces.TouchCallBack


class SimpleItemTouchCallBack(mCallBack: TouchCallBack) : ItemTouchHelper.Callback() {
    private val mCallBack: TouchCallBack

    //左滑删除的
    private var mSwipeEnable = true

    /**
     * 返回可以滑动的方向,一般使用makeMovementFlags(int,int)
     * 或makeFlag(int, int)来构造我们的返回值
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    override fun getMovementFlags(@NonNull recyclerView: RecyclerView, @NonNull viewHolder: RecyclerView.ViewHolder): Int {

        //允许上下拖拽
        val drag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        Log.d("SimpleItemTouchCallBack", " drag $drag")

        //允许向左滑动
        val swipe = ItemTouchHelper.LEFT
        //设置
        return makeMovementFlags(drag, swipe)
    }

    /**
     * 上下拖动item时回调,可以调用Adapter的notifyItemMoved方法来交换两个ViewHolder的位置，
     * 最后返回true，
     * 表示被拖动的ViewHolder已经移动到了目的位置
     *
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     */
    override fun onMove(@NonNull recyclerView: RecyclerView, @NonNull viewHolder: RecyclerView.ViewHolder, @NonNull target: RecyclerView.ViewHolder): Boolean {
        //通知适配器,两个子条目位置发生改变
        mCallBack.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        Log.d("ItemMove", "  ${viewHolder.adapterPosition} ${target.adapterPosition}")
        return true
    }

    /**
     * 当用户左右滑动item时达到删除条件就会调用,一般为一半,条目继续滑动删除,否则弹回
     *
     * @param viewHolder
     * @param direction
     */
    override fun onSwiped(@NonNull viewHolder: RecyclerView.ViewHolder, direction: Int) {
        mCallBack.onItemDelete(viewHolder.adapterPosition)
    }

    /**
     * 支持长按拖动,默认是true
     *
     * @return
     */
    override fun isLongPressDragEnabled(): Boolean {
        return super.isLongPressDragEnabled()
    }

    /**
     * 支持滑动,即可以调用到onSwiped()方法,默认是true
     *
     * @return
     */
    override fun isItemViewSwipeEnabled(): Boolean {
        return mSwipeEnable
    }

    override fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//        Log.d("ItemMove", "  canDropOver")
        return super.canDropOver(recyclerView, current, target)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        Log.d("ItemMove", "  clearView")
        mCallBack.onClearView()
    }

    /**
     * 设置是否支持左滑删除
     *
     * @param enable
     */
    fun setmSwipeEnable(enable: Boolean) {
        mSwipeEnable = enable
    }

    init {
        this.mCallBack = mCallBack
    }
}