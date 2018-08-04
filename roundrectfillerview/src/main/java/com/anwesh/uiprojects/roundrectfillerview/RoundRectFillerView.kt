package com.anwesh.uiprojects.roundrectfillerview

/**
 * Created by anweshmishra on 04/08/18.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Paint
import android.content.Context
import android.graphics.Color
import android.graphics.RectF

val nodes : Int = 5
val speed : Float = 0.05f

fun Canvas.drawRRFNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = w / nodes
    val size : Float = gap/2
    val sc1 : Float = Math.min(0.5f, scale) * 2
    val sc2 : Float = Math.min(0.5f, Math.max(0f, scale - 0.5f)) * 2
    paint.color = Color.parseColor("#2E7D32")
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    save()
    translate(gap * i + gap / 2 + gap * sc2, h / 2)
    paint.style = Paint.Style.STROKE
    drawRoundRect(RectF(-size/2, -size/2, size/2, size/2), size/5, size/5, paint)
    paint.style = Paint.Style.FILL
    val fillSize : Float = size * (1 - sc1)
    drawRoundRect(RectF(-fillSize/2, -fillSize/2, fillSize/2, fillSize/2), fillSize/5, fillSize/5, paint)
    restore()
}


class RoundRectFillerView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb  : (Float) -> Unit) {
            scale += speed * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.invalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class RRFNode(var i : Int, val state : State = State()) {

        private var next : RRFNode? = null

        private var prev : RRFNode? = null

        fun update(stopcb : (Int, Float) -> Unit) {
            state.update {
                stopcb(i, it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : RRFNode {
            var curr : RRFNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = RRFNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawRRFNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }
    }

    data class LinkedRRF(var i : Int) {

        private var curr : RRFNode = RRFNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : RoundRectFillerView) {

        private val lrrf : LinkedRRF = LinkedRRF(0)

        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#BDBDBD"))
            lrrf.draw(canvas, paint)
            animator.animate {
                lrrf.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            lrrf.startUpdating {
                animator.start()
            }
        }
    }
}