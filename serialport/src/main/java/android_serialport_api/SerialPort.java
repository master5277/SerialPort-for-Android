/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {

    private static final String TAG = "SerialPort";

    /**
     * 串口波特率定义
     */
    public enum BAUDRATE {
        B0(0),
        B50(50),
        B75(75),
        B110(110),
        B134(134),
        B150(150),
        B200(200),
        B300(300),
        B600(600),
        B1200(1200),
        B1800(1800),
        B2400(2400),
        B4800(4800),
        B9600(9600),
        B19200(19200),
        B38400(38400),
        B57600(57600),
        B115200(115200),
        B230400(230400),
        B460800(460800),
        B500000(500000),
        B576000(576000),
        B921600(921600),
        B1000000(1000000),
        B1152000(1152000),
        B1500000(1500000),
        B2000000(2000000),
        B2500000(2500000),
        B3000000(3000000),
        B3500000(3500000),
        B4000000(4000000);

        int baudrate;

        BAUDRATE(int baudrate) {
            this.baudrate = baudrate;
        }

        int getBaudrate() {
            return this.baudrate;
        }

    }

    /**
     * 串口停止位定义
     */
    /**
     * Serial port stop bit
     */
    //STOPB 是一个枚举类型，它定义了两个枚举常量：B1 和 B2。
    //B1 和 B2 分别代表串口通信的两种不同的停止位设置。
    public enum STOPB {
        /**
         * 1位停止位
         */
        B1(1),
        /**
         * 2位停止位
         */
        B2(2);

        int stopBit;

        STOPB(int stopBit) {
            this.stopBit = stopBit;
        }

        public int getStopBit() {
            return this.stopBit;
        }

    }

    /**
     * 串口数据位定义
     */
    public enum DATAB {
        /**
         * 5位数据位
         */
        CS5(5),
        /**
         * 6位数据位
         */
        CS6(6),
        /**
         * 7位数据位
         */
        CS7(7),
        /**
         * 8位数据位
         */
        CS8(8);

        int dataBit;

        DATAB(int dataBit) {
            this.dataBit = dataBit;
        }

        public int getDataBit() {
            return this.dataBit;
        }
    }

    /**
     * 串口校验位定义
     */
    public enum PARITY {
        /**
         * 无奇偶校验
         */
        NONE(0),
        /**
         * 奇校验
         */
        ODD(1),
        /**
         * 偶校验
         */
        EVEN(2);

        int parity;

        PARITY(int parity) {
            this.parity = parity;
        }

        public int getParity() {
            return this.parity;
        }
    }

    /**
     * 串口流控定义
     */
    public enum FLOWCON {
        /**
         * 不使用流控
         */
        NONE(0),
        /**
         * 硬件流控
         */
        HARD(1),
        /**
         * 软件流控
         */
        SOFT(2);

        int flowCon;

        FLOWCON(int flowCon) {
            this.flowCon = flowCon;
        }

        public int getFlowCon() {
            return this.flowCon;
        }
    }


    /*
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    //FileDescriptor文件描述符
    //FileDescriptor 是Java中用于表示文件描述符的类。
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    //SerialPort类的构造函数，传入参数baudrate,stopBits,dataBits,parity,flowCon等参数
    public SerialPort(File device, int baudrate, int stopBits, int dataBits, int parity, int flowCon, int flags) throws SecurityException, IOException {

        /* Check access permission */  // 检查是否获取了指定串口的读写权限
          /*
        这段代码的主要目的是检查指定的设备文件是否具有读取和写入权限。
        如果设备文件没有读取或写入权限，代码会尝试使用超级用户权限（su）修改文件的权限，以便后续可以进行读取和写入操作。
        如果权限修改失败或者修改后仍然没有读写权限，代码会抛出 SecurityException 异常，表示没有足够的权限进行操作。
         */
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                // 如果没有获取指定串口的读写权限，则通过挂在到linux的方式修改串口的权限为可读写
                //su 变量用于表示一个正在运行的进程，通常是用于获取超级用户权限的进程。
                Process su;
                /*
                使用了Java的 Runtime 类的 getRuntime() 方法获取运行时对象，//首先获取运行时对象
                然后调用 exec() 方法来执行命令 "/system/bin/su"，//然后执行命令
                这是一个在类 Unix 操作系统上用于获取超级用户权限的命令。
                 */
                su = Runtime.getRuntime().exec("/system/bin/su");
                /*
                "chmod 666 " + device.getAbsolutePath() + "\n"：这是第一个命令，它使用 chmod 命令修改指定文件的权限。
                666 表示将文件的权限设置为可读（R）、可写（W）和不可执行（X），即文件所有者、文件所属组和其他用户都具有读取和写入权限。
                device.getAbsolutePath()：这部分是获取 device 对象的绝对路径的方法，通常表示要修改权限的文件的路径。
                "\n"：这是一个换行符，用于将第一个命令和第二个命令分隔开。
                "exit\n"：这是第二个命令，它是一个退出命令，通常用于结束执行超级用户权限的子进程。
                 */
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                /*
                cmd.getBytes(): 将存储在 cmd 字符串中的命令转换为字节数组。
                因为 getOutputStream().write() 方法接受字节数组作为输入，所以需要将字符串转换为字节数组。
                write(cmd.getBytes()): 将字节数组写入到 su 进程的标准输入。这相当于将命令发送给 su 进程，以便执行这些命令
                su.getOutputStream(): 从 su 进程中获取输出流，这个输出流可以用于将数据写入到子进程的标准输入（stdin）中。
                 */
                su.getOutputStream().write(cmd.getBytes());
                /*
                su.waitFor(): 这个部分使用 waitFor() 方法来等待超级用户权限的子进程 su 执行完毕并返回退出状态码。
                一般来说，如果命令执行成功，状态码通常为0。如果状态码不等于0，说明命令执行失败。//看看权限赋予是否成功
                 */
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }
        //open是本地方法，在下面声明了是native类型的
        //给open方法传入参数，执行方法，返回一个文件描述符FileDescriptor类型的参数
        mFd = open(device.getAbsolutePath(), baudrate, stopBits, dataBits, parity, flowCon, flags);
        //如果mFd等于null，那么就是说本地方法native open方法打开失败
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        /*
        这行代码的目的是创建一个 FileInputStream，它可以用于从与 mFd 关联的文件或资源中读取数据。
        这种方式常用于在Java程序中进行底层I/O操作，例如读取串口设备的数据或其他文件操作。
        mFileInputStream 可以在后续的代码中用于读取数据。
        FileInputStream 是Java中用于从文件或文件描述符中读取数据的类。它允许你以字节的方式读取数据。
         */
        //输入流；输出流
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    // Getters and setters
    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    // JNI
    // JNI：调用java本地接口，实现串口的打开和关闭

    /**
     * 串口有五个重要的参数：串口设备名，波特率，检验位，数据位，停止位
     * 其中检验位一般默认位NONE,数据位一般默认为8，停止位默认为1
     *
     * @param path     串口设备的据对路径
     * @param baudrate {@link BAUDRATE}波特率
     * @param stopBits {@link STOPB}停止位
     * @param dataBits {@link DATAB}数据位
     * @param parity   {@link PARITY}校验位
     * @param flowCon  {@link FLOWCON}流控
     * @param flags    O_RDWR  读写方式打开 | O_NOCTTY  不允许进程管理串口 | O_NDELAY   非阻塞
     * @return
     */
    //native 关键字标记的，表示它是由本地库提供的方法。
    //这个方法用于打开串口通信，并返回一个文件描述符（FileDescriptor）。
    //声明下这个本地方法
    private native static FileDescriptor open(String path, int baudrate, int stopBits, int dataBits, int parity, int flowCon, int flags); //打开串口

    public native void close(); //关闭串口
    //这段代码是一个静态代码块（static block），它在类加载时执行，用于加载一个本地库（native library）称为 "serialport"。
    //System.loadLibrary 方法用于加载本地库，这些库通常是用C或C++编写的，并通过Java的本地接口（JNI）与Java代码交互。
    //具体的链接要看CMakeLists.txt的内容，里面的内容是关于爱用JNI接口连接本地库。
    static {
        System.loadLibrary("serialport"); // 载入底层C文件 so库链接文件
    }
}
