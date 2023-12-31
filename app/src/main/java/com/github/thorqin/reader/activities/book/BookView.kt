package com.github.thorqin.reader.activities.book

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.github.thorqin.reader.App
import kotlin.math.floor
import android.graphics.*
import kotlin.math.ceil

const val MAX_TITLE_LINES = 4

class BookView : View {
	constructor(context: Context): super(context) {
		createPaint()
	}
	constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
		createPaint()
	}

	private var text = ""
	private var bookName = ""
	private var chapterName = ""
	private var pageNo = ""
	private var progressInfo = ""
	private var pageIndex: Int? = null

	private lateinit var paint: Paint
	private lateinit var descPaint: Paint
	private lateinit var titlePaint: Paint

	/**** DEBUG ****/
//	private lateinit var debugPaint: Paint
//	private var touchPoint: PointF? = null

	private var cw = 0f
	private var lh = 0
	private var titleLh = 0
	private var titleHeight = 0f
	private var titleCw: Float = 0f
	private var paintWidth = 0f
	private var paintHeight = 0f
	private var paintLeft = 0f
	private var paintTop = 0f
	private var footBarTop = 0f
	private var headBarTop = 0f
	private var textOffsetY = 0f
	private var textOffsetX = 0f
	private var textOffset2X = 0f
	private var titleOffsetY = 0f
	private var descOffsetY = 0f
	private var pxPerDp = 0
	private var barHeight: Int = 0



	var textSize = 24
		set(value) {
			field = value
			createPaint()
			initValues()
			invalidate()
		}

	private var descColor = "#666666"
		set(value) {
			field = value
			createPaint()
			invalidate()
		}

	var textColor = "#aaaaaa"
		set(value) {
			field = value
			createPaint()
			invalidate()
		}

	fun setBookInfo(bookName: String, chapterName: String, text: String, pageNo: String, progressInfo: String, pageIndex: Int) {
		this.text = text
		this.bookName = bookName
		this.chapterName = chapterName
		this.pageNo = pageNo
		this.progressInfo = progressInfo
		this.pageIndex = pageIndex
		paint.isAntiAlias = true
		invalidate()
	}

	private fun isAscii(c: Char): Boolean {
		return c in 'a'..'z' || c in 'A'..'Z'
	}

	private fun chooseText(txt: String, pos: Int, result: MutableList<String>) {
		val c = txt[pos]
		val ub = Character.UnicodeBlock.of(c)
		if (c in 'a'..'z' || c in 'A'..'Z') {
			var start = pos
			var end = pos + 1
			while (start > 0) {
				if (isAscii(txt[start - 1])) {
					start--
				} else {
					break
				}
			}
			while (end < txt.length) {
				if (isAscii(txt[end])) {
					end++
				} else {
					break
				}
			}
			result.add(txt.substring(start, end))
		} else if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
			ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
			ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A) {
			result.add(c.toString())
			val app = context.applicationContext as App
			for (i in 2..4) {
				for (j in 1..i) {
					val start = pos - j + 1
					if (start >= 0 && start + i < txt.length) {
						val s = txt.substring(start, start + i)
						// println(s)
						if (app.isChinesePhrase(s)) {
							result.add(s)
						}
					}
				}
			}
			result.sortWith { p0, p1 ->
				p1.length - p0.length
			}
		}
	}

	fun hitTest(x: Float, y: Float): List<String> {
		/**** DEBUG ****/
//		touchPoint = PointF(x, y)
//		this.invalidate()

		val result = arrayListOf<String>()

		val caption = if (chapterName.isEmpty()) this.bookName else chapterName
		var hitTheTarget = false
		titleHeight = 0f
		if (pageIndex == 0) {
			drawTitle(null, caption, HitTest(x, y) {
				hitTheTarget = true
				chooseText(caption, it, result)
			})
			if (hitTheTarget) {
				return result
			}
		}
		drawContent(null, HitTest(x, y) {
			hitTheTarget = true
			chooseText(text, it, result)
		})
		return result
	}

	private fun createPaint() {

		pxPerDp = App.dip2px(context, 1f)
		barHeight = pxPerDp * 24

		lh = App.dip2px(context, (1.5f * textSize))

		val rect = Rect()
		descPaint = Paint()
		descPaint.typeface = Typeface.MONOSPACE
		descPaint.textSize = App.dip2px(context, 14f).toFloat()
		descPaint.color = Color.parseColor(descColor)
		descPaint.isAntiAlias = true
		descPaint.getTextBounds("a中", 0, 2, rect)
		descOffsetY = -rect.top.toFloat()+ (barHeight - descPaint.textSize) / 2f

		paint = Paint()
		paint.typeface = Typeface.MONOSPACE
		paint.textSize = App.dip2px(context, textSize.toFloat()).toFloat()
		paint.color = Color.parseColor(textColor)
		paint.isAntiAlias = true
		cw = ceil(paint.measureText(" "))

		paint.getTextBounds("a中", 0, 2, rect)
		textOffsetY = -rect.top.toFloat() + (lh - textSize * pxPerDp) / 2f

		var w = ceil(paint.measureText("a"))
		paint.getTextBounds("a", 0, 1, rect)
		textOffsetX = (w - (rect.right - rect.left)) / 2f

		w = ceil(paint.measureText("中"))
		paint.getTextBounds("中", 0, 1, rect)
		textOffset2X = (w - (rect.right - rect.left)) / 2f

		titlePaint = Paint()
		titlePaint.typeface = Typeface.MONOSPACE
		titlePaint.textSize = App.dip2px(context, textSize * 1.5f).toFloat()
		titlePaint.color = Color.parseColor(textColor)
		titlePaint.isAntiAlias = true

		titleCw = ceil(titlePaint.measureText(" "))
		titlePaint.getTextBounds("a中", 0, 2, rect)
		titleLh = rect.bottom - rect.top
		titleOffsetY = -rect.top.toFloat()

		/**DEBUG**/
//		debugPaint = Paint()
//		debugPaint.style = Paint.Style.STROKE
//		debugPaint.color = Color.parseColor(textColor)
//		debugPaint.strokeWidth = 1f
	}



	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		super.onLayout(changed, left, top, right, bottom)
		if (changed) {
			initValues()
		}
	}

	private fun initValues() {


		paintWidth = (width - paddingLeft - paddingRight).toFloat()

		val numberOfChars = floor(paintWidth / cw).toInt()
		paintWidth = if (numberOfChars % 2 == 1) {
			cw * (numberOfChars - 1)
		} else {
			cw * numberOfChars
		}

		paintHeight = height - paddingTop - paddingBottom - barHeight * 2f
		val numberOfLines = floor(paintHeight / lh)
		paintHeight = numberOfLines * lh

		paintLeft = (width - paintWidth) / 2f
		paintTop = (height - paintHeight) / 2f

		headBarTop = paddingTop.toFloat()
		footBarTop = (height - paddingBottom - barHeight).toFloat()

	}

	override fun onDraw(canvas: Canvas?) {
		if (canvas == null) {
			return
		}

		val caption = if (chapterName.isEmpty()) this.bookName else chapterName
		val title = if (pageIndex == 0) this.bookName else caption
		// TOP BAR
		canvas.drawText(title , paintLeft, headBarTop + descOffsetY, descPaint)


		// FOOT BAR
		canvas.drawText(this.pageNo, paintLeft + pxPerDp * 20, footBarTop + descOffsetY, descPaint)
		val descWidth = descPaint.measureText(this.progressInfo)
		canvas.drawText(this.progressInfo, paintWidth - descWidth - pxPerDp * 20, footBarTop + descOffsetY, descPaint)

		titleHeight = 0f
		if (pageIndex == 0) {
			drawTitle(canvas, caption, null)
		}
		drawContent(canvas, null)


		/**DEBUG**/
//		canvas?.drawRect(paintLeft, 0f, paintLeft + paintWidth, barHeight.toFloat(), debugPaint)
//		canvas?.drawRect(paintLeft, footBarTop, paintLeft + paintWidth, footBarTop + barHeight.toFloat(), debugPaint)
//		canvas?.drawRect(paintLeft, paintTop, paintLeft + paintWidth, paintTop + paintHeight, debugPaint)
//		if (touchPoint != null) {
//			canvas.drawCircle(touchPoint!!.x, touchPoint!!.y, lh / 2f, debugPaint)
//		}
	}

	private val zero = 0.toChar()

	private class HitTest(val x: Float, val y: Float, val onHit: (pos: Int) -> Unit)

	private fun drawTitle(canvas: Canvas?, title: String, hitTest: HitTest?) {
		var t = paintTop // + lh / 3f
		val l = paintLeft
//		val lh = titleLh
		titleHeight = 0f
		var lw = 0f
		var line = 1
		for ((i,c) in title.withIndex()) {
			val w = if (c > zero && c < 256.toChar()) {
				titleCw
			} else {
				2 * titleCw
			}
			if (lw + w > paintWidth) {
				if (line < MAX_TITLE_LINES) {
					lw = 0f
					t += lh + 3 * pxPerDp
					line++
				} else {
					break
				}
			}
			if (hitTest != null) {
				if (hitTest.x > paintLeft + lw && hitTest.x < paintLeft + lw + w &&
					hitTest.y > t && hitTest.y < t + lh) {
					hitTest.onHit(i)
				}
			} else {
				/**DEBUG**/
				//canvas?.drawRect(paintLeft + lw, t, paintLeft + lw + w, t + lh, debugPaint)
				canvas?.drawText(c.toString(), paintLeft + lw, t + titleOffsetY, titlePaint)
			}
			lw += w
		}
		if (hitTest == null) {
			t += lh + 3 * pxPerDp
			canvas?.drawRect(
				l, t,
				l + paintWidth, t + 1 * pxPerDp.toFloat(), titlePaint
			)
		}
		titleHeight = (line + 1) * lh.toFloat()
	}

	private fun drawContent(canvas: Canvas?, hitTest: HitTest?) {
		val totalLines = if (pageIndex == 0)
			floor((paintHeight - titleHeight).toDouble() / lh).toInt()
		else
			floor(paintHeight.toDouble() / lh).toInt()

		// var l = if (pageIndex == 0) 3 else 0
		var l = 0
		var lw = 0f

		var begin = true
		for ((i, c) in text.withIndex()) {
			if (c == '\r') {
				continue
			}
			if (c == '\n') {
				if (begin) {
					continue
				}
				if (l < totalLines - 1) {
					l++
					lw = 0f
					continue
				} else {
					return
				}
			}
			begin = false
			var w: Float
			var offsetX: Float
			if (c > zero && c < 256.toChar()) {
				w = cw
				offsetX = textOffsetX
			} else {
				w = 2 * cw
				offsetX = textOffset2X
			}
			if (lw + w > paintWidth) {
				if (l < totalLines - 1) {
					l++
					lw = 0f
				} else {
					return
				}
			}
			if (hitTest != null) {
				if (hitTest.x > paintLeft + lw && hitTest.x < paintLeft + lw + w &&
					hitTest.y > paintTop + titleHeight + l * lh && hitTest.y < paintTop + titleHeight + (l + 1) * lh) {
					hitTest.onHit(i)
					return
				}
			} else {
				/**DEBUG**/
//				canvas?.drawRect(paintLeft + lw, paintTop + titleHeight + l * lh, paintLeft + lw + w, paintTop + titleHeight + (l + 1) * lh, debugPaint)
				canvas?.drawText(c.toString(), paintLeft + lw + offsetX, paintTop + titleHeight + textOffsetY + l * lh, paint)
			}
			lw += w
		}
	}

	private fun calcTitleHeight(title: String): Float {
		var lw = 0f
		var line = 1
		for (c in title) {
			val w = if (c > zero && c < 256.toChar()) {
				titleCw
			} else {
				2 * titleCw
			}
			if (lw + w > paintWidth) {
				if (line < MAX_TITLE_LINES) {
					lw = 0f
					line++
				} else {
					break
				}
			}
			lw += w
		}
		return (line + 1) * lh.toFloat()
	}

	fun calcPageSize(title: String, text: CharSequence, offset: Int, length: Int, pageIndex: Int) : Int {
		if (cw <= 0) {
			return 0
		}
		val len = if (length > text.length) {
			text.length
		} else length

		val contentHeight = if (pageIndex == 0) {
			paintHeight - calcTitleHeight(title)
		} else paintHeight

		val totalLines = floor(contentHeight.toDouble() / lh).toInt()

		var l = 0
		var lw = 0f
		val zero = 0.toChar()
		var begin = true
		for (i in offset until len) {
			val c = text[i]
			if (c == '\r') {
				continue
			}
			if (c == '\n') {
				if (begin) {
					continue
				}
				if (l < totalLines - 1) {
					l++
					lw = 0f
					continue
				} else {
					return i - offset
				}
			}
			begin = false
			val w = if (c > zero && c < 256.toChar()) {
				cw
			} else {
				2 * cw
			}
			if (lw + w > paintWidth) {
				if (l < totalLines - 1) {
					l++
					lw = w
				} else {
					return i - offset
				}
			} else {
				lw += w
			}
		}
		return len - offset
	}
}
