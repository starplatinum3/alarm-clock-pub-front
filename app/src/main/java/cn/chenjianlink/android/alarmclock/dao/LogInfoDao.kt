package cn.chenjianlink.android.alarmclock.dao

import androidx.room.Dao
import androidx.room.Query
import cn.chenjianlink.android.alarmclock.model.LogInfo
import com.starp.roomUtil.BaseDao
import kotlinx.coroutines.flow.Flow

// import java.util.concurrent.Flow

@Dao
public interface  LogInfoDao : BaseDao<LogInfo> {
    // LogInfo.
    /**
     * 查询所有的节气数据
     */
    // @Query("SELECT * FROM term")
    @Query("SELECT * FROM "+LogInfo.tableName)
    fun queryAll(): Flow<List<LogInfo>>
}