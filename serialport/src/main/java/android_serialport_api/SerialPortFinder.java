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
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

public class SerialPortFinder {//定义 SerialPortFinder 类：查找可用的串口设备，提供了获取串口设备名称和路径的方法。


    public class Driver {
		//Driver 类表示一个串口驱动，包含驱动名称和设备根目录等信息。该类还维护了该驱动下的串口设备列表。
        public Driver(String name, String root) {
            mDriverName = name;
            mDeviceRoot = root;
        }

        private String mDriverName;
        private String mDeviceRoot;
		//mDrivers：表示所有已发现的串口驱动列表
		//mDevices: 表示的是某个串口驱动下的串口设备列表。
        Vector<File> mDevices = null;
		//Vector<File>：这部分声明了一个 Vector 类型的变量，该 Vector 存储的元素类型是 File，即文件对象。Vector 是 Java 中的一种动态数组数据结构，可以存储多个元素并根据需要动态扩展。
		//mDevices：这是变量的名称，通常用来表示某个类的成员变量。在这里，mDevices 表示的是某个串口驱动下的串口设备列表。
		//= null：这部分表示在声明变量时将其初始化为 null，即空引用。这意味着在声明时，mDevices 这个串口设备列表还没有被分配内存空间，因此它当前是一个空列表。

		//获取特定串口驱动下的串口设备列表，并返回这个列表。
        public Vector<File> getDevices() {
            if (mDevices == null) {
				//这行代码创建了一个新的 Vector 对象，并将其赋值给 mDevices 变量，从而初始化了设备列表。
				//初始化mDevices列表
                mDevices = new Vector<File>();
				//这行代码创建了一个 File 对象 dev，用于表示设备文件夹 /dev。
                File dev = new File("/dev");
				//这行代码获取了设备文件夹 /dev 中的所有文件和子文件夹，并将它们存储在 files 数组中。
                File[] files = dev.listFiles();

                if (files != null) {
                    int i;
					//for (i = 0; i < files.length; i++)：这是一个循环语句，用于遍历 files 数组中的所有文件和子文件夹。
                    for (i = 0; i < files.length; i++) {
                        if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
							//if (files[i].getAbsolutePath().startsWith(mDeviceRoot))：这是一个条件语句，用于检查文件的绝对路径是否以 mDeviceRoot 变量指定的路径为前缀。如果是，表示这个文件是符合条件的设备文件。
                            Log.d(TAG, "Found new device: " + files[i]);
							//mDevices.add(files[i]);：如果文件符合条件，就将它添加到 mDevices 设备列表中。
                            mDevices.add(files[i]);
                        }
                    }
                }
            }
            return mDevices;//返回 mDevices 设备列表。
        }

