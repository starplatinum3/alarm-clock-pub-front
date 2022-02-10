/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.chenjianlink.android.alarmclock.logInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.chenjianlink.android.alarmclock.Graph
import cn.chenjianlink.android.alarmclock.dao.LogInfoDao
import cn.chenjianlink.android.alarmclock.db.AppDatabase
import cn.chenjianlink.android.alarmclock.model.LogInfo
import com.starp.roomUtil.RoomUtil
import kotlinx.coroutines.flow.*
//import com.example.jetcaster.Graph
//import com.example.jetcaster.data.Category
//import com.example.jetcaster.data.CategoryStore
import kotlinx.coroutines.launch

class LogInfoViewModel(
    private val logInfoDao: LogInfoDao = Graph.database.logInfoDao()
  //var AppDatabab
  //  AppDatada
) : ViewModel() {
    //AppDa
    //DateU
    // Holds our currently selected category
    //private val _selectedCategory = MutableStateFlow<Category?>(null)
//Muta
//    Mut
//    val _stara= MutableStateFlow()
//    val d=AppDatabase.getDatabase(cotext)
    //保存用户界面通过[状态]收集的视图状态
    // Holds our view state which the UI collects via [state]
    //private val _state = MutableStateFlow(DiscoverViewState())
    private val _state = MutableStateFlow(LogInfoViewState())

    //val state: StateFlow<DiscoverViewState>
    val state: StateFlow<LogInfoViewState>
        get() = _state

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            //结合每个流的最新值，使我们能够生成
//查看仅包含最新值的状态实例。
//            聚合两个流
//            logInfoDao.
//            RoomUtil.selectFlow(logInfoDao, LogInfo()).collect {
//                _state.value=  LogInfoViewState(logInfos=it)
//            }
            logInfoDao.queryAll().collect {
                _state.value=  LogInfoViewState(logInfos=it)
            }
            //combine(
            //    //logInfoDao.
            //    //logInfoDao.
            //    RoomUtil.selectFlow(logInfoDao, LogInfo()).onEach {  }
            //    logInfoDao.categoriesSortedByPodcastCount()
            //        .onEach { categories ->
            //            //如果我们还没有选定的类别，请选择第一个
            //            // If we haven't got a selected category yet, select the first
            //            if (categories.isNotEmpty() && _selectedCategory.value == null) {
            //                _selectedCategory.value = categories[0]
            //            }
            //        },
            //    _selectedCategory
            //) { categories, selectedCategory ->
            //    DiscoverViewState(
            //        categories = categories,
            //        selectedCategory = selectedCategory
            //    )
            //}.collect { _state.value = it }
            //循环选择  是循环吗 不是啊 返回的是一个 列表 it
        }
    }


    //fun onCategorySelected(category: Category) {
    //    _selectedCategory.value = category
    //}
}

//data class DiscoverViewState(
//    val categories: List<Category> = emptyList(),
//    val selectedCategory: Category? = null
//)

data class LogInfoViewState(
    //val categories: List<Category> = emptyList(),
    val logInfos: List<LogInfo> = emptyList(),
    val selectedLogInfo: LogInfo? = null
)