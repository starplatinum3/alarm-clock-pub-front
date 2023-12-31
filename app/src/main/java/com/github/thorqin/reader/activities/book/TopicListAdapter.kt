package com.github.thorqin.reader.activities.book

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.github.thorqin.reader.App
import cn.chenjianlink.android.alarmclock.R

class TopicListAdapter(private val context: Context, private var chapters: ArrayList<App.Chapter>): BaseAdapter() {
	var readChapter: Int = 0
		set(value) {
			field = value
			this.notifyDataSetChanged()
		}


	var onSelectTopic: ((chapterIndex: Int) -> Unit)? = null
	private val inflater: LayoutInflater = LayoutInflater.from(context)

	@SuppressLint("InflateParams")
	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		val item = chapters[position]
		val view: TextView = if (convertView != null) {
			if (convertView.tag == position) {
				if (position == readChapter) {
					convertView.setBackgroundColor(context.getColor(R.color.colorAccent))
				} else {
					convertView.setBackgroundColor(context.getColor(R.color.colorWorkspace))
				}
				return convertView
			} else {
				convertView as TextView
			}
		} else {
			inflater.inflate(R.layout.topic_list_item, null) as TextView
		}

		if (position == readChapter) {
			view.setBackgroundColor(context.getColor(R.color.colorAccent))
		} else {
			view.setBackgroundColor(context.getColor(R.color.colorWorkspace))
		}
		view.tag = position
		view.text = item.name
		view.setOnClickListener {
			onSelectTopic?.invoke(it.tag as Int)
		}

		return view
	}

	override fun getItem(position: Int): Any {
		return chapters[position]
	}

	override fun getItemId(position: Int): Long {
		return position.toLong()
	}

	override fun getCount(): Int {
		return chapters.size
	}

}
