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

package cn.chenjianlink.android.alarmclock.db

import cn.chenjianlink.android.alarmclock.dao.LogInfoDao
import cn.chenjianlink.android.alarmclock.model.LogInfo
//import com.example.jetcaster.data.room.CategoriesDao
//import com.example.jetcaster.data.room.EpisodesDao
//import com.example.jetcaster.data.room.PodcastCategoryEntryDao
//import com.example.jetcaster.data.room.PodcastsDao
import com.starp.roomUtil.RoomUtil
import kotlinx.coroutines.flow.Flow
import javax.security.auth.login.LoginException

/**
 * A data repository for [Category] instances.
 */
class LogInfoStore(
    val   logInfoDao: LogInfoDao,
    //private val categoriesDao: CategoriesDao,
    //private val categoryEntryDao: PodcastCategoryEntryDao,
    //private val episodesDao: EpisodesDao,
    //private val podcastsDao: PodcastsDao
) {


    /**
     * Returns a flow containing a list of categories which is sorted by the number
     * of podcasts in each category.
     */
    //fun select(
    //   logInfo: LogInfo
    //): Flow<List<LogInfo>> {
    //    return arrayListOf<LogInfo>()
    //    //logInfoDao.
    //    //val selectFlow = RoomUtil.selectFlow(logInfoDao, logInfo)
    //    //
    //    //return  RoomUtil.selectFlow(logInfoDao, logInfo)
    //    //return categoriesDao.categoriesSortedByPodcastCount(limit)
    //}

    /**
     * Returns a flow containing a list of podcasts in the category with the given [categoryId],
     * sorted by the their last episode date.
     */
    //fun podcastsInCategorySortedByPodcastCount(
    //    categoryId: Long,
    //    limit: Int = Int.MAX_VALUE
    //): Flow<List<PodcastWithExtraInfo>> {
    //    return podcastsDao.podcastsInCategorySortedByLastEpisode(categoryId, limit)
    //}
    //
    ///**
    // * Returns a flow containing a list of episodes from podcasts in the category with the
    // * given [categoryId], sorted by the their last episode date.
    // */
    //fun episodesFromPodcastsInCategory(
    //    categoryId: Long,
    //    limit: Int = Integer.MAX_VALUE
    //): Flow<List<EpisodeToPodcast>> {
    //    return episodesDao.episodesFromPodcastsInCategory(categoryId, limit)
    //}
    //
    ///**
    // * Adds the category to the database if it doesn't already exist.
    // *
    // * @return the id of the newly inserted/existing category
    // * 如果类别不存在，则将其添加到数据库中。
    // *
    // *@返回新插入/现有类别的id
    // */
    //suspend fun addCategory(category: Category): Long {
    //    return when (val local = categoriesDao.getCategoryWithName(category.name)) {
    //        null -> categoriesDao.insert(category)
    //        else -> local.id
    //    }
    //}
    //
    //suspend fun addPodcastToCategory(podcastUri: String, categoryId: Long) {
    //    categoryEntryDao.insert(
    //        PodcastCategoryEntry(podcastUri = podcastUri, categoryId = categoryId)
    //    )
    //}
}
