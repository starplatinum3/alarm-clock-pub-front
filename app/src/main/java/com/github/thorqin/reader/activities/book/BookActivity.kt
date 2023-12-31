package com.github.thorqin.reader.activities.book

import android.animation.Animator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.chenjianlink.android.alarmclock.R
//import kotlinx.android.synthetic.main.activity_book.*
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.*
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.*
import android.view.View.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.thorqin.reader.App
import com.github.thorqin.reader.activities.setting.SettingsActivity
import cn.chenjianlink.android.alarmclock.databinding.ActivityBookBinding
import cn.chenjianlink.android.alarmclock.utils.LogcatHelper
import com.github.thorqin.reader.services.TTSService
import com.github.thorqin.reader.utils.*
import com.koushikdutta.async.http.AsyncHttpClient
import com.koushikdutta.async.http.AsyncHttpGet
import com.koushikdutta.async.http.AsyncHttpResponse
import java.io.File
import java.lang.Exception
import java.net.URLEncoder
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs
import kotlin.math.floor


class BookActivity : AppCompatActivity() {

	companion object {
		private const val TITLE_LINE_SIZE = 50
	}

	enum class Direction(var value: Int) {
		LEFT(0),
		RIGHT(1)
	}

	private lateinit var ttsService: TTSService
	private val serviceConnection = object: ServiceConnection {
		override fun onServiceDisconnected(p0: ComponentName?) {

		}

		override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
			ttsService = (p1 as TTSService.TTSBinder).service
			if (::fileInfo.isInitialized) {
				ttsService.setFileInfo(fileInfo)
			}
			ttsService.setListener(object: TTSService.StateListener {
				override fun onStart() {
					if (!ttsPlaying) {
						ttsPlaying = true
						val drawable = ContextCompat.getDrawable(this@BookActivity, R.drawable.ic_pause_white_24dp)
						binding.ttsButton.setImageDrawable(drawable)
						binding.ttsButton.invalidateDrawable(drawable!!)
						if (showActionBar) {
							toggleActionBar()
						}
					}
				}

				override fun onStop() {
					if (ttsPlaying) {
						ttsPlaying = false
						val drawable = ContextCompat.getDrawable(this@BookActivity, R.drawable.ic_play_arrow_white_24dp)
						binding.ttsButton.setImageDrawable(drawable)
						binding.ttsButton.invalidateDrawable(drawable!!)
						if (!showActionBar) {
							toggleActionBar()
						}
					}
				}

				override fun onUpdate() {
					showContent(true)
				}

			})
		}

	}


	private lateinit var handler: Handler
	private lateinit var summary: App.FileSummary
	private lateinit var fileInfo: App.FileDetail
	private var boxWidth: Float = 0F
	private var atBegin = false
	private var atEnd = false
	private var showActionBar = false
	private var showSceneBar = false
	private var showFontSizeBar = false
	private var ttsPlaying = false

	private lateinit var binding: ActivityBookBinding

	private val app: App
		get() {
			return application as App
			// return App.getAp
		}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		val inflater = menuInflater
		inflater.inflate(R.menu.activity_book, menu)
		return true
	}

	override fun onDestroy() {
		try {
			unbindService(serviceConnection)
		} catch (e: Throwable) {
		}
		super.onDestroy()
	}


	@SuppressLint("RtlHardcoded", "ClickableViewAccessibility")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = ActivityBookBinding.inflate(layoutInflater)

		// handler = Handler(Looper.getMainLooper())
		//
		// val ttsIntent = Intent(this, TTSService::class.java)
		// bindService(ttsIntent, serviceConnection, Context.BIND_AUTO_CREATE)
		//
		// val key = intent.getStringExtra("key")
		// if (key == null) {
		// 	this.finish()
		// 	return
		// }
		// key 是啥 书本的名字吗
		// if (!app.config.files.containsKey(key)) {
		// 	app.toast(getString(R.string.invalid_config))
		// 	finish()
		// 	return
		// }

		setContentView(binding.root)

		var surface: View? = null
		binding.drawerBox.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
		binding.drawerBox.addDrawerListener(object : DrawerLayout.DrawerListener {
			override fun onDrawerStateChanged(newState: Int) {}

			override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
				surface = null
				if (showActionBar) {
					toggleActionBar()
				}
			}

			override fun onDrawerClosed(drawerView: View) {
				surface = null
			}

			override fun onDrawerOpened(drawerView: View) {
				surface = null
			}

		})

		binding.topicButton.setOnClickListener {
			toggleActionBar()
			binding.drawerBox.openDrawer(Gravity.LEFT, true)
			(binding.topicList.adapter as TopicListAdapter).readChapter = fileInfo.readChapter
			binding.topicList.requestLayout()
			binding.topicList.setSelection(fileInfo.readChapter)
		}

		binding.brightness.setOnClickListener {
			toggleActionBar {
				toggleSceneBar()
			}
		}

		binding.fontSize.setOnClickListener {
			toggleActionBar {
				toggleFontSizeBar()
			}
		}

		binding.smallSize.setOnClickListener {
			if (app.config.fontSize != App.FontSize.SMALL) {
				app.config.fontSize = App.FontSize.SMALL
				resizeFont()
			}
		}

		binding.normalSize.setOnClickListener {
			if (app.config.fontSize != App.FontSize.NORMAL) {
				app.config.fontSize = App.FontSize.NORMAL
				resizeFont()
			}
		}

		binding.bigSize.setOnClickListener {
			if (app.config.fontSize != App.FontSize.BIG) {
				app.config.fontSize = App.FontSize.BIG
				resizeFont()
			}
		}

		binding.brightnessButton.setOnClickListener {
			app.config.sunshineMode = binding.sunshineMode.tag != R.drawable.radius_button_checked
			applySceneMode()
			app.saveConfig()
		}

		binding.sunshineMode.setOnClickListener {
			app.config.sunshineMode = binding.sunshineMode.tag != R.drawable.radius_button_checked
			applySceneMode()
			app.saveConfig()
		}

		binding.eyeCareMode.setOnClickListener {
			app.config.eyeCareMode = binding.eyeCareMode.tag != R.drawable.radius_button_checked
			applySceneMode()
			app.saveConfig()
		}

		binding.setting.setOnClickListener {
			toggleActionBar()
			val intent = Intent(this, SettingsActivity::class.java)
			this.startActivityForResult(intent, 1)
		}

		applySceneMode()

		// summary = app.config.files[key] as App.FileSummary
		val absFilename = LogcatHelper.getAbsFilename();
		summary=App.FileSummary()
		summary.path=absFilename
		summary.name="log_file"
		binding.toolbar.title = summary.name
		setSupportActionBar(binding.toolbar)
		supportActionBar?.title = summary.name
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		// supportActionBar?.hide()
		binding.appBar.visibility = INVISIBLE
		binding.footBar.visibility = INVISIBLE
		binding.sceneBar.visibility = INVISIBLE
		binding.fontSizeBar.visibility = INVISIBLE
		handler.postDelayed({
			binding.appBar.translationY = -binding.appBar.height.toFloat()
			binding.appBar.visibility = GONE
			binding.footBar.translationY = binding.footBar.height.toFloat()
			binding.footBar.visibility = GONE
			binding.sceneBar.translationY = binding.sceneBar.height.toFloat()
			binding.sceneBar.visibility = GONE
			binding.fontSizeBar.translationY = binding.fontSizeBar.height.toFloat()
			binding.fontSizeBar.visibility = GONE
		}, 50)

		binding.bufferView.addOnLayoutChangeListener { _, left, top, right, bottom, _, _, _, _ ->
			boxWidth = binding.flipper.measuredWidth.toFloat()
			// println("boxWidth: $boxWidth")
			val newW = right - left
			val newH = bottom - top
			if (::fileInfo.isInitialized) {
				if (fileInfo.screenWidth != newW && fileInfo.screenHeight != newH
					&& newW != 0 && newH != 0
				) {
					initBook(newW, newH)
				}
			} else {
				if (newW != 0 && newH != 0) {
					initBook(newW, newH)
				}
			}
			setPos()
		}


		var startX: Float? = null
		var startY: Float? = null
		var hitTestRunable: Runnable? = null
		var viewX: Float? = null
		var moveDirection: Direction = Direction.LEFT
		// 界面切换动作 滑动翻页
		binding.flipper.setOnTouchListener { _, event ->
			when (event.action) {
				MotionEvent.ACTION_DOWN -> {
					when {
						showFontSizeBar -> {
							toggleFontSizeBar()
							false
						}
						showSceneBar -> {
							toggleSceneBar()
							false
						}
						showActionBar -> {
							toggleActionBar()
							false
						}
						else -> {
							startX = event.rawX
							startY = event.y

							/**DEBUG**/
							// println("rawY: ${event.rawY}, y: $startY")

							hitTestRunable = Runnable {
								hitTestRunable = null
								if (startX != null && startY != null) {
									val currentView = binding.flipper.getChildAt(1) as BookView
									val hitResult = currentView.hitTest(startX!!, startY!!)
									if (hitResult.isNotEmpty()) {
										startX = null
										startY = null
										viewX = null
										explain(hitResult)
									}
								}
							}.also {
								handler.postDelayed(it,300)
							}
							surface == null
						}
					}
				}
				MotionEvent.ACTION_MOVE -> {
					if (startX != null) {
						val diffX = abs(event.rawX - startX!!)
						val diffY = abs(event.y - startY!!)
						// println("diffX: $diffX diffY: $diffY")
						hitTestRunable?.let {
							if (diffX >= 10 || diffY >= 10) {
								handler.removeCallbacks(it)
								hitTestRunable = null
							}
						}
						if (surface == null) {
							@Suppress("ControlFlowWithEmptyBody")
							if (event.rawX > startX!! && atBegin) {
								// do nothing
							} else if (event.rawX < startX!! && atEnd) {
								// do nothing
							} else {
								if (event.rawX > startX!! + 10f) {
									moveDirection = Direction.RIGHT
									surface = binding.flipper.getChildAt(2)
								} else if (event.rawX < startX!! - 10f) {
									moveDirection = Direction.LEFT
									surface = binding.flipper.getChildAt(1)
								}

								viewX = surface?.translationX
							}
						} else {
							surface?.elevation = 20f
							when (moveDirection) {
								Direction.RIGHT -> {
									if (event.rawX > startX!!) {
										surface!!.translationX = viewX!! + event.rawX - startX!!
									}
								}
								else -> {
									if (event.rawX < startX!!) {
										surface!!.translationX = viewX!! + event.rawX - startX!!
									}
								}
							}
						}
					}
					true
				}
				MotionEvent.ACTION_UP -> {
					val diff = boxWidth / 8
					if (surface != null) {
						when (moveDirection) {
							Direction.RIGHT -> { // flip to previous page
								if (event.rawX > startX!! + diff) {
									val toPos = 0f
									ttsService.stop()
									moveViewTo(surface!!, toPos) {
										val bottomView = binding.flipper.getChildAt(0)
										binding.flipper.removeViewAt(0)
										binding.flipper.addView(bottomView)
										setPos()
										surface?.elevation = 0f
										surface = null
										fileInfo.previous()
										fileInfo.syncTTSPoint()
										if (ttsPlaying) {
											ttsService.play()
										}
										handler.postDelayed({
											showContent(false)
										}, 50)
									}
								} else {
									val toPos = -boxWidth
									moveViewTo(surface!!, toPos) {
										surface?.elevation = 0f
										surface = null
										// Restore, no change
									}
								}
							}
							else -> { // flip to next page
								if (event.rawX < startX!! - diff) {
									ttsService.stop()
									moveViewTo(surface!!, -boxWidth) {
										val topView = binding.flipper.getChildAt(2)
										binding.flipper.removeViewAt(2)
										binding.flipper.addView(topView, 0)
										setPos()
										surface?.elevation = 0f
										surface = null
										fileInfo.next()
										fileInfo.syncTTSPoint()
										if (ttsPlaying) {
											ttsService.play()
										}
										handler.postDelayed({
											showContent(false)
										}, 50)
									}
								} else {
									moveViewTo(surface!!, 0f) {
										surface?.elevation = 0f
										surface = null
										// Restore, no change
									}
								}
							}
						}
					} else {
						if (startX != null && abs(event.rawX - startX!!) < 10 && startY != null && abs(event.y - startY!!) < 10) {
							// IS CLICK
							if (startX!! > boxWidth / 4 && startX!! < boxWidth / 4 * 3) {
								toggleActionBar()
							} else if (startX!! < boxWidth / 4 && !atBegin) {
								if (app.config.clickToFlip) {
									prevPage()
								}
							} else if (startX!! > boxWidth / 4 * 3 && !atEnd) {
								if (app.config.clickToFlip) {
									nextPage()
								}
							}
						}
					}
					startX = null
					startY = null
					hitTestRunable?.let {
						handler.removeCallbacks(it)
						hitTestRunable = null
					}
					viewX = null
					true
				}
				else -> false
			}
		}

		binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
				binding.pageNoText.text = (binding.seekBar.progress + 1).toString()
			}

			override fun onStartTrackingTouch(p0: SeekBar?) {
				binding.ttsButton.visibility = GONE
				binding.pageNo.visibility = VISIBLE
			}

			override fun onStopTrackingTouch(p0: SeekBar?) {
				binding.pageNo.visibility = GONE
				binding.ttsButton.visibility = VISIBLE
				ttsService.stop()
				fileInfo.setNewReadPage(binding.seekBar.progress)
				fileInfo.syncTTSPoint()
				if (ttsPlaying) {
					ttsService.play()
				}
				showContent(true)
			}

		})

		binding.prevTopic.setOnClickListener {
			ttsService.stop()
			fileInfo.prevTopic()
			fileInfo.syncTTSPoint()
			if (ttsPlaying) {
				ttsService.play()
			}
			showContent(true)
		}

		binding.nextTopic.setOnClickListener {
			ttsService.stop()
			fileInfo.nextTopic()
			fileInfo.syncTTSPoint()
			if (ttsPlaying) {
				ttsService.play()
			}
			showContent(true)
		}

		binding.ttsButton.setOnClickListener {
			if (!ttsService.isAvailable) {
				App.toast(this, "请安装中文 TTS 引擎后再使用本功能！")
			} else {
				ttsPlaying = !ttsPlaying
				if (ttsPlaying) {
					ttsPlaying = ttsService.play(true)
				} else {
					ttsService.stop(true)
				}
				val drawable = if (ttsPlaying) {
					ContextCompat.getDrawable(this, R.drawable.ic_pause_white_24dp)
				} else {
					ContextCompat.getDrawable(this, R.drawable.ic_play_arrow_white_24dp)
				}
				binding.ttsButton.setImageDrawable(drawable)
				binding.ttsButton.invalidateDrawable(drawable!!)
				if (ttsPlaying && showActionBar) {
					toggleActionBar()
				} else if (!ttsPlaying && !showActionBar) {
					toggleActionBar()
				}
			}
		}

		openBook()
		keepScreenOn()
	}


	private fun prevPage() {
		if (!atBegin) {
			ttsService.stop()
			val moveView = binding.flipper.getChildAt(2)
			moveView.elevation = 20f
			moveViewTo(moveView, 0f) {
				val bottomView = binding.flipper.getChildAt(0)
				binding.flipper.removeViewAt(0)
				binding.flipper.addView(bottomView)
				setPos()
				moveView.elevation = 0f
				fileInfo.previous()
				fileInfo.syncTTSPoint()
				if (ttsPlaying) {
					ttsService.play()
				}
				handler.postDelayed({
					showContent(false)
				}, 50)
			}
		}
	}

	private fun nextPage() {
		if (!atEnd) {
			ttsService.stop()
			val moveView = binding.flipper.getChildAt(1)
			moveView.elevation = 20f
			moveViewTo(moveView, -boxWidth) {
				val topView = binding.flipper.getChildAt(2)
				binding.flipper.removeViewAt(2)
				binding.flipper.addView(topView, 0)
				setPos()
				moveView.elevation = 0f
				fileInfo.next()
				fileInfo.syncTTSPoint()
				if (ttsPlaying) {
					ttsService.play()
				}
				handler.postDelayed({
					showContent(false)
				}, 50)
			}
		}
	}

	override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
		return when (keyCode) {
			KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_PAGE_UP -> {
				prevPage()
				true
			}
			KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_PAGE_DOWN -> {
				nextPage()
				true
			}
			else -> super.onKeyUp(keyCode, event)
		}
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		if (ttsPlaying || !app.config.volumeFlip) {
			return super.onKeyDown(keyCode, event)
		}
		return when (keyCode) {
			KeyEvent.KEYCODE_VOLUME_DOWN -> {
				nextPage()
				true
			}
			KeyEvent.KEYCODE_VOLUME_UP -> {
				prevPage()
				true
			}
			else -> super.onKeyDown(keyCode, event)
		}
	}

	@SuppressLint("RtlHardcoded")
	override fun onBackPressed() {
		when {
			binding.drawerBox.isDrawerOpen(Gravity.LEFT) -> binding.drawerBox.closeDrawer(Gravity.LEFT)
			showActionBar -> toggleActionBar()
			showSceneBar -> toggleSceneBar()
			showFontSizeBar -> toggleFontSizeBar()
			else -> super.onBackPressed()
		}
	}

