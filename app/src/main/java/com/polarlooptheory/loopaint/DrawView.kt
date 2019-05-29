package com.polarlooptheory.loopaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mDrawings = arrayListOf(Drawing())

    private var mCurX = 0f
    private var mCurY = 0f
    private var mStartX = 0f
    private var mStartY = 0f

    init {
        mDrawings.last().mPaint.apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 32f
            isAntiAlias = true
        }
    }

    fun getSize():Float{return mDrawings.last().mPaint.strokeWidth}
    fun getColor():Int{return mDrawings.last().mPaint.color}

    fun changeColor(color: Int){
        val tmp = Drawing()
        tmp.mPaint = Paint(mDrawings.last().mPaint)
        tmp.mPaint.color = color
        mDrawings.add(tmp)
    }

    fun changeSize(size: Float){
        val tmp = Drawing()
        tmp.mPaint = Paint(mDrawings.last().mPaint)
        tmp.mPaint.strokeWidth = size
        mDrawings.add(tmp)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mDrawings.forEach {
        canvas.drawPath(it.mPath, it.mPaint)
        }
    }

    private fun actionDown(x: Float, y: Float) {
        mDrawings.last().mPath.moveTo(x, y)
        mCurX = x
        mCurY = y
    }

    private fun actionMove(x: Float, y: Float) {
        mDrawings.last().mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2)
        mCurX = x
        mCurY = y
    }

    private fun actionUp() {
        mDrawings.last().mPath.lineTo(mCurX, mCurY)

        // draw a dot on click
        if (mStartX == mCurX && mStartY == mCurY) {
            mDrawings.last().mPath.lineTo(mCurX, mCurY + 2)
            mDrawings.last().mPath.lineTo(mCurX + 1, mCurY + 2)
            mDrawings.last().mPath.lineTo(mCurX + 1, mCurY)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = x
                mStartY = y
                actionDown(x, y)
            }
            MotionEvent.ACTION_MOVE -> actionMove(x, y)
            MotionEvent.ACTION_UP -> actionUp()
        }
        invalidate()
        return true
    }

    fun clearCanvas(clr: Int, size: Float) {
        mDrawings = arrayListOf(Drawing())
        mDrawings.last().mPaint.apply {
            color = clr
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = size
            isAntiAlias = true
        }
        invalidate()
    }

    fun bitmap(): Bitmap {
        val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(b)
        c.drawColor(Color.WHITE)
        mDrawings.forEach {
            c.drawPath(it.mPath, it.mPaint)
        }
        return b
    }
}