package com.github.thorqin.reader.component

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.widget.ScrollView

class HalfHeightScrollView @JvmOverloads constructor(
	context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {
	private val metrics = DisplayMetrics()
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val activity = context as Activity
		activity.windowManager.defaultDisplay.getMetrics(metrics)
		val newHeight = MeasureSpec.makeMeasureSpec((metrics.heightPixels * 0.7).toInt(), MeasureSpec.AT_MOST);

		super.onMeasure(widthMeasureSpec, newHeight)
	}
}
