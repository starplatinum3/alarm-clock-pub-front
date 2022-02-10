package com.github.thorqin.reader.activities.community

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.thorqin.reader.App
import cn.chenjianlink.android.alarmclock.R
import cn.chenjianlink.android.alarmclock.databinding.ActivityCommunityBinding
import com.github.thorqin.reader.activities.main.MainActivity
import com.github.thorqin.reader.activities.main.REQUEST_OPEN_UPLOAD
import com.github.thorqin.reader.activities.wifi.UploadActivity
//import cn.chenjianlink.android.alarmclock.databinding.ActivityCommunityBinding
import com.github.thorqin.reader.utils.json
//import kotlinx.android.synthetic.main.activity_community.*
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.roundToInt


private class UploadResult(var score: Int, var message: String)

private class UploadBookInfo(var name: String, var author: String, var desc: String, var hash: String, var key: String)

private class DownloadResult(var ok: Boolean, var message: String)

private class DownloadBookInfo(var id: Long, var name: String, var author: String, var desc: String, var hash: String, var size: Long)

private class ProgressDialog(val bar: ProgressBar, val info: TextView, val dialog: AlertDialog)

class CommunityActivity : AppCompatActivity() {

	private val app: App
		get() {
			return application as App
		}

	private lateinit var handler: Handler
	private lateinit var mainPage: String

	private lateinit var progressDialog: ProgressDialog

	private lateinit var binding: ActivityCommunityBinding
//
//	val handler = Handler(Looper.getMainLooper())

	companion object {

		val fileMap = hashMapOf(
			"js" to "application/javascript",
			"css" to "text/css",
			"htm" to "text/html",
			"html" to "text/html",
			"png" to "image/png",
			"jpg" to "image/jpeg",
			"jpeg" to "image/jpeg",
			"svg" to "image/svg+xml",
			"json" to "application/json"
		)

		fun getMimeByName(name: String): String {
			val dot = name.lastIndexOf(".")
			return if (dot < 0) {
				"application/octet-stream"
			} else {
				fileMap[name.substring(dot + 1)] ?: "application/octet-stream"
			}
		}
	}

	@SuppressLint("SetJavaScriptEnabled")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		handler = Handler(Looper.getMainLooper())
		binding = ActivityCommunityBinding.inflate(layoutInflater)

		val useDebug = app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE > 0
		WebView.setWebContentsDebuggingEnabled(useDebug)

		setContentView(binding.root)

		val cookieManager = CookieManager.getInstance()
		cookieManager.setAcceptCookie(true)

		//binding.webView.clearCache(true)
		binding.webView.visibility = View.GONE
		binding.webView.settings.allowFileAccess = true
		binding.webView.settings.allowContentAccess = true
		binding.webView.settings.allowFileAccessFromFileURLs = true
		binding.webView.settings.javaScriptEnabled = true
		binding.webView.settings.setSupportZoom(false)
		binding.webView.settings.defaultTextEncodingName = "utf-8"
		binding.webView.settings.domStorageEnabled = true
		binding.webView.settings.databaseEnabled = true
		binding.webView.settings.setAppCacheEnabled(true)
		binding.webView.settings.loadsImagesAutomatically = true
		binding.webView.setBackgroundColor(getColor(R.color.colorBlack))

		progressDialog = createLoadingDialog()

