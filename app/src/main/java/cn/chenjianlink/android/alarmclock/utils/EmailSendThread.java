package cn.chenjianlink.android.alarmclock.utils;

//import com.example.myapplication.R;

//import com.example.myapplication.model.EmailSender;

import androidx.room.Database;

import java.util.Arrays;
import java.util.Date;

import javax.mail.MessagingException;

import cn.chenjianlink.android.alarmclock.db.AppDatabase;
import cn.chenjianlink.android.alarmclock.model.EmailSender;
import cn.chenjianlink.android.alarmclock.model.LogInfo;
import cn.chenjianlink.android.alarmclock.repository.LogInfoRepository;
import lombok.Data;

//@Data
public class EmailSendThread extends Thread {

    private LogInfoRepository logInfoRepository;

    public EmailSendThread(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public void setDatabase(AppDatabase database) {
        this.database = database;
    }

    AppDatabase database;
    //Database
    public EmailSendThread() {
        logInfoRepository = new LogInfoRepository();
        logInfoRepository.setLogInfoDao(database.logInfoDao());
        //LogInfoRepository lo
    }
    String host = "smtp.163.com";
    String post = "25";
    String from = "starplatinum111@163.com";
    String title = "title" + "-";
    String content = "content";
    String[] receiver = new String[]{"starplatinum111@163.com"};
    String account = "starplatinum111@163.com";
    String password = "xxx";

//    https://blog.csdn.net/weixin_42247720/article/details/93710545
    @Override
    public void run() {
        try {
            EmailSender sender = new EmailSender();
            //设置服务器地址和端口，可以查询网络

            sender.setProperties(host, post);
//            android.R.string.search_go
//            Android 密码配置在 文件
//            R.id.decelerate
            //分别设置发件人，邮件标题和文本内容

            sender.setMessage(from, title, content);
            //设置收件人
            sender.setReceiver(receiver);
            //添加附件换成你手机里正确的路径
            // sender.addAttachment("/sdcard/emil/emil.txt");
            //发送邮件
            //sender.setMessage("你的163邮箱账号", "EmailS//ender", "Java Mail ！");这里面两个邮箱账号要一致
            sender.sendEmail(host, account, password);
        } catch (MessagingException e) {
            e.printStackTrace();
            //database.logInfoDao().
            LogInfo logInfo = new LogInfo(null);
            logInfo.setTitle("发送邮件失败");
            logInfo.setDate(new Date());
            logInfo.setContent(e.getMessage());
            //logInfo.set(e.getMessage());
            logInfoRepository.saveLog(logInfo);

        }
    }

    public static void main(String[] args) {
        EmailSendThread emailSendThread = new EmailSendThread();
        emailSendThread.start();
    }

    @Override
    public String toString() {
        return "EmailSendThread{" +
                "host='" + host + '\'' +
                ", post='" + post + '\'' +
                ", from='" + from + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", receiver=" + Arrays.toString(receiver) +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getReceiver() {
        return receiver;
    }

    public void setReceiver(String[] receiver) {
        this.receiver = receiver;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
