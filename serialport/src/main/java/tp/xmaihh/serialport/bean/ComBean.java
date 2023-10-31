package tp.xmaihh.serialport.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ComBean implements Parcelable {
    public byte[] bRec;//一个字节数组，用于存储接收到的数据。
    public String sRecTime;//一个字符串，用于存储数据接收的时间。
    public String sComPort;//一个字符串，用于存储数据所在的串口号。

    /*
    该构造函数接受三个参数：串口号 sPort、字节数组 buffer 和数据的大小 size。
     */
    public ComBean(String sPort, byte[] buffer, int size) {
        this.sComPort = sPort;
        this.bRec = new byte[size];//创建实例
        for (int i = 0; i < size; i++) {
            this.bRec[i] = buffer[i];
        }//传输数据
        // 这行代码的作用是创建一个时间格式化器，它可以将时间对象按照 "小时:分钟:秒.毫秒" 的格式进行格式化。
        SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss");
        //这行代码的作用是将当前日期和时间格式化成指定格式的字符串，并将结果赋值给 sRecTime 属性。
        this.sRecTime = sDateFormat.format(new Date());
    }

    /*
    这是 ComBean 类中的一个构造函数，用于从一个 Parcel 对象中读取数据以恢复 ComBean 对象的状态。
    在 Android 中，Parcelable 接口通常用于传递对象的数据。
    Parcel 是一个用于在不同组件之间传递数据的 Android 类，它允许将对象序列化为字节流，然后在不同组件之间传递这些字节流数据。
     */
    protected ComBean(Parcel in) {
        /*
        从 Parcel 对象 in 中读取一个字节数组，并将其赋值给 bRec 属性。
        这里假定 in 中包含了之前通过 writeToParcel 方法写入的字节数组。
         */
        bRec = in.createByteArray();
        /*
        从 Parcel 对象 in 中读取一个字符串，并将其赋值给 sRecTime 属性。
        这里假定 in 中包含了之前通过 writeToParcel 方法写入的字符串，通常是表示数据接收时间的字符串。
         */
        sRecTime = in.readString();
         /*
        从 Parcel 对象 in 中读取一个字符串，并将其赋值给 sComPort 属性。
        这里假定 in 中包含了之前通过 writeToParcel 方法写入的字符串，通常是表示数据所在串口的字符串。
         */
        sComPort = in.readString();
    }
    /*
    这是 ComBean 类中的一个内部静态成员，它实现了 Parcelable.Creator 接口，用于创建 ComBean 对象。
    在 Android 中，Parcelable.Creator 接口的主要作用是用于在反序列化过程中创建对象。
     */
    /*
    public static final Creator<ComBean> CREATOR：
    这是一个静态的 Parcelable.Creator 类型的常量，它用于创建 ComBean 对象。CREATOR 常量是必需的，因为它会在反序列化时被使用。
     */
    public static final Creator<ComBean> CREATOR = new Creator<ComBean>() {
        @Override
        public ComBean createFromParcel(Parcel in) {
            return new ComBean(in);
        }

        @Override
        public ComBean[] newArray(int size) {
            return new ComBean[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(bRec);
        dest.writeString(sRecTime);
        dest.writeString(sComPort);
    }
}
