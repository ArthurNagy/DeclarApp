package com.arthurnagy.staysafe.feature.shared

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Shader
import android.graphics.drawable.Drawable
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.RecyclerView
import com.arthurnagy.staysafe.R
import kotlin.math.abs

typealias OnRemoveItem<T> = (item: T) -> Unit
typealias CanRemoveItem<T> = (item: T) -> Boolean

fun <T, VB : ViewDataBinding, A : DataBoundListAdapter<T, VB>> setupSwipeToDelete(
    recyclerView: RecyclerView,
    adapter: A,
    onRemoveItem: OnRemoveItem<T>,
    canRemoveItem: CanRemoveItem<T> = { true }
) {
    ItemTouchHelper(SwipeToDeleteCallback(recyclerView.context, adapter, onRemoveItem, canRemoveItem)).attachToRecyclerView(recyclerView)
}

@Suppress("MagicNumber", "ComplexMethod", "LongMethod", "LargeClass")
private class SwipeToDeleteCallback<T, VB : ViewDataBinding, out A : DataBoundListAdapter<T, VB>>(
    context: Context,
    private val adapter: A,
    private val removeFromPosition: OnRemoveItem<T>,
    private val canDeleteItemOnPosition: CanRemoveItem<T>
) : ItemTouchHelper.SimpleCallback(0, START or END) {

    private val backgroundColor: Int = context.color(R.color.transparent)
    private val shadowColor: Int = context.color(R.color.shadow)
    private val deleteColor: Int = context.color(R.color.delete_red)
    private val iconPadding: Int
    private val topShadowHeight: Float
    private val bottomShadowHeight: Float
    private val sideShadowWidth: Float

    // lazily initialized later
    private var initialized = false
    private var iconColorFilter: Int
    private var deleteIcon: Drawable? = null
    private var circlePaint: Paint? = null
    private var leftShadowPaint: Paint? = null
    private var rightShadowPaint: Paint? = null
    private var topShadowPaint: Paint? = null
    private var bottomShadowPaint: Paint? = null

    init {
        iconColorFilter = deleteColor
        iconPadding = context.dimensionPixel(R.dimen.first_keyline)
        // faking elevation light-source; so use different shadow sizes
        topShadowHeight = context.dimension(R.dimen.small_margin)
        bottomShadowHeight = topShadowHeight / 2f
        sideShadowWidth = topShadowHeight * 3f / 4f
    }

    // don't support re-ordering
    override fun onMove(rv: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        removeFromPosition(adapter.currentList[viewHolder.adapterPosition])
    }

    override fun getSwipeDirs(rv: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        // can only swipe-dismiss certain sources
        val swipeDir = if (canDeleteItemOnPosition(adapter.currentList[viewHolder.adapterPosition])) START or END else 0
        return makeMovementFlags(0, swipeDir)
    }

    // make deleting a deliberate gesture
    override fun getSwipeEscapeVelocity(defaultValue: Float) = defaultValue * 5f

    override fun isLongPressDragEnabled() = false

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        // bail fast if there isn't a swipe
        if (dX == 0f) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        val direction: Int = if (dX > 0) END else START

        val left = viewHolder.itemView.left.toFloat()
        val top = viewHolder.itemView.top.toFloat()
        val right = viewHolder.itemView.right.toFloat()
        val bottom = viewHolder.itemView.bottom.toFloat()
        val width = right - left
        val height = bottom - top
        val saveCount = c.save()
        val moveDelta = abs(dX)

        // clip to the 'revealed' area
        when (direction) {
            START -> c.clipRect(right - moveDelta, top, right, bottom)
            END -> c.clipRect(left, top, moveDelta, bottom)
        }
        c.drawColor(backgroundColor)

        // lazy initialize some vars
        initialize(recyclerView.context)

        // variables dependent upon gesture progress
        val progress = moveDelta / width
        val swipeThreshold = getSwipeThreshold(viewHolder) - 0.15F
        val thirdThreshold = swipeThreshold / 3f
        val iconPopThreshold = swipeThreshold + 0.125f
        val iconPopFinishedThreshold = iconPopThreshold + 0.125f
        var opacity = 1f
        var iconScale = 1f
        var circleRadius = 0f
        var iconColor = deleteColor
        when (progress) {
            in 0f..thirdThreshold -> {
                // fade in
                opacity = progress / thirdThreshold
            }
            in thirdThreshold..swipeThreshold -> {
                // scale icon down to 0.9
                iconScale = 1f - (((progress - thirdThreshold) / (swipeThreshold - thirdThreshold)) * 0.1f)
            }
            else -> {
                // draw circle and switch icon color
                circleRadius = (progress - swipeThreshold) * width * CIRCLE_ACCELERATION
                iconColor = Color.WHITE
                // scale icon up to 1.2 then back down to 1
                iconScale = when (progress) {
                    in swipeThreshold..iconPopThreshold -> 0.9f + ((progress - swipeThreshold) / (iconPopThreshold - swipeThreshold)) * 0.3f
                    in iconPopThreshold..iconPopFinishedThreshold -> {
                        1.2f - (((progress - iconPopThreshold) / (iconPopFinishedThreshold - iconPopThreshold)) * 0.2f)
                    }
                    else -> 1f
                }
            }
        }

        deleteIcon?.let {
            val cy = top + height / 2f
            val halfIconSize = it.intrinsicWidth * iconScale / 2f

            val cx = when (direction) {
                START -> right - iconPadding - it.intrinsicWidth / 2f
                else -> left + iconPadding + it.intrinsicWidth / 2f
            }

            it.setBounds(
                (cx - halfIconSize).toInt(), (cy - halfIconSize).toInt(),
                (cx + halfIconSize).toInt(), (cy + halfIconSize).toInt()
            )
            it.alpha = (opacity * 255f).toInt()
            if (iconColor != iconColorFilter) {
                it.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
                iconColorFilter = iconColor
            }
            if (circleRadius > 0f) {
                circlePaint?.let { paint ->
                    c.drawCircle(cx, cy, circleRadius, paint)
                }
            }
            it.draw(c)
        }

        // draw shadows to fake elevation of surrounding views
        topShadowPaint?.let {
            it.shader?.setTranslation(y = top)
            c.drawRect(left, top, right, top + topShadowHeight, it)
        }
        bottomShadowPaint?.let {
            it.shader?.setTranslation(y = bottom - bottomShadowHeight)
            c.drawRect(left, bottom - bottomShadowHeight, right, bottom, it)
        }
        when (direction) {
            START -> leftShadowPaint?.let {
                val shadowLeft = right - moveDelta
                it.shader.setTranslation(x = shadowLeft)
                c.drawRect(shadowLeft, top, shadowLeft + sideShadowWidth, bottom, it)
            }
            END -> rightShadowPaint?.let {
                val shadowRight = moveDelta - sideShadowWidth
                it.shader.setTranslation(x = shadowRight)
                c.drawRect(shadowRight, top, shadowRight + sideShadowWidth, bottom, it)
            }
        }

        c.restoreToCount(saveCount)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun initialize(context: Context) {
        if (!initialized) {
            deleteIcon = context.drawable(R.drawable.ic_delete_24dp)
            topShadowPaint = Paint().apply {
                shader = LinearGradient(0f, 0f, 0f, topShadowHeight, shadowColor, 0, Shader.TileMode.CLAMP)
            }
            bottomShadowPaint = Paint().apply {
                shader = LinearGradient(0f, 0f, 0f, bottomShadowHeight, 0, shadowColor, Shader.TileMode.CLAMP)
            }
            leftShadowPaint = Paint().apply {
                shader = LinearGradient(0f, 0f, sideShadowWidth, 0f, shadowColor, 0, Shader.TileMode.CLAMP)
            }
            rightShadowPaint = Paint().apply {
                shader = LinearGradient(0f, 0f, sideShadowWidth, 0f, 0, shadowColor, Shader.TileMode.CLAMP)
            }
            circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = deleteColor
            }
            initialized = true
        }
    }

    companion object {
        // expand the circle rapidly once it shows, don't track swipe 1:1
        private const val CIRCLE_ACCELERATION = 3f
    }
}