// 白天还是黑夜
	private fun applySceneMode() {
		val sunshine =
			if (app.config.sunshineMode) R.drawable.radius_button_checked else R.drawable.radius_button_normal
		binding.sunshineMode.setBackgroundResource(sunshine)
		binding.sunshineMode.tag = sunshine

		val eyeCare =
			if (app.config.eyeCareMode) R.drawable.radius_button_checked else R.drawable.radius_button_normal
		binding.eyeCareMode.setBackgroundResource(eyeCare)
		binding.eyeCareMode.tag = eyeCare

		val drawable = if (app.config.sunshineMode) {
			ContextCompat.getDrawable(this, R.drawable.ic_wb_sunny_white_24dp)
		} else {
			ContextCompat.getDrawable(this, R.drawable.ic_brightness_2_white_24dp)
		}
		binding.brightnessButton.setImageDrawable(drawable)

		if (binding.brightnessButton.isShown) {
			binding.brightnessButton.hide()
			binding.brightnessButton.show()
		}


		val color = if (app.config.sunshineMode) {
			if (app.config.eyeCareMode) {
				"#fff0a0"
			} else {
				"#ffffff"
			}
		} else {
			if (app.config.eyeCareMode) {
				"#887740"
			} else {
				"#888888"
			}
		}

		for (i in 0 until binding.flipper.childCount) {
			val bookView = binding.flipper.getChildAt(i) as BookView
			bookView.textColor = color
		}
	}


	private fun toggleFontSizeBar(callback: (() -> Unit)? = null) {
		val anim = ValueAnimator()
		if (!showFontSizeBar) {
			binding.fontSizeBar.visibility = VISIBLE
			anim.setFloatValues(1f, 0f)
		} else {
			anim.setFloatValues(0f, 1f)
		}
		anim.duration = 200
		anim.addUpdateListener {
			val value = anim.animatedValue as Float
			binding.fontSizeBar.translationY = binding.fontSizeBar.height * value
		}
		anim.addListener(object : Animator.AnimatorListener {
			override fun onAnimationRepeat(p0: Animator?) {}
			override fun onAnimationEnd(p0: Animator?) {
				if (!showFontSizeBar) {
					binding.fontSizeBar.translationY = 0f
					showFontSizeBar = true
					callback?.invoke()
				} else {
					binding.fontSizeBar.visibility = GONE
					showFontSizeBar = false
					callback?.invoke()
				}
			}

			override fun onAnimationCancel(p0: Animator?) {}

			override fun onAnimationStart(p0: Animator?) {}
		})
		anim.start()
	}

	private fun toggleSceneBar(callback: (() -> Unit)? = null) {
		val anim = ValueAnimator()
		if (!showSceneBar) {
			binding.sceneBar.visibility = VISIBLE
			anim.setFloatValues(1f, 0f)
		} else {
			anim.setFloatValues(0f, 1f)
		}
		anim.duration = 200
		anim.addUpdateListener {
			val value = anim.animatedValue as Float
			binding.sceneBar.translationY = binding.sceneBar.height * value
		}
		anim.addListener(object : Animator.AnimatorListener {
			override fun onAnimationRepeat(p0: Animator?) {}
			override fun onAnimationEnd(p0: Animator?) {
				if (!showSceneBar) {
					binding.sceneBar.translationY = 0f
					showSceneBar = true
					callback?.invoke()
				} else {
					binding.sceneBar.visibility = GONE
					showSceneBar = false
					callback?.invoke()
				}
			}

			override fun onAnimationCancel(p0: Animator?) {}

			override fun onAnimationStart(p0: Animator?) {}
		})
		anim.start()
	}

	private fun toggleActionBar(callback: (() -> Unit)? = null) {
		val anim = ValueAnimator()
		var modifyTtsButton: Boolean = false
		if (!showActionBar) {
			modifyTtsButton = (binding.ttsButton.visibility == GONE)
			binding.appBar.visibility = VISIBLE
			binding.footBar.visibility = VISIBLE
			binding.brightnessButton.show()
			if (!ttsPlaying) {
				binding.ttsButton.visibility = VISIBLE
			}
			anim.setFloatValues(1f, 0f)
		} else {
			modifyTtsButton = (binding.ttsButton.visibility == VISIBLE && !ttsPlaying)
			anim.setFloatValues(0f, 1f)
		}
		anim.duration = 200
		anim.addUpdateListener {
			val value = anim.animatedValue as Float
			binding.footBar.translationY = binding.footBar.height * value
			binding.appBar.translationY = -binding.appBar.height * value
			binding.brightnessButton.alpha = 1 - value
			if (modifyTtsButton) {
				binding.ttsButton.alpha = (1 - value) * 0.9f
			}
		}
		anim.addListener(object : Animator.AnimatorListener {
			override fun onAnimationRepeat(p0: Animator?) {}
			override fun onAnimationEnd(p0: Animator?) {
				if (!showActionBar) {
					binding.appBar.translationY = 0f
					binding.footBar.translationY = 0f
					showActionBar = true
					callback?.invoke()
				} else {
					binding.brightnessButton.hide()
					if (modifyTtsButton) {
						binding.ttsButton.visibility = GONE
					}
					binding.appBar.visibility = GONE
					binding.footBar.translationY = -binding.appBar.height.toFloat()
					binding.footBar.visibility = GONE
					binding.footBar.translationY = binding.footBar.height.toFloat()
					showActionBar = false
					callback?.invoke()
				}
			}

			override fun onAnimationCancel(p0: Animator?) {}

			override fun onAnimationStart(p0: Animator?) {}
		})
		anim.start()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			android.R.id.home -> {
				finish()
				true
			}
			R.id.split_topic -> {
				inputTopicRule()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun moveViewTo(v: View, pos: Float, onEndCallback: () -> Unit) {
		val transAnim = ObjectAnimator.ofFloat(v, "translationX", pos)
		transAnim.duration = 100
		transAnim.addListener(object : Animator.AnimatorListener {
			override fun onAnimationRepeat(animation: Animator?) {}
			override fun onAnimationEnd(animation: Animator?) {
				onEndCallback()
			}

			override fun onAnimationCancel(animation: Animator?) {}
			override fun onAnimationStart(animation: Animator?) {}
		})

		transAnim.start()
	}

	private fun setPos() {
		binding.flipper.getChildAt(2).translationX = -boxWidth
		binding.flipper.getChildAt(1).translationX = 0f
		binding.flipper.getChildAt(0).translationX = 0f
	}


	private class PageInfo(
		var text: String,
		var chapterName: String,
		var readPage: Int,
		var chapterPage: Int
	)

	private fun getPrevPageInfo(): PageInfo? {
		if (fileInfo.chapters.size == 0) {
			return null
		}
		val p: Int
		var c = fileInfo.readChapter
		p = if (fileInfo.readPageOfChapter > 0) {
			fileInfo.readPageOfChapter - 1
		} else {
			if (c > 0) {
				c--
				if (fileInfo.chapters[c].pages.size > 0) {
					fileInfo.chapters[c].pages.size - 1
				} else {
					return null
				}
			} else {
				return null
			}
		}
		return PageInfo(
			fileInfo.getContent(c, p),
			fileInfo.chapters[c].name, fileInfo.readPage - 1, p
		)
	}

	private fun getCurrentPageInfo(): PageInfo {
		return PageInfo(
			fileInfo.getContent(fileInfo.readChapter, fileInfo.readPageOfChapter),
			fileInfo.chapters[fileInfo.readChapter].name, fileInfo.readPage, fileInfo.readPageOfChapter
		)
	}

	private fun getNextPageInfo(): PageInfo? {
		if (fileInfo.chapters.size == 0) {
			return null
		}
		val p: Int
		var c = fileInfo.readChapter
		p = if (fileInfo.readPageOfChapter < fileInfo.chapters[c].pages.size - 1) {
			fileInfo.readPageOfChapter + 1
		} else {
			if (c < fileInfo.chapters.size - 1) {
				c++
				if (fileInfo.chapters[c].pages.size > 0) {
					0
				} else {
					return null
				}
			} else {
				return null
			}
		}
		return PageInfo(
			fileInfo.getContent(c, p),
			fileInfo.chapters[c].name, fileInfo.readPage + 1, p
		)
	}

	private fun showContent(updateAll: Boolean) {
		val prevView = binding.flipper.getChildAt(2) as BookView
		val currentView = binding.flipper.getChildAt(1) as BookView
		val nextView = binding.flipper.getChildAt(0) as BookView

		if (updateAll) {
			val current = getCurrentPageInfo()
			// currentView.text = currentText
			currentView.setBookInfo(
				fileInfo.name,
				current.chapterName, current.text,
				"${fileInfo.readPage + 1} / ${fileInfo.totalPages}",
				"${floor((fileInfo.readPage + 1) / fileInfo.totalPages.toDouble() * 10000) / 100}%",
				current.chapterPage
			)
		}

		binding.seekBar.progress = fileInfo.readPage
		val prev = getPrevPageInfo()
		if (prev == null) {
			atBegin = true
		} else {
			atBegin = false
			//prevView.text = prevText
			prevView.setBookInfo(
				fileInfo.name,
				prev.chapterName, prev.text,
				"${prev.readPage + 1} / ${fileInfo.totalPages}",
				"${floor((prev.readPage + 1) / fileInfo.totalPages.toDouble() * 10000) / 100}%",
				prev.chapterPage
			)
		}

		val next = getNextPageInfo()
		if (next == null) {
			atEnd = true
		} else {
			atEnd = false
			// nextView.text = nextText
			nextView.setBookInfo(
				fileInfo.name,
				next.chapterName, next.text,
				"${next.readPage + 1} / ${fileInfo.totalPages}",
				"${floor((next.readPage + 1) / fileInfo.totalPages.toDouble() * 10000) / 100}%",
				next.chapterPage
			)
		}
		summary.lastReadTime = Date().time
		summary.progress = (fileInfo.readPage + 1).toFloat() / fileInfo.totalPages
		handler.postDelayed({
			app.saveFileState(fileInfo, fileInfo.key)
			app.saveConfig()
		}, 16)
	}

	@SuppressLint("RtlHardcoded")
	private fun onSelectTopic(chapterIndex: Int) {
		ttsService.stop()
		fileInfo.readChapter = chapterIndex
		fileInfo.readPageOfChapter = 0
		var p = 0
		for (i in 0 until chapterIndex) {
			p += fileInfo.chapters[i].pages.size
		}
		fileInfo.readPage = p
		fileInfo.syncTTSPoint()
		if (ttsPlaying) {
			ttsService.play()
		}
		showContent(true)
		binding.drawerBox.closeDrawer(Gravity.LEFT, true)
	}

	private fun openBook() {
		try {
			fileInfo = app.getFileConfig(summary.key)
			app.config.lastRead = summary.key
			val adapter = TopicListAdapter(this, fileInfo.chapters)
			adapter.onSelectTopic = this::onSelectTopic
			binding.topicList.adapter = adapter
			binding.seekBar.max = fileInfo.totalPages - 1
			if (fileInfo.fontSize != app.config.fontSize) {
				resizeFont()
				return
			}
			applySize()
			fileInfo.syncTTSPoint()
			// kotlin ::
			if (::ttsService.isInitialized) {
				ttsService.setFileInfo(fileInfo)
			}
			showContent(true)
		} catch (e: Exception) {
			println("Open index file failed, need to initialize!")
		}
	}

	private fun parseFile(file: File, pattern: Pattern, bookName: String): App.FileDetail {

		lateinit var charset: String
		file.inputStream().use {
			// Firstly we should detect file encoding
			charset = detectCharset(it)
		}

		file.reader(Charset.forName(charset)).use {
			val buffer = CharArray(8192)
			val line = StringBuilder(8192)
			val content = StringBuilder(8192)
			var lineStart = 0L
			var lineSize = 0
			var scan = 0L
			var lineEnd = true
			var beginChapter = true
			val fileInfo: App.FileDetail = app.newDetail()
			fileInfo.encoding = charset
			fileInfo.name = bookName
			fileInfo.fontSize = app.config.fontSize
			val emptyChapters = hashMapOf<String, App.Chapter>()

			var chapter = App.Chapter()
			chapter.name = fileInfo.name

			fun testEnd(c: Char?) {
				if (!beginChapter) {
					when {
						lineSize in 1..TITLE_LINE_SIZE -> // match line content
							when {
								pattern.matcher(line).find() -> {
									chapter.endPoint = lineStart
									val str = content.subSequence(0, content.length - lineSize).trimEnd()
									parseChapter(chapter, str, str.length)
									if (chapter.isEmpty) { // Remove index only chapter
										emptyChapters[chapter.name] = chapter
									} else {
										emptyChapters[chapter.name]?.delete = true
									}
									fileInfo.chapters.add(chapter)
									chapter = App.Chapter()
									chapter.name = line.trim().toString()
									beginChapter = true
									content.clear()
									line.clear()
									lineSize = 0
								}
								c != null -> {
									content.append(c)
									lineSize = 0
									line.clear()
								}
								else -> {
									chapter.endPoint = scan
									val str = content.trimEnd()
									parseChapter(chapter, str, str.length)
									if (chapter.isEmpty) {
										emptyChapters[chapter.name] = chapter
									} else {
										emptyChapters[chapter.name]?.delete = true
									}
									fileInfo.chapters.add(chapter)
								}
							}
						c != null -> {
							content.append(c)
							lineSize = 0
							line.clear()
						}
						else -> {
							chapter.endPoint = scan
							val str = content.trimEnd()
							parseChapter(chapter, str, str.length)
							if (chapter.isEmpty) {
								emptyChapters[chapter.name] = chapter
							} else {
								emptyChapters[chapter.name]?.delete = true
							}
							fileInfo.chapters.add(chapter)
						}
					}
				}
			}


			while (true) {
				val size = it.read(buffer, 0, 8192)
				if (size <= 0) {
					break
				}
				for (i in 0 until size) {
					val c = buffer[i]
					if (c == '\n') { //  || c == '\r'
						lineEnd = true
						testEnd(c)
					} else {
						if (beginChapter) {
							chapter.startPoint = scan
							beginChapter = false
						}
						content.append(c)
						if (lineEnd) {
							lineEnd = false
							lineStart = scan
						}
						lineSize++
						if (lineSize < TITLE_LINE_SIZE) {
							line.append(c)
						}
					}
					scan++
				}
			}
			testEnd(null)

			var i = 0
			while (i < fileInfo.chapters.size) {
				if (fileInfo.chapters[i].delete) {
					fileInfo.chapters.removeAt(i)
				} else {
					i++
				}
			}
			for (j in 0 until fileInfo.chapters.size) {
				fileInfo.totalPages += fileInfo.chapters[j].pages.size
			}

			return fileInfo

		}
	}

	private val emptyRegex = Regex("(\\s|\\n|\\r)*", RegexOption.MULTILINE)
	private fun parseChapter(
		chapter: App.Chapter,
		content: CharSequence,
		length: Int
	) {
		if (content.isEmpty() && chapter.name.isEmpty()) {
			return
		}
		var offset = 0
		var index = 0
		while (offset < length) {
			val pageSize = binding.bufferView.calcPageSize(chapter.name, content, offset, length, index)
			if (pageSize > 0) {
				val page = App.Page()
				page.start = chapter.startPoint + offset
				page.length = pageSize
				chapter.pages.add(page)
				index++
				offset += pageSize
			} else {
				break
			}
		}
		if (chapter.pages.isEmpty()) {
			val page = App.Page()
			page.start = chapter.startPoint + offset
			page.length = 0
			chapter.pages.add(page)
		}
		if (emptyRegex.matches(content)) {
			chapter.isEmpty = true
		}

	}

	private fun initBook(width: Int, height: Int) {
		var topicRules: List<String>? = null
		val topicRule = if (!::fileInfo.isInitialized) App.getTopicRule() else {
			topicRules = fileInfo.topicRules
			fileInfo.getTopicRule()
		}
		binding.bufferView.textSize = app.config.fontSize.value
		val pattern: Pattern = try {
			Pattern.compile(topicRule.replace(Regex("\\s+"), ""), Pattern.CASE_INSENSITIVE)
		} catch (e: Exception) {
			Pattern.compile(App.getTopicRule(), Pattern.CASE_INSENSITIVE)
		}
		try {
			var txtFilePath = summary.path
			var bookName = summary.name
			if (summary.path.endsWith(".epub", ignoreCase = true)) {
				txtFilePath = summary.path.substring(0, summary.path.length - 4) + "text"
				if (!File(txtFilePath).exists()) {
					bookName = EPub.epub2txt(summary.path, txtFilePath) ?: summary.name
				}
			}


			applySize()
			val file = File(txtFilePath)

			if (summary.hash == null) { // 首次打开计算文件 HASH
				val digest = MessageDigest.getInstance("SHA-256")
				file.inputStream().use {
					val buf = ByteArray(4096)
					var size = it.read(buf)
					while (size > 0) {
						digest.update(buf, 0, size)
						size = it.read(buf)
					}
				}
				summary.hash = hexString(digest.digest())
			}

			fileInfo = parseFile(file, pattern, bookName)
			fileInfo.topicRules = topicRules
			app.config.lastRead = summary.key
			fileInfo.key = summary.key
			fileInfo.path = txtFilePath
			fileInfo.screenWidth = width
			fileInfo.screenHeight = height
			binding.seekBar.max = fileInfo.totalPages - 1
			app.saveFileIndex(fileInfo, summary.key)
			fileInfo.syncTTSPoint()
			if (::ttsService.isInitialized) {
				ttsService.setFileInfo(fileInfo)
			}
			showContent(true)
			val adapter = TopicListAdapter(this, fileInfo.chapters)
			adapter.onSelectTopic = this::onSelectTopic
			binding.topicList.adapter = adapter
		} catch (e: Exception) {
			e.printStackTrace(System.err)
			app.toast(getString(R.string.cannot_init_book))
			finish()
		}
	}

	private fun applySize() {
		binding.smallSize.setBackgroundResource(R.drawable.radius_button_normal)
		binding.normalSize.setBackgroundResource(R.drawable.radius_button_normal)
		binding.bigSize.setBackgroundResource(R.drawable.radius_button_normal)

		when (app.config.fontSize) {
			App.FontSize.SMALL -> {
				binding.smallSize.setBackgroundResource(R.drawable.radius_button_checked)
			}
			App.FontSize.NORMAL -> {
				binding.normalSize.setBackgroundResource(R.drawable.radius_button_checked)
			}
			else -> {
				binding.bigSize.setBackgroundResource(R.drawable.radius_button_checked)
			}
		}
		binding.bufferView.textSize = app.config.fontSize.value
		for (i in 0 until 3) {
			(binding.flipper.getChildAt(i) as BookView).textSize = app.config.fontSize.value
		}
	}

	private fun resizeFont() {
		binding.bufferView.textSize = app.config.fontSize.value
		handler.postDelayed({
			if (fileInfo.chapters.size > 0) {
				val readPoint =
					fileInfo.chapters[fileInfo.readChapter].pages[fileInfo.readPageOfChapter].start
				val file = File(summary.path)
				file.reader(Charset.forName(fileInfo.encoding)).use {
					var lastEnd = 0L
					for (i in 0 until fileInfo.chapters.size) {
						val c = fileInfo.chapters[i]
						it.skip(c.startPoint - lastEnd)
						val buffer = CharArray((c.endPoint - c.startPoint).toInt())
						val size = it.read(buffer)
						val str = String(buffer, 0, size).trimEnd()
						val chapter = App.Chapter()
						chapter.name = c.name
						chapter.startPoint = c.startPoint
						chapter.endPoint = c.endPoint

						parseChapter(chapter, str, str.length)
						fileInfo.chapters[i] = chapter
						lastEnd = c.endPoint
					}
				}
				val readChapter = fileInfo.chapters[fileInfo.readChapter]
				var i = readChapter.pages.size - 1
				while (i >= 0) {
					if (readPoint >= readChapter.pages[i].start) {
						fileInfo.readPageOfChapter = i
						break
					}
					i--
				}
				var total = 0
				@Suppress("NAME_SHADOWING")
				for (i in 0 until fileInfo.chapters.size) {
					total += fileInfo.chapters[i].pages.size
				}
				fileInfo.totalPages = total
				fileInfo.calcReadPage()
				binding.seekBar.max = fileInfo.totalPages - 1
			}
			fileInfo.fontSize = app.config.fontSize
			app.saveFileIndex(fileInfo, fileInfo.key)
			app.saveFileState(fileInfo, fileInfo.key)
			applySize()
			showContent(true)
		}, 50)
	}

	private fun keepScreenOn() {
		if (app.config.neverLock) {
			window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
			@Suppress("DEPRECATION")
			window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
		} else {
			window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
			@Suppress("DEPRECATION")
			window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == 1) { // SETTING ACTIVITY CLOSED
			keepScreenOn()
		}
	}


	private fun addNewRule(onSuccess: (rule: String) -> Unit) {
		val layout = inflate(this, R.layout.input_dialog, null)
		val et = layout.findViewById<EditText>(R.id.editText)
		val msg = layout.findViewById<TextView>(R.id.errText)

		et.addTextChangedListener(object: TextWatcher {
			override fun afterTextChanged(s: Editable?) {
				msg.visibility = GONE
			}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

			}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

			}

		})
//
//		et.setText(if (fileInfo.topicRule == null) App.TOPIC_RULE else fileInfo.topicRule!!)
		val dialog = AlertDialog.Builder(this, R.style.dialogStyle).setTitle(getString(R.string.topic_rule))
			.setView(layout)
			.setPositiveButton(getString(R.string.ok), null)
			.setNegativeButton(getString(R.string.cancel), null)
			.setCancelable(false)
			.create()
		dialog.setOnShowListener {
			it as AlertDialog
			val button = it.getButton(AlertDialog.BUTTON_POSITIVE)
			button.setOnClickListener {
				val input = et.text.toString()
				if (input.trim() == "") {
					msg.visibility = VISIBLE
				} else {
					try {
						Pattern.compile(input)
						dialog.dismiss()

						onSuccess(input)

					} catch (e: Exception) {
						msg.visibility = VISIBLE
					}
				}
			}
		}
		dialog.show()
	}

	private fun inputTopicRule() {

		class Rule(var rule: String, var selected: Boolean)


		val layout = inflate(this, R.layout.select_topic_rule, null)
		val listBox = layout.findViewById<ListView>(R.id.listBox)

		fun getCurrentRules(): MutableList<Rule> {
			val rs = App.RULES.map {
				Rule(it, true)
			}.toMutableList()

			if (fileInfo.topicRules != null) {
				rs.forEach {
					it.selected = false
				}
				fileInfo.topicRules!!.forEach {
					if (rs.none { r ->
							if (r.rule == it) {
								r.selected = true
								true
							} else {
								false
							}
						}) {
						rs.add(Rule(it, true))
					}
				}
			}
			return rs
		}
		var rules = getCurrentRules()

		val adapter = object: BaseAdapter() {
			private val inflater: LayoutInflater = LayoutInflater.from(this@BookActivity)
			override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
				val checkbox = inflater.inflate(R.layout.topic_rule_item, null) as CheckBox
				checkbox.text = rules[position].rule
				checkbox.isChecked = rules[position].selected
				checkbox.tag = rules[position]
				checkbox.setOnClickListener {
					val item = it.tag as Rule
					item.selected = it.isSelected
				}
				return checkbox
			}

			override fun getItem(position: Int): Any {
				return rules[position]
			}

			override fun getItemId(position: Int): Long {
				return position.toLong()
			}

			override fun getCount(): Int {
				return rules.size
			}

		}
		listBox.adapter = adapter

		val addButton = layout.findViewById<Button>(R.id.addButton)
		addButton.setOnClickListener {
			addNewRule {
				if (rules.none {r ->
						if (r.rule == it) {
							r.selected = true
							true
						} else {
							false
						}
					}) {
					rules.add(Rule(it, true))
				}
				adapter.notifyDataSetChanged()
				handler.post {
					listBox.setSelection(listBox.bottom)
				}
			}
		}

		val dialog = AlertDialog.Builder(this, R.style.dialogStyle).setTitle(getString(R.string.topic_rule))
			.setView(layout)
			.setPositiveButton(getString(R.string.ok), null)
			.setNegativeButton(getString(R.string.cancel), null)
			.setNeutralButton(getString(R.string.reset), null)
			.setCancelable(false)
			.create()

		dialog.setOnShowListener {
			it as AlertDialog
			val button = it.getButton(AlertDialog.BUTTON_POSITIVE)
			button.setOnClickListener {
				fileInfo.topicRules = rules.filter { r ->
					r.selected
				}.map {r ->
					r.rule
				}
				dialog.dismiss()
				handler.postDelayed({
					initBook(binding.bufferView.width, binding.bufferView.height)
				}, 50)
			}
			val resetButton = it.getButton(AlertDialog.BUTTON_NEUTRAL)
			resetButton.setOnClickListener {
				rules = App.RULES.map {
					Rule(it, true)
				}.toMutableList()
				adapter.notifyDataSetChanged()
			}
		}
		dialog.show()
