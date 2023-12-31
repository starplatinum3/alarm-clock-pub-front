package com.github.thorqin.reader.activities.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.Window
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.MenuCompat
import com.github.thorqin.reader.App
import cn.chenjianlink.android.alarmclock.R
import cn.chenjianlink.android.alarmclock.databinding.ActivityMainBinding
import cn.chenjianlink.android.alarmclock.databinding.ActivityMainBookBinding
import com.github.thorqin.reader.activities.book.BookActivity
import com.github.thorqin.reader.activities.community.CommunityActivity
import com.github.thorqin.reader.activities.wifi.UploadActivity
//import cn.chenjianlink.android.alarmclock.databinding.ActivityMainBinding


const val REQUEST_SEARCH_BOOK = 1
const val REQUEST_OPEN_BOOK = 2
const val REQUEST_OPEN_UPLOAD = 3
const val REQUEST_CHECK_UPDATE = 4
const val REQUEST_OPEN_COMMUNITY = 5

class MyFileProvider : FileProvider()

class MainActivity : AppCompatActivity() {

	// private val app: App
	// 	get() {
	// 		return application as App
	// 	}

	private lateinit var handler: Handler

	private lateinit var updateManager: UpdateManager

	private lateinit var bookManager: BookManager

//	lateinit var binding: ActivityMainBinding
	lateinit var binding: ActivityMainBookBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBookBinding.inflate(layoutInflater)
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		setContentView(binding.root)
		setSupportActionBar(binding.toolbar)

		handler = Handler(Looper.getMainLooper())
		updateManager = UpdateManager(this)
		bookManager = BookManager(this)

		val openView = intent.getStringExtra("openView")
		if (openView == "community") {
			showCommunity()
		} else {
			// if (app.config.lastRead != null && app.config.files.containsKey(app.config.lastRead!!)) {
			// 	val intent = Intent(this, BookActivity::class.java)
			// 	intent.putExtra("key", app.config.lastRead)
			// 	startActivityForResult(intent, REQUEST_OPEN_BOOK)
			// }
		}

		binding.searchButton.setOnClickListener {
			bookManager.searchBooks()
		}

 		// bookManager.showFiles()
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		val op = intent?.getStringExtra("op")
		if (op == "searchLocal") {
			bookManager.searchBooks()
		} else {
			val openView = intent?.getStringExtra("openView")
			if (openView == "community") {
				showCommunity()
			}
		}
	}

	override fun onDestroy() {
		updateManager.destroy()
		super.onDestroy()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		val inflater = menuInflater
		inflater.inflate(R.menu.activity_main, menu)
		MenuCompat.setGroupDividerEnabled(menu, true)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.search_book -> {
				bookManager.searchBooks()
				true
			}
			R.id.empty_book -> {
				bookManager.clearBooks()
				true
			}
			R.id.wifi_upload -> {
				showUpload()
				true
			}
			R.id.about -> {
				showAbout()
				true
			}

			R.id.interchange -> {
				showCommunity()
				true
			}
			R.id.check_update -> {
				updateManager.checkUpdate()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}


	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
				val b = shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				if (!b && requestCode == REQUEST_SEARCH_BOOK) {
					bookManager.showOpenAppSetting()
				}
			} else {
				if (requestCode == REQUEST_SEARCH_BOOK) {
					bookManager.searchBooks()
				} else if (requestCode == REQUEST_CHECK_UPDATE) {
					updateManager.checkUpdate()
				}
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when (requestCode) {
			REQUEST_SEARCH_BOOK -> // SETTING MAYBE CHANGED
				bookManager.searchBooks()
			REQUEST_OPEN_BOOK -> { // BOOK ACTIVITY CLOSED
				bookManager.showFiles()
				// app.config.lastRead = null
				// app.saveConfig()
			}
			REQUEST_OPEN_UPLOAD, REQUEST_OPEN_COMMUNITY -> bookManager.showFiles()
			REQUEST_CHECK_UPDATE -> updateManager.checkUpdate()
		}
	}

	private fun showAbout() {
		val pkgInfo = packageManager.getPackageInfo(packageName, 0)
		val versionName = pkgInfo.versionName

		AlertDialog.Builder(this, R.style.dialogStyle).setTitle(getString(R.string.app_name))
			.setMessage(String.format(getString(R.string.about_me), versionName))
			.setCancelable(true).show()
	}

	private fun showUpload() {
		val intent = Intent(this, UploadActivity::class.java)
		startActivityForResult(intent, REQUEST_OPEN_UPLOAD)
	}

	private fun showCommunity() {
		val intent = Intent(this, CommunityActivity::class.java)
		startActivityForResult(intent, REQUEST_OPEN_COMMUNITY)
	}

}