        public String getName() {
            return mDriverName;
        }
    }

	private static final String TAG = "SerialPort";

	private Vector<Driver> mDrivers = null;//别把mDrivers和mDevices弄混了
	//这段代码定义了 getDrivers 方法，它的作用是获取系统中的串口驱动信息，并返回一个存储驱动信息的 Vector 列表。
	Vector<Driver> getDrivers() throws IOException {
		if (mDrivers == null) {
			//mDrivers 是一个成员变量，它的类型是 Vector<Driver>，表示一个向量或列表，用于存储 Driver 对象（串口驱动信息）的集合。
			//在代码中，mDrivers 变量在第一次使用前进行了初始化操作，即创建了一个新的空向量，以便后续可以向其中添加 Driver 对象。
			mDrivers = new Vector<Driver>();
			//new FileReader("/proc/tty/drivers")：创建一个 FileReader 对象，用于读取指定路径的文件内容。在这里，指定的文件路径是"/proc/tty/drivers"，表示要读取的文件是位于 /proc/tty/drivers 目录下的文件。
			//new LineNumberReader(...)：将 FileReader 对象包装在 LineNumberReader 中，以便可以读取文件的内容，并且 LineNumberReader 可以记录行号（行号从 1 开始计数）。
			//整个代码行的作用是创建一个能够读取 /proc/tty/drivers 文件内容并记录行号的 LineNumberReader 对象
			//该对象被命名为 r，以便后续可以使用 r 来读取文件内容和获取行号。这种操作通常用于读取系统文件或配置文件中的内容，
			LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));

			String l;
			//r.readLine()：这是 LineNumberReader 类的方法，用于从输入流中读取一行文本。
			//它会读取一行文本内容并返回为一个字符串。如果已经读取到文件的末尾，它会返回 null，表示没有更多的内容可读。
			//在代码中，每次循环执行这行代码，都会读取 r 中的下一行文本，并将其存储在变量 l 中。这个循环会一直执行，直到读取到文件末尾为止。这个文本行通常包含有关设备驱动程序的信息。
			while((l = r.readLine()) != null) {
				// Issue 3:
				// Since driver name may contain spaces, we do not extract driver name with split()
				//l：这是先前从文件中读取的一行文本，它包含有关设备驱动程序的信息。
				//.substring(0, 0x15)：这是一个字符串截取操作，它从文本行的索引 0（起始位置）开始，截取到索引 0x15（十进制的21）之前的字符。这个操作实际上截取了一个固定长度的子字符串，包含了驱动程序的名称。
				//.trim()：这是一个字符串修剪操作，它用于去除字符串的前导和尾随空格（包括空格、制表符等）。这是为了确保驱动程序名称的前后不包含多余的空格。
				String drivername = l.substring(0, 0x15).trim();
				//l：这是先前从文件中读取的一行文本，它包含了有关设备驱动程序的信息。
				//.split(" +")：这是一个字符串拆分操作，它将字符串 l 拆分成多个子字符串，并且拆分的依据是一个或多个连续的空格字符。这个正则表达式模式 " +" 表示匹配一个或多个空格字符。
				//拆分后的子字符串会存储在数组 w 中，每个子字符串代表了一部分信息。这种拆分通常用于解析文本数据，特别是当数据的各个部分由空格或其他分隔符分隔时。
				String[] w = l.split(" +");
				//w.length >= 5：这个条件检查数组 w 的长度是否至少为 5，以确保数组包含足够的元素供后续处理。如果数组长度小于 5，那么就没有足够的信息来表示一个有效的设备驱动程序。
				//w[w.length-1].equals("serial")：这个条件检查数组 w 的最后一个元素是否等于字符串 "serial"。这是为了确定该驱动程序是否是串口设备的驱动程序。只有当最后一个元素为 "serial" 时，才会继续处理这个驱动程序。
				//Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length-4])：这是一个调试日志输出语句，它将找到的驱动程序的名称 drivername 和路径信息 w[w.length-4] 记录到 Android 的 Logcat 中，以便开发人员进行调试和跟踪。
				if ((w.length >= 5) && (w[w.length-1].equals("serial"))) {
					Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length-4]);
					//w[w.ength-4]为路径信息
					//drivername是驱动程序名称，w[w.length-4]是根目录
					mDrivers.add(new Driver(drivername, w[w.length-4]));
				}
			}
			//r.close() 是用于关闭 LineNumberReader 对象 r 的方法调用。在 Java 中，当不再需要一个文件或流时，最好将其关闭以释放相关资源并避免资源泄漏。
			r.close();
		}
		return mDrivers;//mDrivers里面是多个Driver,而Driver包括驱动程序和根目录，根据getDevices()方法可以获取特定驱动程序下的串口设备列表
	}

	//这段代码是用于获取系统中所有串口设备的名称和对应的驱动程序名称的方法。
	public String[] getAllDevices() {
		//创建一个字符串类型的向量 devices，用于存储串口设备的名称和驱动程序名称。
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		// 创建一个迭代器 itdriv，用于遍历驱动程序列表。
		Iterator<Driver> itdriv;
		try {
			//itdriv = getDrivers().iterator(); 这行代码的作用是获取所有驱动程序并创建一个迭代器以遍历它们。
			itdriv = getDrivers().iterator();
			//while循环，当迭代器中还有元素时就继续参与循环
			while(itdriv.hasNext()) {
				Driver driver = itdriv.next();
				//getDevices()方法是在Driver类中实现的
				//获取到驱动器之后就可以使用getDevices()来获取特定的驱动设备下的串口设备列表，得到列表对象之后建立迭代器
				Iterator<File> itdev = driver.getDevices().iterator();
				//itdev是设备迭代器，，当迭代器中还有元素时就继续参与while循环
				while(itdev.hasNext()) {
					//getName() 是 File 类的一个方法，它用于获取文件对象的名称
					String device = itdev.next().getName();
					//将设备文件名称和驱动程序名称格式化为一个字符串 value，形如 "设备文件名称 (驱动程序名称)"
					String value = String.format("%s (%s)", device, driver.getName());
					//将格式化后的字符串 value 添加到 devices 向量中，表示一个串口设备。
					devices.add(value);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//通过 devices.toArray(new String[devices.size()]) 将向量 devices 转换为字符串数组，并返回包含所有串口设备名称的数组。
		//Vector<String> devices = new Vector<String>();也就是说devices时Vector类型的，Vector 是一个动态数组
		//toArray() 方法可以将 Vector 中的元素转换为一个数组。在这个例子中，toArray() 方法返回一个 Object 类型的数组，
		// 但是由于我们知道这个数组中的元素都是 String 类型，因此我们可以通过传递一个指定类型的数组（new String[devices.size()]）给 toArray() 方法，使其返回一个 String 类型的数组。
		//这个数组的大小和 devices 中的元素数量相同，它包含了 devices 中所有的字符串。最终，这个字符串数组被作为方法的返回值。
		return devices.toArray(new String[devices.size()]);
	}

	//获取设备路径
	public String[] getAllDevicesPath() {
		//创建一个Vector类型的字符串数组devices
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		//创建Driver类型迭代器
		Iterator<Driver> itdriv;
		try {
			//这是获取到驱动程序的迭代器
			itdriv = getDrivers().iterator();
			//当迭代器中还有驱动程序时就继续实行while循环
			while(itdriv.hasNext()) {
				//获取下一个驱动程序
				Driver driver = itdriv.next();
				//获取到驱动程序之后就利用Driver类内定义的getDevices()方法获得当前驱动程序的设备列表，然后得到设备列表的迭代器
				Iterator<File> itdev = driver.getDevices().iterator();
				//这两个循环的意思时先遍历所有的驱动程序，再遍历每个驱动程序之下的设备
				//遍历设备
				while(itdev.hasNext()) {
					//获取到下一个设备之后，利用getAbsolutePath()方法得到设备的绝对路径
					String device = itdev.next().getAbsolutePath();
					//得到绝对路径之后就把路径添加到Vector类型的字符串数组devices
					devices.add(device);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		//和上面一样，使用toArray方法返回返回一个 Object 类型的数组，返回的是String，所以使用new String[devices.size()];
		return devices.toArray(new String[devices.size()]);
	}
}
