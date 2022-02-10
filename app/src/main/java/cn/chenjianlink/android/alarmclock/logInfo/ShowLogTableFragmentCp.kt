package cn.chenjianlink.android.alarmclock.logInfo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.chenjianlink.android.alarmclock.db.AppDatabase
import cn.chenjianlink.android.alarmclock.model.LogInfo
import cn.chenjianlink.android.alarmclock.utils.DateUtil
import java.util.*

// import java.lang.reflect.Modifier

// import androidx.room.util.TableInfo
// import org.w3c.dom.Text

//@Composable
//fun Discover(
//    navigateToPlayer: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val viewModel: LogInfoViewModel = viewModel()
//    //val viewModel: DiscoverViewModel = viewModel()
//    val viewState by viewModel.state.collectAsState()
//
//    val selectedCategory = viewState.selectedCategory
//
//    if (viewState.categories.isNotEmpty() && selectedCategory != null) {
//        Column(modifier) {
//            Spacer(Modifier.height(8.dp))
//
//            PodcastCategoryTabs(
//                categories = viewState.categories,
//                selectedCategory = selectedCategory,
//                onCategorySelected = viewModel::onCategorySelected,
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(Modifier.height(8.dp))
//
//            Crossfade(
//                targetState = selectedCategory,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//            ) { category ->
//                /**
//                 * TODO, need to think about how this will scroll within the outer VerticalScroller
//                 */
//                /**
//                 * TODO, need to think about how this will scroll within the outer VerticalScroller
//                 */
//                PodcastCategory(
//                    categoryId = category.id,
//                    navigateToPlayer = navigateToPlayer,
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//        }
//    }
//    // TODO: empty state
//}


// class ShowLogTableFragmentCp {
// }
// https://stackoverflow.com/questions/59368360/how-to-use-compose-inside-fragment
class ShowLogTableFragmentCp : Fragment() {
    val database = AppDatabase.getDatabase(context)
    val logInfoDao = database.logInfoDao()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val viewModel: LogInfoViewModel = viewModel()
        // kotlin 调用 java的类
        //val logInfo = LogInfo(1)
        val logInfo = LogInfo(null)
        // val logInfo = LogInfo(1, Date(),"1","1",true)
        //logInfo.id=1
        // // logInfo.id
        logInfo.content="1"
        logInfo.title="1"
        // logInfoDao.se
        // val queryAll = logInfoDao.queryAll()
        // queryAll.last()
        val logInfos=  logInfoDao.queryAll().asLiveData()
        // logInfoDao.queryAll().asLiveData()
        // asLiveData  observe
//观察数据变化
//        logInfos.v
        logInfos.observe(viewLifecycleOwner, {
            //val viewModel: LogInfoViewModel = viewModel()
            var logInfo = ""
            for (logInfoOne in it) {
                logInfo += "${logInfoOne.title} - ${logInfoOne.content} \n"
            }
            Log.e("存储的数据", logInfo)
        })


//在线程中操作数据库
//         Thread {
//             logInfoDao.queryAll()
//             solarTermDao.insert(entityYuShui)
//         }.start()

        // ————————————————
        // 版权声明：本文为CSDN博主「乐翁龙」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
        // 原文链接：https://blog.csdn.net/u010976213/article/details/117325771
        // val select = RoomUtil.select(logInfoDao, LogInfo.builder().build())
        // database = AppDatabase.getDatabase(context)
        //查询所有数据并转换为LiveData类型
        // val terms = solarTermDao.queryAll().asLiveData()

        // https://blog.csdn.net/u010976213/article/details/117325771

        // val entityYuShui = SolarTerm(
        //     name = "雨水",
        //     content = "正月中，天一生水，春始属木，然生木者必水也，故立春后继之雨水。且东风既解冻，则散而为雨矣。",
        //     time = "2.18-2.19",
        //     mark = "春雨贵如油",
        //     imgUrl = "https://cdn.dribbble.com/users/2676519/screenshots/7056971/media/efde75ba876f38cb95ce5b936f481784.jpg?compress=1&resize=1000x750"
        // )
        //
        // val entityChunFen = SolarTerm(
        //     name = "春分",
        //     content = "春分者，阴阳相半也。故昼夜均而寒暑平。斗指壬为春分，约行周天，南北两半球昼夜均分，又当春之半，故名为春分。",
        //     time = "3.20-3.21",
        //     mark = "春分有雨到清明，清明下雨无路行",
        //     imgUrl = "https://cdn.dribbble.com/users/2676519/screenshots/7056971/media/cdcf98372abf88a554a53362c6037f2a.jpg?compress=1&resize=1000x750"
        // )

        // val list = arrayListOf(entityYuShui, entityChunFen)
        val list = arrayListOf(logInfo)
        // ————————————————
        // 版权声明：本文为CSDN博主「乐翁龙」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
        // 原文链接：https://blog.csdn.net/u010976213/article/details/117325771
        //
        return ComposeView(requireContext()).apply {

            setContent {
                val viewModel: LogInfoViewModel = viewModel()
                val viewState by viewModel.state.collectAsState()
                // Text(text = "Hello world.")
                // https://blog.csdn.net/sinat_38184748/article/details/119355727
                LazyColumn {
                    // 显示列表

                    //items(list.size) { index ->
                    items(viewState.logInfos.size) { index ->
                    //items(logInfos.size) { index ->
                        //val info = list[index]
                        val info = viewState.logInfos[index]
                        //val info =logInfos[index]
                        // 列表单项的UI
                        // Text(text = info.id)
                        // info.
                        //kotlin 调用java 代码说是不能为null
                         val dateToString = DateUtil.dateToString(info.date, DateUtil.FORMAT_ONE)
                        info.title?.let { Text(text = it) }
                        info.content?.let { Text(text = it) }
                        info.key?.let { Text(text = it) }
                        //我们在问 key 有没有 有的话 key就是it 就是放上去
                        Text(text = dateToString)
                        //Text 值是null  compose
                        //Spacer(modifier = 4.dp)
                        Spacer(modifier =  Modifier.size(10.dp))
                        //Spacer compose
                    }
                    // items(count = list.size,
                    //     // key=list.
                    // itemContent: @Composable LazyItemScope.(index: Int) -> Unit
                    // ) {
                    //     // SolarTermsItem(entity = it)
                    //     // Text(text = it.)
                    //     LogInfoItem(entity = it)
                    // }
                }
            }
        }
    }
}
// @Preview
@ExperimentalAnimationApi
@Composable
fun LogInfoItem(entity: LogInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        entity.content?.let {
            Text(
                text = it,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        // ————————————————
        // 版权声明：本文为CSDN博主「乐翁龙」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
        // 原文链接：https://blog.csdn.net/u010976213/article/details/117325771
    }
}

// Preview
// Composable
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    // ComposeTheme {
    //     Greeting("Android")
    // }
}

// LazyColumn {
//     items(list) {
//         SolarTermsItem(entity = it)
//     }
// }
//
// @Composable
// fun MessageList(messages: List<Message>) {
//     TableInfo.Column {
//         messages.forEach { message ->
//             MessageRow(message)
//         }
//     }
// }