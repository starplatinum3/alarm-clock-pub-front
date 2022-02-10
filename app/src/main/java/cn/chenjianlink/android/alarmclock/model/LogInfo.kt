package cn.chenjianlink.android.alarmclock.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//kotlin builder
@Entity
//@BuilderInference
data class LogInfo(
    //kotlin Integer
    @PrimaryKey(autoGenerate = true)
    //Integer id:Integer,
    //val id: Int?,
    var id: Int?,
){

    companion object{
        //tableName:String="LogInfo"
        const val tableName="LogInfo"
    }

//int id;
//是不能写 private吗、、

    //val date: Date?=null

    //kotlin set
    //val title: String?=null
    //val content: String?=null;
    ////int  isVerificationCode;
    //val verificationCode: Boolean?=null;

    var date: Date?=null
    var title: String?=null
    var content: String?=null;
    //int  isVerificationCode;
    var verificationCode: Boolean?=null;
    var key: String?=null;
    //@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    //@ColumnInfo(name = "name") val name: String
//java 调用 kotlin data class
}