		binding.webView.addJavascriptInterface(object {

			@JavascriptInterface
			fun showToast(message: String) {
				App.toast(this@CommunityActivity, message)
			}

			@JavascriptInterface
			fun msgbox(message: String) {
				App.msgbox(this@CommunityActivity, message, this@CommunityActivity.getString(R.string.app_name))
			}

			@JavascriptInterface
			fun askbox(message: String, callbackKey: String) {
				App.askbox(this@CommunityActivity, message, this@CommunityActivity.getString(R.string.app_name), {
					runOnUiThread {
						binding.webView.evaluateJavascript("eReaderClient.invokeCallback('$callbackKey', true)", null)
					}
				}, {
					runOnUiThread {
						binding.webView.evaluateJavascript("eReaderClient.invokeCallback('$callbackKey', false)", null)
					}
				})
			}

			@JavascriptInterface
			fun searchLocal() {
				val bundle = Bundle()
				bundle.putString("op", "searchLocal")
				val intent = Intent(this@CommunityActivity, MainActivity::class.java)
				intent.putExtras(bundle)
				startActivity(intent)
			}

			@JavascriptInterface
			fun wifiUpload(callbackKey: String?) {
				val intent = Intent(this@CommunityActivity, UploadActivity::class.java)
				intent.putExtra("callbackKey", callbackKey)
				startActivityForResult(intent, REQUEST_OPEN_UPLOAD)
			}

			@JavascriptInterface
			fun showFiles(callbackKey: String?) {
				thread(start = true, isDaemon = true, block = {
//					Thread.sleep(200)
					val list = app.config.getList()
					val data = json().toJson(list)
					runOnUiThread {
						// println(data)
						binding.webView.evaluateJavascript("eReaderClient.invokeCallback('$callbackKey', $data)", null)
					}
				})
			}

			@JavascriptInterface
			fun getBookDesc(key:String, callbackKey: String?) {
				thread(start = true, isDaemon = true, block = {
					val detail = app.getFileConfig(key)
					val desc = detail.getDescription()
					val data = json().toJson(desc)
					runOnUiThread {
						// println(data)
						binding.webView.evaluateJavascript("eReaderClient.invokeCallback('$callbackKey', $data)", null)
					}
				})
			}

			@JavascriptInterface
			fun uploadBook(bookInfo: String, callbackKey: String?) {
				thread(start = true, isDaemon = true, block = {
					var data: String = try {

						runOnUiThread {
							showProgress()
							updateProgress(0f)
						}

						val info = json().fromJson(bookInfo, UploadBookInfo::class.java)
						val detail = app.getFileConfig(info.key)

						var uploadURL = mainPage
						if (!uploadURL.endsWith("/")) {
							uploadURL += "/"
						}
						uploadURL += "api/book/upload"
						val cookie = CookieManager.getInstance().getCookie(mainPage)
						val boundary = "ERBoundary" + UUID.randomUUID().toString().replace(Regex("-"), "").toLowerCase()
						val conn = URL(uploadURL).openConnection() as HttpURLConnection
						conn.connectTimeout = 15000
						conn.readTimeout = 15000
						conn.doOutput = true
						conn.doInput = true
						conn.useCaches = false
						conn.setChunkedStreamingMode(4096)
						conn.requestMethod = "POST"
						conn.setRequestProperty("ENCTYPE", "multipart/form-data")
						conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
						conn.setRequestProperty("Cookie", cookie)
						// println("Cookie: $cookie")
						val os = conn.outputStream
						os.write("--$boundary\r\n".toByteArray())
						os.write("Content-Disposition: form-data; name=file; filename=\"${info.name}-${info.author}.txt\"\r\n\r\n".toByteArray())
						val file = File(detail.path)
						val fileLength = file.length()
						var sum = 0
						val buffer = ByteArray(4096)
						file.inputStream().use {
							var size = it.read(buffer)
							while(size > 0) {

								os.write(buffer, 0, size)
								os.flush()

								sum += size
								val prog = sum / fileLength.toFloat()
								runOnUiThread {
									updateProgress(prog, "上传中")
								}

								size = it.read(buffer)
							}
						}

						os.write("\r\n".toByteArray())

						os.write("--$boundary\r\n".toByteArray())
						os.write("Content-Disposition: form-data; name=info\r\n".toByteArray())
						os.write("Content-Type: application/json; charset=utf-8\r\n\r\n".toByteArray())
						os.write(bookInfo.toByteArray())
						os.write("\r\n--$boundary--\r\n".toByteArray())

						if (conn.responseCode == 200) {
							conn.inputStream.use { inputStream ->
								InputStreamReader(inputStream, "utf-8").use {
									it.readText()
								}
							}
						} else {
							json().toJson(UploadResult(0, conn.responseMessage))
						}
					} catch(e: Throwable) {
						json().toJson(UploadResult(0, e.message ?: "未知错误！"))
					} finally {
						runOnUiThread {
							hideProgress()
							updateProgress(0f)
						}
					}
					runOnUiThread {
						// println(data)
						binding.webView.evaluateJavascript("eReaderClient.invokeCallback('$callbackKey', $data)", null)
					}
				})
			}

			private fun ensureRootDir(bookRoot: File): Boolean {
				return if (bookRoot.exists()) {
					if (bookRoot.isFile) {
						bookRoot.delete()
						bookRoot.mkdirs()
					} else {
						true
					}
				} else {
					bookRoot.mkdirs()
				}
			}

			@JavascriptInterface
			fun downloadBook(downloadBookInfo: String, callbackKey: String) {
				thread(start = true, isDaemon = true, block = {
					val info = json().fromJson<DownloadBookInfo>(downloadBookInfo, DownloadBookInfo::class.java)
					val data = if (app.config.files.entries.filter {
						it.value.hash == info.hash
					}.count() > 0) {
						json().toJson(DownloadResult(false, "图书已在书架中！"))
					} else {
						val bookRoot = app.getExternalBookRootDir()
						if (ensureRootDir(bookRoot)) {
							val file = bookRoot.resolve("${info.name} - ${info.author}.txt")
							if (file.isFile) {
								json().toJson(DownloadResult(false, "图书已在书架中！"))
							} else {
								runOnUiThread {
									showProgress()
									updateProgress(0f)
								}

								try {
									var downloadURL = mainPage
									if (!downloadURL.endsWith("/")) {
										downloadURL += "/"
									}
									downloadURL += "api/book/download?id=${info.id}"
									val cookie = CookieManager.getInstance().getCookie(mainPage)
									val conn = URL(downloadURL).openConnection() as HttpURLConnection
									conn.connectTimeout = 15000
									conn.readTimeout = 15000
									conn.doOutput = false
									conn.doInput = true
									conn.useCaches = false
									conn.setRequestProperty("Cookie", cookie)
									if (conn.responseCode == 200) {
										val buffer = ByteArray(4096)
										var sum = 0
										file.outputStream().use {outputStream ->
											conn.inputStream.use { inputStream ->
												var size = inputStream.read(buffer, 0, 4096)
												while (size > 0) {
													outputStream.write(buffer, 0, size)
													size = inputStream.read(buffer, 0, 4096)
													sum += size;
													val prog = sum / info.size.toFloat()
													runOnUiThread {
														updateProgress(prog, "下载中")
													}
												}
											}
										}
										val key = App.digest(file.absolutePath)
										app.removeBook(key)
										val fc = App.FileSummary()
										fc.key = key
										// hash 差不多的意思
										fc.path = file.absolutePath
										fc.name = file.nameWithoutExtension
										fc.totalLength = file.length()
										app.config.files[key] = fc
										app.saveConfig()
										json().toJson(DownloadResult(true, "下载成功！"))
									} else {
										json().toJson(DownloadResult(false, conn.responseMessage))
									}
								} catch (e: Throwable) {
									if (file.isFile) {
										file.delete()
									}
									json().toJson(DownloadResult(false, e.message ?: "下载失败！"))
								} finally {
									runOnUiThread {
										hideProgress()
										updateProgress(0f)
									}
								}
							}
						} else {
							json().toJson(DownloadResult(false, "创建下载目录失败！"))
						}
					}

					runOnUiThread {
						// println(data)
						binding.webView.evaluateJavascript("eReaderClient.invokeCallback('$callbackKey', $data)", null)
					}
				})
			}

			@JavascriptInterface
			fun close() {
				finish()
			}
		}, "eReader")

