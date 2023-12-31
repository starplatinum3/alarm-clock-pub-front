package com.github.thorqin.reader.services

import android.app.*
import cn.chenjianlink.android.alarmclock.R
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.github.thorqin.reader.App
import java.util.*
import android.os.*
import android.content.Context
import android.media.AudioManager
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.github.thorqin.reader.activities.book.BookActivity
import android.media.MediaPlayer
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.view.KeyEvent


class TTSService : Service()  {

	companion object {
		const val MEDIA_SESSION_ACTIONS = PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE
		const val NOTIFICATIONS_ID = 1
		const val CHANNEL_ID = "TTS-SERVICE-CHANNEL"
		const val CHANNEL_NAME = "EReader TTS"
	}


	interface StateListener {
		fun onStop()
		fun onStart()
		fun onUpdate()
	}

	class TTSBinder(val service: TTSService): Binder()
	private val ttsBinder = TTSBinder(this)

	private lateinit var tts: TextToSpeech
	private var playing = false

	private var ttsAvailable = false
	private var playInfo: App.FileDetail? = null

	private var stateListener: StateListener? = null
	private var readingPos = 0L
	private var nextPos =0L

	private lateinit var handler: Handler
	private lateinit var mediaSession: MediaSessionCompat
	private lateinit var stateBuilder: PlaybackStateCompat.Builder

	private lateinit var phoneStateListener: PhoneStateListener

	private fun createMediaSession() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

			phoneStateListener = object: PhoneStateListener() {
				override fun onCallStateChanged(state: Int, phoneNumber: String?) {
					if (state == TelephonyManager.CALL_STATE_RINGING) {
						val audioManager =  getSystemService(Context.AUDIO_SERVICE) as AudioManager
						if (audioManager.getStreamVolume(AudioManager.STREAM_RING) > 0) {
							if (playing) {
								stop(true)
								stateListener?.onStop()
							}
						}
					}
				}
			}

			val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
		}
		stateBuilder = PlaybackStateCompat.Builder()
			.setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PLAY_PAUSE)
		mediaSession = MediaSessionCompat(this, "EReader").apply {
			setCallback(object: MediaSessionCompat.Callback() {
				override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {

					if (mediaButtonEvent == null) {
						return super.onMediaButtonEvent(mediaButtonEvent)
					}
					val keyEvent = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT) ?: return super.onMediaButtonEvent(mediaButtonEvent)

					if (keyEvent.action == KeyEvent.ACTION_UP) {
						if (keyEvent.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyEvent.keyCode == KeyEvent.KEYCODE_HEADSETHOOK) {
							if (playing) {
								stop(true)
								stateListener?.onStop()
							} else {
								play(true)
								stateListener?.onStart()
							}
							return true
						} else if (keyEvent.keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
							if (playing) {
								stop(true)
								stateListener?.onStop()
								return true
							}
						} else if (keyEvent.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
							if (!playing) {
								play(true)
								stateListener?.onStart()
								return true
							}
						}
					}
					// println("media button event: ${mediaButtonEvent.action}, KeyCode: ${keyEvent.keyCode}")
					return super.onMediaButtonEvent(mediaButtonEvent)
				}
			})
			setPlaybackState(stateBuilder.build())
			setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
			isActive = true
		}
	}

	private fun playSilentSound() {
		val player = MediaPlayer.create(this, R.raw.silent_sound)
		player.setOnCompletionListener { player.release() }
		player.start()
	}

	override fun onCreate() {
		super.onCreate()
		handler = Handler(Looper.getMainLooper())

		createMediaSession()

		tts = TextToSpeech(this) {status ->
			if (status == TextToSpeech.SUCCESS) {
				val result = tts.setLanguage(Locale.CHINA)
				if (result == TextToSpeech.LANG_COUNTRY_AVAILABLE || result == TextToSpeech.LANG_AVAILABLE){
					ttsAvailable = true
				}
			}
		}

		tts.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
			override fun onDone(p0: String?) {
//				println("done: $p0")
				readingPos = nextPos
				if (playInfo != null && p0 == "e-reader-sentence") {
					playInfo!!.setTtsPosition(readingPos)
					(application as App).saveFileState(playInfo!!, playInfo!!.key)
					stateListener?.onUpdate()
					ttsPlay()
				}
			}

			override fun onError(p0: String?) {
				System.err.println("error: $p0")
			}

			override fun onStart(p0: String?) {
				// println("start: $p0")
			}

		})


	}

	override fun onDestroy() {
		try {
			val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
			tts.shutdown()
			mediaSession.release()
		} catch (e: Throwable) {

		}
		super.onDestroy()
	}

	override fun onBind(intent: Intent): IBinder {
		return ttsBinder
	}



	private fun ttsPlay() {
		while (playInfo != null && playing) {
			val info = playInfo as App.FileDetail
			val sentence = info.getTtsSentence(readingPos)
			if (sentence.sentence != null) {
//				println(sentence.sentence)
				val str = sentence.sentence!!.replace(Regex("[\\s\\n\"“”]+"), " ").trim()
				if (str.isNotEmpty()) {
					nextPos = sentence.nextPos
					tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, "e-reader-sentence")
					break
				} else {
					readingPos = sentence.nextPos
				}
			} else {
				stop(true)
				stateListener?.onStop()
				break
			}
		}

	}

	fun setFileInfo(fileInfo: App.FileDetail) {
		playInfo = fileInfo
	}


	fun play(setup: Boolean = false): Boolean {
		if (ttsAvailable) {
			if (playing) {
				stop(setup)
			}
			val info = playInfo ?: return false
			playing = true
			readingPos = info.ttsPoint
			ttsPlay()

			if (setup) {
				playSilentSound()
				mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
					.setActions(MEDIA_SESSION_ACTIONS)
					.setState(PlaybackStateCompat.STATE_PLAYING, 0L, 1f).build())

				mediaSession.setMetadata(MediaMetadataCompat.Builder()
					.putText(MediaMetadataCompat.METADATA_KEY_TITLE, "Ereader TTS")
					.build())

				val notificationIntent = Intent(this, BookActivity::class.java)
				val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
				val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
				@Suppress("DEPRECATION")
				val builder = Notification.Builder(this)
					.setContentTitle(this.getString(R.string.app_name))
					.setContentText("正在使用语音播放图书...")
					.setSmallIcon(R.drawable.ic_book)
					.setContentIntent(pendingIntent)
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN)
					notificationChannel.enableLights(false)
					notificationChannel.setShowBadge(false)
					notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
					notificationManager.createNotificationChannel(notificationChannel)
					builder.setChannelId(CHANNEL_ID)
				}
				val notification = builder.build()
				notificationManager.notify(NOTIFICATIONS_ID, notification)
				startForeground(NOTIFICATIONS_ID, notification)
			}
		}

		return playing
	}

	fun stop(setup: Boolean = false) {
		if (ttsAvailable && playing) {
			tts.stop()
			playing = false
			if (setup) {
				mediaSession.setPlaybackState(
					PlaybackStateCompat.Builder()
						.setActions(MEDIA_SESSION_ACTIONS)
						.setState(PlaybackStateCompat.STATE_PAUSED, 0L, 1f).build()
				)
				stopForeground(false)
				val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
				notificationManager.cancel(NOTIFICATIONS_ID)
			}
		}
	}

	fun setListener(listener: StateListener) {
		stateListener = listener
	}

	val isAvailable: Boolean
		get () {
			return ttsAvailable
		}


}