//		val layout = inflate(this, R.layout.input_dialog, null)
//		val et = layout.findViewById<EditText>(R.id.editText)
//		val msg = layout.findViewById<TextView>(R.id.errText)
//
//		et.addTextChangedListener(object: TextWatcher {
//			override fun afterTextChanged(s: Editable?) {
//				msg.visibility = GONE
//			}
//
//			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//			}
//
//			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//			}
//
//		})
//
//		et.setText(if (fileInfo.topicRule == null) App.TOPIC_RULE else fileInfo.topicRule!!)
//		val dialog = AlertDialog.Builder(this, R.style.dialogStyle).setTitle(getString(R.string.topic_rule))
//			.setView(layout)
//			.setPositiveButton(getString(R.string.ok), null)
//			.setNegativeButton(getString(R.string.cancel), null)
//			.setNeutralButton(getString(R.string.reset), null)
//			.setCancelable(false)
//			.create()
//		dialog.setOnShowListener {
//			it as AlertDialog
//			val button = it.getButton(AlertDialog.BUTTON_POSITIVE)
//			button.setOnClickListener {
//				val input = et.text.toString()
//				if (input.trim() == "") {
//					msg.visibility = VISIBLE
//				} else {
//					try {
//						Pattern.compile(input)
//						fileInfo.topicRule = input
//						dialog.dismiss()
//						handler.postDelayed({
//							initBook(bufferView.width, bufferView.height)
//						}, 50)
//					} catch (e: Exception) {
//						msg.visibility = VISIBLE
//					}
//				}
//			}
//			val resetButton = it.getButton(AlertDialog.BUTTON_NEUTRAL)
//			resetButton.setOnClickListener {
//				et.setText(App.TOPIC_RULE)
//			}
//		}
//		dialog.show()
	}

	private class Dict {
		var word: String? = null
		var phonetic: String? = null
		var definition: String? = null
//		var translation: String? = null
//		var exchange: String? = null
	}

	private fun createTranslationItem(item: Dict): View {
		val translationItem = inflate(this, R.layout.translation_item, null)
		val wordView = translationItem.findViewById<TextView>(R.id.translation_word)
		val phoneticView = translationItem.findViewById<TextView>(R.id.translation_phonetic)
		val definitionView = translationItem.findViewById<TextView>(R.id.translation_definition)
		wordView.text = item.word
		phoneticView.text = item.phonetic
		definitionView.text = item.definition
		return translationItem
	}


	private fun queryWordDefinition(txt: List<String>, success: (result: List<Dict>) -> Unit, error: (msg: String) -> Unit) {
		//var url = "http://138.91.1.176:8080/public/dict"

		app.getWebSiteURL({
			var url = "${it}public/dict"
			for ((i,s) in txt.withIndex()) {
				url += if (i == 0) {
					"?"
				} else {
					"&"
				}
				val word = URLEncoder.encode(s, "utf-8")
				url += "q=$word"
			}
			val listType = makeListType(Dict::class.java)
			val request = AsyncHttpGet(url)
			val client = AsyncHttpClient.getDefaultInstance()
			client.executeString(request,
				object:AsyncHttpClient.StringCallback() {
					override fun onCompleted(e: Exception?, response: AsyncHttpResponse?, result: String?) {
						if (e != null) {
							runOnUiThread {
								error(e.message ?: "服务暂时不可用!")
							}
							return
						}
						val list = json().fromJson(result, listType) as List<Dict>
						runOnUiThread {
							success(list)
						}
					}
				}
			)
		}, {
			error("服务暂时不可用！")
		})
	}

	private fun explain(txt: List<String>) {
		var cancelled = false
		val translationLayout = inflate(this, R.layout.translation_dialog, null)
		val scrollview = translationLayout.findViewById<ScrollView>(R.id.scrollview)
		val container = translationLayout.findViewById<LinearLayout>(R.id.container)
		val errorMsg = translationLayout.findViewById<TextView>(R.id.errorMsg)
		val loading = translationLayout.findViewById<View>(R.id.loading)

		queryWordDefinition(txt, success = { result ->
			if (!cancelled) {
				if (result.isNotEmpty()) {
					for (dict in result) {
						val view = createTranslationItem(dict)
						container.addView(view)
					}
				} else {
					val view = TextView(this)
					view.setTextColor(resources.getColor(R.color.colorWhite, null))
					view.setPadding(20, 40, 20, 40)
					view.textAlignment = TEXT_ALIGNMENT_CENTER
					view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
					view.text = getString(R.string.no_explanation_found)
					container.addView(view)
				}
				loading.visibility = GONE
				scrollview.visibility = VISIBLE
			}
		}, error =  { msg ->
			if (cancelled) {
				errorMsg.text = msg
				loading.visibility = GONE
				errorMsg.visibility = VISIBLE
			}
		})

		val dialog = AlertDialog.Builder(this, R.style.dialogStyle)
			.setView(translationLayout)
			.setCancelable(true)
			.create()
		dialog.setOnCancelListener {
			cancelled = true
		}
		dialog.show()
	}

}
