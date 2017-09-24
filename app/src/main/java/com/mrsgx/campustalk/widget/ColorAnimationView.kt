package com.mrsgx.campustalk.widget

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View

import kotlin.jvm.JvmOverloads;
import android.view.animation.LinearInterpolator



class ColorAnimationView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr), ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    internal var colorAnim: ValueAnimator? = null

    private val mPageChangeListener: PageChangeListener

    internal var onPageChangeListener: ViewPager.OnPageChangeListener? = null

    fun setOnPageChangeListener(onPageChangeListener: ViewPager.OnPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener
    }


    /**
     * 这是你唯一需要关心的方法
     * @param mViewPager  你必须在设置 Viewpager 的 Adapter 这后，才能调用这个方法。
     * @param obj ,这个obj实现了 ColorAnimationView.OnPageChangeListener ，实现回调
     * @param count   ,viewpager孩子的数量
     * @param colors int... colors ，你需要设置的颜色变化值~~ 如何你传人 空，那么触发默认设置的颜色动画
     */
    /**
     * This is the only method you need care about.
     * @param mViewPager  ,you need set the adpater before you call this.
     * @param count   ,this param set the count of the viewpaper's child
     * @param colors ,this param set the change color use (int... colors),
     * so,you could set any length if you want.And by default.
     * if you set nothing , don't worry i have already creat
     * a default good change color!
     */
    fun setmViewPager(mViewPager: ViewPager, count: Int, vararg colors: Int) {
        //		this.mViewPager = mViewPager;
        if (mViewPager.adapter == null) {
            throw IllegalStateException(
                    "ViewPager does not have adapter instance.")
        }
        mPageChangeListener.viewPagerChildCount = count

        mViewPager.setOnPageChangeListener(mPageChangeListener)
        if (colors.size == 0) {
            createDefaultAnimation()
        } else {
            createAnimation(*colors)
        }

    }

    init {
        mPageChangeListener = PageChangeListener()
    }

    private fun seek(seekTime: Long) {
        if (colorAnim == null) {
            createDefaultAnimation()
        }
        colorAnim!!.currentPlayTime = seekTime
    }

    private fun createAnimation(vararg colors: Int) {
        if (colorAnim == null) {
            colorAnim = ObjectAnimator.ofInt(this,
                    "backgroundColor", *colors)
            colorAnim!!.setEvaluator(ArgbEvaluator())
            colorAnim!!.interpolator=LinearInterpolator()
            colorAnim!!.duration = DURATION.toLong()
            colorAnim!!.addUpdateListener(this)
        }
    }

    private fun createDefaultAnimation() {
        colorAnim = ObjectAnimator.ofInt(this,
                "backgroundColor", WHITE, RED, BLUE, GREEN, WHITE)
        colorAnim!!.setEvaluator(ArgbEvaluator())
        colorAnim!!.interpolator=LinearInterpolator()
        colorAnim!!.duration = DURATION.toLong()
        colorAnim!!.addUpdateListener(this)
    }

    override fun onAnimationStart(animation: Animator) {

    }

    override fun onAnimationEnd(animation: Animator) {}

    override fun onAnimationCancel(animation: Animator) {

    }

    override fun onAnimationRepeat(animation: Animator) {

    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        invalidate()
        //		long playtime = colorAnim.getCurrentPlayTime();
    }

    private inner

    class PageChangeListener : ViewPager.OnPageChangeListener {

        var viewPagerChildCount: Int = 0

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            val count = viewPagerChildCount - 1
            if (count != 0) {
                val length = (position + positionOffset) / count
                val progress = (length * DURATION).toInt()
                this@ColorAnimationView.seek(progress.toLong())
            }
            // call the method by default
            if (onPageChangeListener != null) {
                onPageChangeListener!!.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

        }

        override fun onPageSelected(position: Int) {
            if (onPageChangeListener != null) {
                onPageChangeListener!!.onPageSelected(position)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            if (onPageChangeListener != null) {
                onPageChangeListener!!.onPageScrollStateChanged(state)
            }
        }
    }

    companion object {
        private val RED = 0xffFF8080.toInt()
        private val BLUE = 0xff8080FF.toInt()
        private val WHITE = 0xffffffff.toInt()
        private val GREEN = 0xff80ff80.toInt()
        private val DURATION = 3000
    }
}