		binding.webView.webChromeClient = object: WebChromeClient() {
//			override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
//				return super.onConsoleMessage(consoleMessage)
//			}
		}
		binding.webView.webViewClient = object: WebViewClient() {
			override fun onPageFinished(view: WebView?, url: String?) {
				super.onPageFinished(view, url)

				handler.postDelayed({
					binding.webView.visibility = View.VISIBLE
				}, 200)
			}

			override fun onReceivedError(
				view: WebView?,
				request: WebResourceRequest?,
				error: WebResourceError?
			) {
				super.onReceivedError(view, request, error)
				if (request?.url.toString() == mainPage) {
					binding.webView.loadUrl("res://error.html")
//					webView.evaluateJavascript(showError) {}
				}
			}

			override fun shouldInterceptRequest(
				view: WebView?,
				request: WebResourceRequest?
			): WebResourceResponse? {
				println("Request: ${request?.url}")
				return if (request?.url?.scheme == "res") {
					val filename = request.url.toString().substring(6)
					try {
						val inputStream = resources.assets.open(filename)
						val contentType = getMimeByName(filename)
						WebResourceResponse(contentType, "utf-8", inputStream)
					} catch (e: Exception){
						WebResourceResponse("text/plain", "utf-8", 404, "Not Found", null, null)
					}
				} else {
					super.shouldInterceptRequest(view, request)
				}
			}
		}

		app.getWebSiteURL({
			mainPage = it
			runOnUiThread {
				binding.webView.loadUrl(it)
			}
		}, {
			runOnUiThread {
				binding.webView.loadUrl("res://error.html")
				App.toast(this, "网络图书暂不可用!")
			}
		})
	}


	private fun showProgress() {
		progressDialog.dialog.show()
	}

	private fun hideProgress() {
		progressDialog.dialog.hide()
	}

	private fun updateProgress(value: Float, message: String = "处理中") {
		val v = (value * 100).roundToInt().coerceAtLeast(0).coerceAtMost(100)
		progressDialog.bar.progress = v
		progressDialog.info.text = "$message：$v%"
	}

	private fun createLoadingDialog(): ProgressDialog {
		val uploading = View.inflate(this, R.layout.progress, null)
		val bar = uploading.findViewById<ProgressBar>(R.id.progressBar)
		val info = uploading.findViewById<TextView>(R.id.progressInfo)
		val dialog = AlertDialog.Builder(this, R.style.dialogStyle)
			.setView(uploading)
			.setCancelable(false)
			.create()
		return ProgressDialog(bar, info, dialog)
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
		if (keyCode == KEYCODE_BACK) {
			binding.webView.evaluateJavascript("eReaderClient.popWindow()") {
				if (it == "false" || it == "null") {
					this.finish()
				}
			}

			return true
		} else {
			return super.onKeyDown(keyCode, event)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when (requestCode) {
			REQUEST_OPEN_UPLOAD -> {
				val callbackKey = data?.getStringExtra("callbackKey")
				if (callbackKey != null) {
					binding.webView.evaluateJavascript("eReaderClient.invokeCallback('$callbackKey')") {}
				}
			}
		}
	}
}
