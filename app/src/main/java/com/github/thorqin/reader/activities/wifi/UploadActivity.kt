package com.github.thorqin.reader.activities.wifi

import android.content.BroadcastReceiver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import cn.chenjianlink.android.alarmclock.R
import android.net.wifi.WifiManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.content.IntentFilter
import com.github.thorqin.reader.App
import cn.chenjianlink.android.alarmclock.databinding.ActivityUploadBinding
import com.github.thorqin.reader.utils.json
import com.github.thorqin.reader.utils.readTextResource
import com.koushikdutta.async.AsyncServer
import com.koushikdutta.async.http.body.MultipartFormDataBody
import java.net.InetAddress
import java.nio.ByteOrder
import java.math.BigInteger
import com.koushikdutta.async.http.server.AsyncHttpServer
import java.io.File
import java.io.FileOutputStream
import java.net.URLDecoder

class UploadActivity : AppCompatActivity() {

	companion object {
		const val LISTEN_PORT = 8000
	}

	private val app: App
		get() {
			return application as App
		}

	private var running = false
	private var server = AsyncHttpServer()

	private lateinit var binding: ActivityUploadBinding

	private val wifiStateReceiver = object: BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {
			checkWIFIState()
		}

	}

	override fun onDestroy() {
		super.onDestroy()
		stopWebServer()
		try {
			this.unregisterReceiver(wifiStateReceiver)
		} catch (e: Throwable) {
			System.err.println(e)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityUploadBinding.inflate(layoutInflater)
		setContentView(binding.root)

		setSupportActionBar(binding.toolbar)
		supportActionBar?.title = getString(R.string.upload_book)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		init()
		val callbackKey = this.intent.getStringExtra("callbackKey")
		if (callbackKey != null) {
			val intent = Intent()
			intent.putExtra("callbackKey", callbackKey)
			setResult(0, intent)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			android.R.id.home -> {
				finish()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun checkWIFIState() {
		val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
		val networks = connManager.allNetworks
		for (n in networks) {
			@Suppress("DEPRECATION")
			val info = connManager.getNetworkInfo(n) ?: continue
			@Suppress("DEPRECATION")
			if (info.type == ConnectivityManager.TYPE_WIFI && info.isConnected) {
				startWebServer()
				return
			}
		}
		stopWebServer()
	}

	private fun startWebServer() {
		if (running) {
			return
		}
		running = true
		try {
			val wifiMgr = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
			val wifiInfo = wifiMgr.connectionInfo
			var ip = wifiInfo.ipAddress
			if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
				ip = Integer.reverseBytes(ip)
			}
			val ipByteArray = BigInteger.valueOf(ip.toLong()).toByteArray()
			val ipStr = InetAddress.getByAddress(ipByteArray).hostAddress
			server.listen(LISTEN_PORT)
			println("Web Server Started ...")
			binding.ipText.text = resources.getString(R.string.use_browser, ipStr, LISTEN_PORT)
		} catch (e: Exception) {
			stopWebServer()
		}
	}

	private val bookNamePattern = Regex(".+\\.(txt|epub)$", RegexOption.IGNORE_CASE)
	private fun init() {
		val bookRoot = app.getExternalBookRootDir()
		bookRoot.mkdirs()
		val self = this
		server.get("/") { _, response ->
			val text = readTextResource(self, R.raw.index)
			response.send(text)
		}

		server.post("/upload") { request, response ->
			var ct = request.headers.get("contentType")
			if (ct == null)
				ct = request.headers.get("content-type")
			if (Regex("^multipart/form-data;.*").matches(ct)) {
				val body = request.getBody<MultipartFormDataBody>()
				var fileStream : FileOutputStream? = null
				val uploadFileList = arrayListOf<File>()
				var errorMessage: String? = null
				body.setMultipartCallback { part ->
					if (part.isFile) {
						val filename = URLDecoder.decode(part.filename, "utf-8")
						if (bookNamePattern.matches(filename)) {
							println("Uploading file: $filename")
							val file = bookRoot.resolve(filename)
							fileStream?.close()
							try {
								fileStream = file.outputStream()
								uploadFileList.add(file)
							} catch (e: Exception) {
								errorMessage = e.message
								fileStream = null
							}
							body.setDataCallback { _, bufferList ->
								try {
									val bytes = bufferList.allByteArray
									println("data: ${bytes.size} ")
									fileStream?.write(bytes)
								} catch (e: Exception) {
									errorMessage = e.message
									System.err.println("Upload file error: ${e.message}")
								}
							}
						}
					}
				}
				request.setEndCallback {
					fileStream?.close()
					for (f in uploadFileList) {
						if (f.isFile) {
							val key = App.digest(f.absolutePath)
							app.removeBook(key)
							val fc = App.FileSummary()
							fc.key = key
							fc.path = f.absolutePath
							fc.name = f.nameWithoutExtension
							fc.totalLength = f.length()
							app.config.files[key] = fc
							// 从电脑界面下载书本
						}
					}
					app.saveConfig()
					println("upload end")
					if (errorMessage != null) {
						response.code(400)
						response.send("Upload failed: $errorMessage")
					} else {
						response.send("ok")
					}
				}
			} else {
				response.code(400)
				response.send("Bad Request!")
			}
		}

		server.get("/books") { _, response ->
			response.setContentType("application/json")
			val bookList = arrayListOf<App.FileSummary>()
			for (file in app.config.files.entries) {
				bookList.add(file.value)
			}
			val text = json().toJson(bookList)
			response.send(text)
		}

		val filter = IntentFilter()
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION)
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
		this.registerReceiver(wifiStateReceiver, filter)

		checkWIFIState()

	}

	private fun stopWebServer() {
		binding.ipText.text = getString(R.string.need_wifi)
		if (!running) {
			return
		}
		try {
			server.stop()
			AsyncServer.getDefault().stop()
		} catch (e: Exception) {
			System.err.println("Stop server error: ${e.message}")
		}
		running = false
		println("Web Server Stopped ...")
	}

}
