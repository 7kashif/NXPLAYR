package com.chand.progressbutton

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Display
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.button.MaterialButton


 class ProgressButton  @JvmOverloads constructor(
     context: Context,
     attrs: AttributeSet? = null,
     defStyle: Int = 0,
     defStyleRes: Int = 0
 ) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    private var mCircleView: CircleImageView? = null
    var mProgress: CircularProgressDrawable? = null
    var progressButton: MaterialButton? = null

    private var isStartAnim = false
    private var initialWidth: Int = 0
    private var finalWidth: Int = 0
     var progressText: String ?=null
        set(value) {
            progressButton?.text = value
           /* if(value!=null) {
                val textPaint = progressButton?.paint
                textWidth = textPaint?.measureText(value)?.toInt()!!
            }*/
        }
     var textWidth: Int = -1

    var progressBGColor: Int = Color.WHITE
        set(value) {
            mProgress?.backgroundColor = value
        }
    var progressColor: Int = Color.BLACK
        set(value) {
            mProgress?.setColorSchemeColors(value)
        }
    var bgColor: Int = -1
        set(value) {
            progressButton?.setBackgroundColor(value)
        }

    var isArrowVisible: Boolean = true
        set(value) {
            mProgress?.arrowEnabled = value
        }


    var textColor = -1
        set(value) {
            progressButton?.setTextColor(value)
        }
    var backgroundTint = -1
        set(value) {
            progressButton?.backgroundTintList = ColorStateList.valueOf(value)
        }
    var iconTint = Color.BLACK
        set(value) {
            progressButton?.iconTint = ColorStateList.valueOf(value)
        }
    var btn_elevation = -1f
        set(value) {
            progressButton?.elevation = value
        }
    var icon: Drawable? = null
        set(value) {
            progressButton?.icon = value
        }
    var iconSize = -1
        set(value) {
            progressButton?.iconSize = value
        }
    var textSize = -1f
        set(value) {
            progressButton?.textSize = value
        }
    var iconPadding = 0
        set(value) {
            progressButton?.iconPadding = value
        }
    var backgroundTintMode: Display.Mode? = null
    var capsText = false
        set(value) {
            progressButton?.isAllCaps = value
        }
    var iconGravity = 0
        set(value) {
            progressButton?.iconGravity = value
        }
    var strokeColor = -1
        set(value) {
            progressButton?.strokeColor = ColorStateList.valueOf(value)
        }
    var strokeWidth =-1
        set(value) {
            progressButton?.strokeWidth = value
        }
    var rippleColor = -1
        set(value) {
            progressButton?.rippleColor = ColorStateList.valueOf(value)
        }
    var cornerRadius = -1
        set(value) {
            progressButton?.cornerRadius = value
        }


    internal var animationListener: AnimationListener? = null

    internal var animator: ValueAnimator? = null
    internal var yourListener: View.OnClickListener? = null

    var leftPadding = 0

    var topPadding = 0
    var rightPadding = 0
    var bottomPadding = 0
    var parentWidth = 0
    var parentHeight = 0



     init {
         attrs?.let {
             var pa = context.obtainStyledAttributes(
                 it,
                 R.styleable.ProgressButton, 0, 0
             )



                 this.progressBGColor = pa.getColor(R.styleable.ProgressButton_p_progressBGColor, Color.WHITE)
                 this.progressColor = pa.getColor(R.styleable.ProgressButton_p_progressColor, Color.BLACK)
                 this.isArrowVisible = pa.getBoolean(R.styleable.ProgressButton_p_arrowVisible, true)

                this.bgColor = pa.getColor(R.styleable.ProgressButton_p_bgColor, Color.BLACK)
               this.progressText = pa.getString(R.styleable.ProgressButton_p_text)


                 textColor = pa.getColor(R.styleable.ProgressButton_p_textColor, -1)
                 backgroundTint = pa.getColor(R.styleable.ProgressButton_p_backgroundTint, -1)
                 iconTint = pa.getColor(R.styleable.ProgressButton_p_iconTint, -1)
                 strokeColor = pa.getColor(R.styleable.ProgressButton_p_strokeColor, -1)
                 rippleColor = pa.getColor(R.styleable.ProgressButton_p_rippleColor, -1)
//            backgroundTintMode=pa.get(R.styleable.ProgressButton_p_backgroundTintMode,0)
                 btn_elevation = pa.getDimensionPixelSize(R.styleable.ProgressButton_p_elevation, -1).toFloat()
                 iconSize = pa.getDimensionPixelSize(R.styleable.ProgressButton_p_iconSize, -1)
                 textSize = pa.getDimensionPixelSize(R.styleable.ProgressButton_p_textSize, -1).toFloat()
                 iconPadding = pa.getDimensionPixelSize(R.styleable.ProgressButton_p_strokeWidth, -1)
                 strokeWidth = pa.getDimensionPixelSize(R.styleable.ProgressButton_p_iconPadding, -1)
                 cornerRadius = pa.getDimensionPixelSize(R.styleable.ProgressButton_p_cornerRadius, -1)
                 icon = pa.getDrawable(R.styleable.ProgressButton_p_icon)
                 capsText = pa.getBoolean(R.styleable.ProgressButton_p_capsText, false)
                 iconGravity = pa.getInt(R.styleable.ProgressButton_p_iconGravity, MaterialButton.ICON_GRAVITY_START)

                 pa.recycle()

         }


        createProgressView()
        createButtonView()

        yourListener = View.OnClickListener {
            performClick()
        }

        progressButton?.setOnClickListener(yourListener)
        this.post {

            setSizeView()
        }
        progressButton?.post {
            initialWidth = progressButton?.width!!

        }
        mCircleView?.post {
            mCircleView?.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            finalWidth = mCircleView?.width!!
        }
        if(progressText!=null) {
            val textPaint = progressButton?.paint
            textWidth = textPaint?.measureText(progressText)?.toInt()!!
        }


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(measuredWidth + dp2px(3.5f) * 2, measuredHeight + dp2px(3.5f) * 2)

    }

    fun setOnAnimationListener(animationListener: AnimationListener) {
        this.animationListener = animationListener
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //setSizeView()

    }


    private fun setSizeView() {

        parentWidth = width
        parentHeight = height
        leftPadding = paddingLeft
        rightPadding = paddingRight
        topPadding = paddingTop
        bottomPadding = paddingBottom


        setPadding(0, 0, 0, 0)
        val barParams = FrameLayout.LayoutParams(parentWidth, parentHeight)

        barParams.gravity = Gravity.CENTER

        progressButton?.layoutParams = barParams

        progressButton?.setPadding(leftPadding, topPadding, rightPadding, bottomPadding)


        val barParams1 = FrameLayout.LayoutParams((parentHeight * 0.80).toInt(), (parentHeight * 0.80).toInt())
        barParams1.gravity = Gravity.CENTER
        mCircleView?.layoutParams = barParams1
        mCircleView?.setPadding(leftPadding, topPadding, rightPadding, bottomPadding)

        if (parentHeight > dp2px(75.0f))
            mProgress?.setStyle(CircularProgressDrawable.LARGE)
    }


    private fun createProgressView() {
        mCircleView = CircleImageView(context, progressBGColor)
        mProgress = CircularProgressDrawable(context)
        mProgress?.setColorSchemeColors(progressColor)
        mProgress?.setStyle(CircularProgressDrawable.DEFAULT)

        mProgress?.arrowEnabled = isArrowVisible

        mCircleView?.setImageDrawable(mProgress)
        mCircleView?.visibility = View.GONE

        val barParams =
            FrameLayout.LayoutParams(dp2px(40f), dp2px(40f))
        barParams.gravity = Gravity.CENTER
        mCircleView?.layoutParams = barParams
        addView(mCircleView)

    }

    private fun createButtonView() {
        progressButton = MaterialButton(context)
        if(progressText!=null)
        progressButton?.text = progressText


        if(textColor>-1)
        progressButton?.setTextColor(textColor)

     if(backgroundTint>-1)
        progressButton?.backgroundTintList = ColorStateList.valueOf(backgroundTint)

        if(iconTint>-1)
        progressButton?.iconTint = ColorStateList.valueOf(iconTint)
        if(btn_elevation>-1)
        progressButton?.elevation = btn_elevation.toFloat()
        if(iconSize>-1)
        progressButton?.iconSize = iconSize
        if(textSize>-1)
        progressButton?.textSize = textSize.toFloat()

        progressButton?.isAllCaps = capsText
        if(iconGravity>-1)
        progressButton?.iconGravity = iconGravity
        progressButton?.iconPadding = iconPadding
        if(strokeColor>-1)
        progressButton?.strokeColor = ColorStateList.valueOf(strokeColor)
        if(strokeWidth>-1)
        progressButton?.strokeWidth = strokeWidth
//        progressButton?.backgroundTintMode = backgroundTintMode
        if(icon!=null)
        progressButton?.icon = icon
        if(cornerRadius>-1)
        progressButton?.cornerRadius = cornerRadius
        if(rippleColor>-1)
        progressButton?.rippleColor = ColorStateList.valueOf(rippleColor)

        progressButton?.gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL

        if(bgColor>-1)
        progressButton?.setBackgroundColor(bgColor)

        val barParams =
            FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, dp2px(50f))
        barParams.gravity = Gravity.CENTER
        progressButton?.layoutParams = barParams
        addView(progressButton)
    }

    fun startAnimation() {

        isStartAnim = true
        isEnabled = false
        animationListener?.startAnimationListener()

        widthAnimator(progressButton!!, initialWidth, 0).start()


    }

    fun endAnimation() {
        isStartAnim = false
        progressButton?.visibility = View.VISIBLE
        reset()
        mCircleView?.visibility = View.GONE
        isEnabled = true

        widthAnimator(progressButton!!, 0, initialWidth).start()
        animationListener?.endAnimationListener()

    }


    internal fun widthAnimator(view: View, initial: Int, final: Int) =
        ValueAnimator.ofInt(initial, final).apply {
            addUpdateListener { animation ->
                if (animation.animatedValue as Int <= textWidth + dp2px(25f) && (view as MaterialButton).text.isNotEmpty()) {
                    view.text = ""
                } else if (animation.animatedValue as Int >= textWidth + dp2px(25f) && !(view as MaterialButton).text.isNotEmpty()) {
                    view.text = progressText
                }

                view.updateWidth(animation.animatedValue as Int)
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // done
                    if (initialWidth > final) {
                        progressButton?.visibility = View.GONE
                        progressbarAnim()


                    }


                }
            })

            duration = 400

        }

    fun progressbarAnim() {
        if (animator != null) {
            animator!!.removeAllUpdateListeners()
            animator!!.cancel()
        }
        mCircleView?.visibility = View.VISIBLE

        animator = ValueAnimator.ofInt(0, 255)
        animator!!.duration = 200
        animator?.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->

            mCircleView?.alpha = valueAnimator.animatedValue as Int / 255f


        })
        animator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // done
                mProgress?.start()


            }
        })
        animator?.start()
    }

    internal fun heightAnimator(view: View, initial: Int, final: Int) =
        ValueAnimator.ofInt(initial, final).apply {
            addUpdateListener { animation ->
                view.updateHeight(animation.animatedValue as Int)
            }
        }

    internal fun View.updateWidth(width: Int) {
        val layoutParams = this.layoutParams
        layoutParams.width = width
        this.layoutParams = layoutParams
    }

    internal fun View.updateHeight(height: Int) {
        val layoutParams = this.layoutParams
        layoutParams.height = height
        this.layoutParams = layoutParams
    }

    internal fun reset() {
        mCircleView?.clearAnimation()
        mProgress?.stop()
        mProgress?.arrowEnabled = true

    }

    fun px2dp(px: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, this.resources.displayMetrics)
    }

    fun dp2px(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), this.resources.displayMetrics)
            .toInt()
    }

    interface AnimationListener {
        fun startAnimationListener()
        fun endAnimationListener()
    }


}