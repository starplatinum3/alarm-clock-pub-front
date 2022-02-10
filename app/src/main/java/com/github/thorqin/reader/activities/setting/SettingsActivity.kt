package com.github.thorqin.reader.activities.setting

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.github.thorqin.reader.App
import cn.chenjianlink.android.alarmclock.R
import cn.chenjianlink.android.alarmclock.databinding.SettingsActivityBinding

class SettingsActivity : AppCompatActivity() {

	private val app: App
		get() {
			return application as App
		}

	private lateinit var binding: SettingsActivityBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = SettingsActivityBinding.inflate(layoutInflater)
		setContentView(binding.root)

		setSupportActionBar(binding.toolbar)
		supportActionBar?.title = getString(R.string.settings)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		// Click to flip
		binding.clickToFlipSwitch.isChecked = app.config.clickToFlip
		binding.clickToFlipItem.setOnClickListener {
			binding.clickToFlipSwitch.toggle()
			app.config.clickToFlip = binding.clickToFlipSwitch.isChecked
			app.saveConfig()
		}
		binding.clickToFlipSwitch.setOnClickListener {
			app.config.clickToFlip = binding.clickToFlipSwitch.isChecked
			app.saveConfig()
		}

		// Never lock screen
		binding.screenLockSwitch.isChecked = app.config.neverLock
		binding.screenLockItem.setOnClickListener {
			binding.screenLockSwitch.toggle()
			app.config.neverLock = binding.screenLockSwitch.isChecked
			app.saveConfig()
		}
		binding.screenLockSwitch.setOnClickListener {
			app.config.neverLock = binding.screenLockSwitch.isChecked
			app.saveConfig()
		}

		// Volume Button to Flip
		binding.volumeToFlipSwitch.isChecked = app.config.volumeFlip
		binding.volumeToFlip.setOnClickListener {
			binding.volumeToFlipSwitch.toggle()
			app.config.volumeFlip = binding.volumeToFlipSwitch.isChecked
			app.saveConfig()
		}
		binding.volumeToFlipSwitch.setOnClickListener {
			app.config.volumeFlip = binding.volumeToFlipSwitch.isChecked
			app.saveConfig()
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

}
