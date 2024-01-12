package tp.xmaihh.serialport;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;
import tp.xmaihh.serialport.bean.ComBean;
import tp.xmaihh.serialport.stick.AbsStickPackageHelper;
import tp.xmaihh.serialport.stick.BaseStickPackageHelper;
import tp.xmaihh.serialport.utils.ByteUtil;

public abstract class SerialHelper {
    private SerialPort mSerialPort;//声明了一个mSerialPort对象，创建之后里面会打开串口的权限，同时设置串口路径，波特率，数据位等参数
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;//声明了一个名为 mReadThread 读线程的私有成员变量
    private SendThread mSendThread;//声明了一个名为 mSendThread 写线程的私有成员变量
    private String sPort = "/dev/ttyS1";//默认开启串口设备名称
    private int iBaudRate = 9600;//默认波特率
    private int stopBits = 1;//默认停止位
    private int dataBits = 8;
    private int parity = 0;
    private int flowCon = 0;
    private int flags = 0;
    private boolean _isOpen = false;///串口是否打开的标志位
    private byte[] _bLoopData = {48};//用于循环发送的数据
    private int iDelay = 500;//发送数据之间的延迟时间


    //构造函数，在创建SerialHelper类的同时传入参数sPort（串口设备名称）以及iBaudRate（波特率）参数
    //构造函数接受串口设备名称和波特率作为参数，并用于初始化 sPort 和 iBaudRate 变量。
    public SerialHelper(String sPort, int iBaudRate) {
        this.sPort = sPort;
        this.iBaudRate = iBaudRate;
    }
    //open()方法
    /*
    用于打开串口连接，实例化 SerialPort 对象，并启动读取和发送数据的线程。
     */
    public void open()
            throws SecurityException, IOException, InvalidParameterException {
        //实例化一个串口对象（SerialPort），
        //在给定路径（sPort 变量的值）的基础上创建一个新的 File 对象。File 类是 Java 中用于表示文件和目录的类，它可以用于执行与文件系统相关的操作。
        this.mSerialPort = new SerialPort(new File(this.sPort), this.iBaudRate, this.stopBits, this.dataBits, this.parity, this.flowCon, this.flags);
        this.mOutputStream = this.mSerialPort.getOutputStream();//mOutputStream文件输出流
        this.mInputStream = this.mSerialPort.getInputStream();//mInputStream文件输入流
        this.mReadThread = new ReadThread();//实例化一个读取数据的线程
        this.mReadThread.start();//读取数据的线程启动
        this.mSendThread = new SendThread();//实例化一个发送数据的线程
        this.mSendThread.setSuspendFlag();//这个方法可能用于在 mSendThread 中设置一个标志，以控制线程的挂起状态。挂起状态表示线程暂时停止执行，等待某种条件的触发或信号的发送才能继续执行。
        this.mSendThread.start();//发送数据的线程启动
        this._isOpen = true;//串口打开
    }
    //关闭串口
    /*
    用于关闭串口连接，中断读取数据的线程，并关闭串口。
    同时将串口开启标志位设置为false
     */
    /*
    mReadThread 用于在后台不断读取串口数据，当需要关闭串口时，
    通过调用 this.mReadThread.interrupt() 来通知线程中断，
    然后线程内部会检查中断标志位并执行清理工作，最终终止线程的执行。这是一种安全地终止线程的方式。
     */
    public void close() {
        if (this.mReadThread != null) {
            this.mReadThread.interrupt();
        }
        if (this.mSerialPort != null) {
            this.mSerialPort.close();//这个close()方法时SerialPort中的方法，也就是连接的SerialPort.c中的方法
            this.mSerialPort = null;
        }
        this._isOpen = false;
    }

    /*
send()：发送字节数组数据。

 */
    //this.mOutputStream 是一个串口的输出流对象，用于将数据写入到串口中。
    //.write(bOutArray) 是输出流的 write 方法，用于将字节数组 bOutArray 中的数据写入到输出流中。


    // 写入的数据将会被发送到串口设备。
    //bOutArray 是一个字节数组，包含了要发送到串口的数据。
    public void send(byte[] bOutArray) {
        try {
            this.mOutputStream.write(bOutArray);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
   ByteUtil.HexToByteArr(sHex)：这一行代码调用了 ByteUtil 类的静态方法 HexToByteArr，
   该方法用于将十六进制字符串转换为字节数组。sHex 字符串中的每两个字符被转换为一个字节。
   例如，如果 sHex 是 "1A2B3C"，则会转换为字节数组 {0x1A, 0x2B, 0x3C}。0x1A:00010010 ;0x2B:00100011
    */

    //sendHex和sendText都是在内部调用了send方法，发送的字节数组
    public void sendHex(String sHex) {
        byte[] bOutArray = ByteUtil.HexToByteArr(sHex);
        send(bOutArray);
    }
    /*
   sTxt.getBytes()：这一行代码使用字符串的 getBytes() 方法将文本字符串 sTxt 转换为字节数组。
   这个操作将字符串的每个字符转换为对应的字节表示，根据字符编码来确定字节数组的内容。
    */
    /*
    byte[] bOutArray：这一行代码将转换后的字节数组存储在名为 bOutArray 的变量中。

    send(bOutArray)：最后，使用 send 方法将字节数组 bOutArray 发送到串口。这实现了将文本字符串数据发送到串口的功能。
     */
    public void sendTxt(String sTxt) {
        byte[] bOutArray = sTxt.getBytes();
        send(bOutArray);
    }
    /*
   private class ReadThread extends Thread：定义了一个内部类 ReadThread，
   它继承自 Thread 类，因此可以作为一个独立的线程运行。
    */
    private class ReadThread
            extends Thread {
        private ReadThread() {
        }//构造函数
        //这是 Thread 类的 run 方法的重写。在这个方法中，定义了线程的主要逻辑，即持续读取串口数据。
        public void run() {
            super.run();
            //while (!isInterrupted())：这是一个循环，只要线程没有被中断（isInterrupted() 方法返回 false），就会一直执行下去。
            while (!isInterrupted()) {
                try {
                    /*
                    这一行代码检查串口输入流是否为 null，如果为 null，就直接返回，表示无法继续读取数据。
                     */
                    if (SerialHelper.this.mInputStream == null) {
                        return;
                    }
                    /*
                    byte[] buffer = getStickPackageHelper().execute(SerialHelper.this.mInputStream)：
                    这一行代码使用 getStickPackageHelper() 方法来处理串口输入流中的数据，
                    可能会进行粘包处理或其他处理。处理后的数据存储在 buffer 字节数组中。
                     */
                    byte[] buffer = getStickPackageHelper().execute(SerialHelper.this.mInputStream);
                    if (buffer != null && buffer.length > 0) {
                        ComBean ComRecData = new ComBean(SerialHelper.this.sPort, buffer, buffer.length);
                        SerialHelper.this.onDataReceived(ComRecData);//这里是直接使用了onDataReceived()函数
                    }
//                    int available = SerialHelper.this.mInputStream.available();
//
//                    if (available > 0) {
//                        byte[] buffer = new byte['?'];
//                        int size = SerialHelper.this.mInputStream.read(buffer);
//                        if (size > 0) {
//                            ComBean ComRecData = new ComBean(SerialHelper.this.sPort, buffer, size);
//                            SerialHelper.this.onDataReceived(ComRecData);
//                        }
//                    } else {
//                        SystemClock.sleep(50);
//                    }

                } catch (Throwable e) {
                    Log.e("error", e.getMessage());
                    return;
                }
            }
        }
    }

    /*
    private class SendThread extends Thread：定义了一个内部类 SendThread，
    它继承自 Thread 类，因此可以作为一个独立的线程运行。
     */
    private class SendThread
            extends Thread {
        /*
        public boolean suspendFlag = true：定义了一个名为 suspendFlag 的布尔类型成员变量，
        用于控制线程的挂起和恢复状态。初始值为 true，表示线程初始时处于挂起状态。
         */
        public boolean suspendFlag = true;

        private SendThread() {
        }

        public void run() {
            super.run();
             /*
            while (!isInterrupted())：这是一个循环，只要线程没有被中断（isInterrupted() 方法返回 false），就会一直执行下去。
             */
            while (!isInterrupted()) {
                /*
                synchronized (this)：这是一个同步块，用于对当前线程对象进行同步。在这个同步块内部，
                线程会检查 suspendFlag 的状态，如果为 true，则通过 wait() 方法将线程挂起，
                直到被唤醒（setResume() 方法被调用）。      wait()函数与setResume()方法对应
                 */
                synchronized (this) {
                    while (this.suspendFlag) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                /*
                SerialHelper.this.send(SerialHelper.this.getbLoopData())：
                这一行代码调用了 SerialHelper 对象的 send 方法，用于发送串口数据。
                发送的数据是通过 SerialHelper.this.getbLoopData() 获取的，这是一个用于获取待发送数据的方法。
                 */
                SerialHelper.this.send(SerialHelper.this.getbLoopData());
                /*
                这是一个线程休眠的操作，用于控制发送数据的间隔时间。
                SerialHelper.this.iDelay 是休眠的时间，单位是毫秒。
                 */
                try {
                    Thread.sleep(SerialHelper.this.iDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        //用于设置 suspendFlag 为 true，将线程挂起。
        public void setSuspendFlag() {
            this.suspendFlag = true;
        }
        /*
        public synchronized void setResume() 方法：
        用于设置 suspendFlag 为 false，并通过 notify() 方法唤醒线程。
         */
        public synchronized void setResume() {
            this.suspendFlag = false;
            notify();
        }
    }
    //获得波特率
    public int getBaudRate() {
        return this.iBaudRate;
    }
    //设置波特率
    public boolean setBaudRate(int iBaud) {
        if (this._isOpen) {//串口已经打开了，就不需要再设置波特率
            return false;
        }
        this.iBaudRate = iBaud;
        return true;
    }

    public boolean setBaudRate(String sBaud) {//方法重载
        int iBaud = Integer.parseInt(sBaud);
        return setBaudRate(iBaud);
    }

    public int getStopBits() {
        return this.stopBits;
    }

    public boolean setStopBits(int stopBits) {//设置停止位
        if (this._isOpen) {
            return false;
        }
        this.stopBits = stopBits;
        return true;
    }

    public int getDataBits() {
        return this.dataBits;
    }

    public boolean setDataBits(int dataBits) {//设置数据位
        if (this._isOpen) {
            return false;
        }
        this.dataBits = dataBits;
        return true;
    }

    public int getParity() {
        return this.parity;
    }

    public boolean setParity(int parity) {
        if (this._isOpen) {
            return false;
        }
        this.parity = parity;
        return true;
    }

    public int getFlowCon() {
        return this.flowCon;
    }

    public boolean setFlowCon(int flowCon) {
        if (this._isOpen) {
            return false;
        }
        this.flowCon = flowCon;
        return true;
    }

    public String getPort() {
        return this.sPort;
    }

    public boolean setPort(String sPort) {
        if (this._isOpen) {
            return false;
        }
        this.sPort = sPort;
        return true;
    }

    public boolean isOpen() {
        return this._isOpen;
    }

    public byte[] getbLoopData() {
        return this._bLoopData;
    }

    public void setbLoopData(byte[] bLoopData) {
        this._bLoopData = bLoopData;
    }//设置字节组数据bLoopData

    public void setTxtLoopData(String sTxt) {
        this._bLoopData = sTxt.getBytes();
    }

    public void setHexLoopData(String sHex) {
        this._bLoopData = ByteUtil.HexToByteArr(sHex);
    }

    public int getiDelay() {
        return this.iDelay;
    }

    public void setiDelay(int iDelay) {
        this.iDelay = iDelay;
    }
    /*
   public void startSend()：这是一个公共方法，可以从外部调用，用于启动串口数据发送线程。

   if (this.mSendThread != null)：这一行代码检查 mSendThread 对象是否为空，确保线程对象已经创建。

   this.mSendThread.setResume()：如果 mSendThread 不为空，就调用 setResume() 方法，
   将发送线程的挂起状态设置为 false，以使线程开始发送数据。
    */
    public void startSend() {
        if (this.mSendThread != null) {
            this.mSendThread.setResume();//发送线程启动
        }
    }
    //停止数据的传送
    //如果数据传输线程不为空，设置线程悬挂
    public void stopSend() {
        if (this.mSendThread != null) {
            this.mSendThread.setSuspendFlag();
        }
    }

    /*
  protected abstract void onDataReceived(ComBean paramComBean)：
  这是一个抽象方法，它被声明为 protected，表示只能在 SerialHelper 的子类中实现。
  该方法接受一个 ComBean 类型的参数 paramComBean，用于处理接收到的串口数据。
   */
    //这个方法在MainActivity中实现的
    protected abstract void onDataReceived(ComBean paramComBean);

    /*
   = new BaseStickPackageHelper()：这一部分表示将 mStickPackageHelper 初始化为 BaseStickPackageHelper 类的一个新实例。
   这意味着 mStickPackageHelper 引用了 BaseStickPackageHelper 对象。
    */
    //具体的黏包处理可以看serialport.src.man.java.tp.xmaihh.serialport.stick
    private AbsStickPackageHelper mStickPackageHelper = new BaseStickPackageHelper();  // 默认不处理粘包，直接读取返回

    public AbsStickPackageHelper getStickPackageHelper() {
        return mStickPackageHelper;
    }

    public void setStickPackageHelper(AbsStickPackageHelper mStickPackageHelper) {
        this.mStickPackageHelper = mStickPackageHelper;
    }

}
