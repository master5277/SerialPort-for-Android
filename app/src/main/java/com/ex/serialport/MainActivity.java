package com.ex.serialport;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ex.serialport.adapter.LogListAdapter;
import com.ex.serialport.adapter.SpAdapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android_serialport_api.SerialPortFinder;
import tp.xmaihh.serialport.SerialHelper;
import tp.xmaihh.serialport.bean.ComBean;
import tp.xmaihh.serialport.utils.ByteUtil;
//AppCompatActivity 是 Android Support Library 提供的一个基类，它用于支持旧版本的 Android 平台（Android 2.1 及更高版本）使用新的 Material Design 风格和现代的 UI 组件。
public class MainActivity extends AppCompatActivity {

    //声明各个使用到的模块名称
    //AppCompatActivity 是 Android Support Library 提供的一个基类，它用于支持旧版本的 Android 平台（Android 2.1 及更高版本）使用新的 Material Design 风格和现代的 UI 组件。

    //RecyclerView 是一种用于显示列表和网格布局的高度可定制的视图容器。它通常用于显示大量数据项目
    //声明一个名为 recy 的私有成员变量，并且该变量的类型是 RecyclerView
    private RecyclerView recy;
    //声明一个下拉列表
    private Spinner spSerial;
    //声明一个输入框
    private EditText edInput;
    private Button btSend;
    //声明一个单选按钮集合（Text ，Hex）
    private RadioGroup radioGroup;
    //声明两个单选按钮
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    //这个是使用了serialport包里面的serialport.src.main.java.android_serialport_api.SerialPortFinder类
    //定义了一个串口查找相关的类，然后声明了一个这样的对象
    private SerialPortFinder serialPortFinder;
    //这个是使用了serialport包里的serial.tp.xmaihh.serialprt.SerialHelper类
    //定义了一个串口帮助相关的类，这个类中包含设置打开串口，关闭串口，发送以及接收数据
    private SerialHelper serialHelper;
    private Spinner spBote;
    private Button btOpen;
    //使用LogListAdapter类声明一个实例化对象logListAdapter，RecyclerView适配器
    private LogListAdapter logListAdapter;
    //声明一个数据位的下拉框
    private Spinner spDatab;
    //声明一个校验位的下拉框
    private Spinner spParity;
    //声明一个停止位的下拉框
    private Spinner spStopb;
    //声明一个流控位的下拉框
    //软件流控位（Software Flow Control）：软件流控位是通过特定的控制字符来实现的，通常使用了两个特殊字符：
    //
    //XON（Transmit On）：发送端发送XON字符以指示它可以继续发送数据。
    //XOFF（Transmit Off）：发送端发送XOFF字符以指示接收端停止接收数据，直到再次收到XON字符。
    //当使用软件流控位时，发送端和接收端使用这些控制字符来控制数据的流动，从而避免数据缓冲区溢出。
    private Spinner spFlowcon;

    //页面销毁
    @Override
    protected void onDestroy() {
        //继承父类的onDestory()方法
        super.onDestroy();
        //关闭读线程，串口，，设置isopen标志位
        serialHelper.close();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//页面布局按照R.layout.activity_main创建
        recy = (RecyclerView) findViewById(R.id.recyclerView);//实例化recyclerView控件
        spSerial = (Spinner) findViewById(R.id.sp_serial);//实例化串口下拉框
        edInput = (EditText) findViewById(R.id.ed_input);//实例化编辑框
        btSend = (Button) findViewById(R.id.btn_send);//实例化发送按钮
        spBote = (Spinner) findViewById(R.id.sp_baudrate);//实例化波特率下拉框
        btOpen = (Button) findViewById(R.id.btn_open);//实例化打开按钮

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);//实例化单按钮集合
        radioButton1 = (RadioButton) findViewById(R.id.radioButton1);//实例化单选按钮1Text
        radioButton2 = (RadioButton) findViewById(R.id.radioButton2);//实例化单选按钮2Hex

        spDatab = (Spinner) findViewById(R.id.sp_databits);//实例化数据位下拉框
        spParity = (Spinner) findViewById(R.id.sp_parity);//实例化校验位下拉框
        spStopb = (Spinner) findViewById(R.id.sp_stopbits);//实例化停止位下拉框
        spFlowcon = (Spinner) findViewById(R.id.sp_flowcon);//实例化流控位下拉框

        logListAdapter = new LogListAdapter(null);//实例化一个RecyclerView适配器
         /*
        recy.setLayoutManager(new LinearLayoutManager(this));：设置 RecyclerView 的布局管理器，
        这里使用了 LinearLayoutManager，它会将列表项按照线性布局方式排列。this 表示当前活动或上下文，用于获取正确的上下文信息。
         */
        recy.setLayoutManager(new LinearLayoutManager(this));
          /*
        recy.setAdapter(logListAdapter);：将 logListAdapter 适配器设置到 RecyclerView 中，以便将数据显示在 RecyclerView 中。
         */
        recy.setAdapter(logListAdapter);
        /*
       recy.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));：
       添加一个分割线到 RecyclerView 中。这行代码创建了一个 DividerItemDecoration 对象，用于在 RecyclerView 中的列表项之间添加分割线。
       DividerItemDecoration.VERTICAL 表示分割线的方向为垂直方向。然后，将这个分割线装饰器添加到 RecyclerView 中。
         */
        //这个分割线是用来作为基准进行布局的，具体看：R.layout.id.gline,这里的操作是将分割线装入
        recy.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //实例化串口查找对象，这里的是查找的所有的串口，先查找所有的串口驱动，然后查找各个串口驱动下的串口
        serialPortFinder = new SerialPortFinder();
        //创建一个SerialHelper对象，同时实现onDataReceived()方法
        /*
        使用匿名内部类方式创建了一个 SerialHelper 的子类实例，覆盖了 onDataReceived 方法。
         */
        serialHelper = new SerialHelper("dev/ttyS1", 115200) {
            //方法中comBean中含有需要传输的数据，字节组数据
            @Override
            protected void onDataReceived(final ComBean comBean) {//传进来的是一个comBean对象
                /*
                runOnUiThread(new Runnable() { ... });：这个代码块确保在主线程上运行，在主线程上更新 UI。
                因为串口数据接收通常在后台线程中进行，为了在 UI 上显示数据，需要确保将 UI 操作放在主线程中执行。
                 */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton1) {
                             /*
                            logListAdapter.addData(...)：通过适配器 logListAdapter 将接收到的串口数据添加到 RecyclerView 中，以显示在用户界面上。
                             */
                            /*
                            new String(comBean.bRec, StandardCharsets.UTF_8) 这段代码的作用是将字节数组 comBean.bRec 解码为字符串，使用 UTF-8 字符集进行解码。
                            new String(comBean.bRec, StandardCharsets.UTF_8) 的结果将是一个包含了根据 UTF-8 编码解释的字节数据的字符串。这通常用于将接收到的二进制数据转换为文本，以便在用户界面上显示或进行进一步的处理。
                             */
                            //logListAdapter.addData(comBean.sRecTime + " Rx:<==" + new String(comBean.bRec, StandardCharsets.UTF_8))
                            try {
                                Toast.makeText(getBaseContext(), new String(comBean.bRec, "UTF-8"), Toast.LENGTH_SHORT).show();
                                logListAdapter.addData(comBean.sRecTime + "Rx:<==" + new String(comBean.bRec, "UTF-8"));
                                if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
                                    /*
                                这行代码的作用是使 RecyclerView 滚动到列表的最后一个项，以确保用户能够看到最新的数据。具体解释如下：

                                 recy 是您之前声明的 RecyclerView 对象的引用，它用于显示列表数据。

                                logListAdapter 是您的 RecyclerView 适配器，可能包含列表数据的引用。

                                logListAdapter.getData().size() 用于获取适配器中的数据项数量，也就是列表中当前的项数。

                                recy.smoothScrollToPosition(...) 用于滚动 RecyclerView 到指定的位置。在这里，通过传递 logListAdapter.getData().size()，它会将 RecyclerView 滚动到列表的最后一个项的位置。
                                 */
                                    recy.smoothScrollToPosition(logListAdapter.getData().size());
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //ByteUtil.ByteArrToHex(comBean.bRec) 这段代码的作用是将字节数组 comBean.bRec 转换为十六进制字符串表示形式。
                            Toast.makeText(getBaseContext(), ByteUtil.ByteArrToHex(comBean.bRec), Toast.LENGTH_SHORT).show();
                            logListAdapter.addData(comBean.sRecTime + "Rx:<== " + ByteUtil.ByteArrToHex(comBean.bRec));
                            if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
                                recy.smoothScrollToPosition(logListAdapter.getData().size());//滚动RecyclerView到指定位置，在这里，通过传递 logListAdapter.getData().size()，它会将 RecyclerView 滚动到列表的最后一个项的位置。
                            }
                        }
                    }
                });
            }
        };
        //获取所有的串口设备的路径
        final String[] ports = serialPortFinder.getAllDevicesPath();
        //设置波特率  final:常量
        final String[] botes = new String[]{"0", "50", "75", "110", "134", "150", "200", "300", "600", "1200", "1800", "2400", "4800", "9600", "19200", "38400", "57600", "115200", "230400", "460800", "500000", "576000", "921600", "1000000", "1152000", "1500000", "2000000", "2500000", "3000000", "3500000", "4000000"};
        //数据位
        final String[] databits = new String[]{"8", "7", "6", "5"};
        //校验位
        final String[] paritys = new String[]{"NONE", "ODD", "EVEN"};
        //停止位
        final String[] stopbits = new String[]{"1", "2"};
        /*
        final String[] flowcons = new String[]{...};：这个数组包含了流控制类型的选项，用于配置串口通信的流控制参数。
        流控制可以是 NONE（无流控制）、RTS/CTS（硬件流控制）或 XON/XOFF（软件流控制）。
         */
        //流控位
        final String[] flowcons = new String[]{"NONE", "RTS/CTS", "XON/XOFF"};

        //实例化下拉框适配器
        SpAdapter spAdapter = new SpAdapter(this);
        //将串口设备路径信息数据传入下拉框适配器
        spAdapter.setDatas(ports);
        //给控件spSerial绑定适配器
        spSerial.setAdapter(spAdapter);
         /*
        这段代码是一个事件监听器，它监测一个名为 spSerial 的 AdapterView（通常是 Spinner，即下拉列表或下拉框）的选择事件。
        当用户在下拉列表中选择一个选项时，会触发 onItemSelected 方法。
         */
        spSerial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //调用 serialHelper.close() 方法，关闭当前已打开的串口连接，确保不会同时打开多个串口。
                serialHelper.close();
                //erialHelper.setPort(ports[position]) 方法，设置 serialHelper 对象的串口端口为用户选择的新串口，其中 ports[position] 包含了选中串口的路径。
                serialHelper.setPort(ports[position]);
                //btOpen.setEnabled(true) 方法，启用一个按钮（可能是打开串口的按钮，名为 btOpen），以便用户可以点击它来打开新选择的串口。
                btOpen.setEnabled(true);
            }
            //当用户打开下拉列表或下拉框，但没有选择其中的任何选项，即点击了下拉列表但没有进一步选择其中的项时，此方法会被触发。
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpAdapter spAdapter2 = new SpAdapter(this);//实例化下拉框
        spAdapter2.setDatas(botes);//设置波特率
        spBote.setAdapter(spAdapter2);//给波特率的下拉框组件设置适配器

        spBote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //调用 serialHelper.close() 方法，关闭当前已打开的串口连接，确保不会同时打开多个串口。
                serialHelper.close();
                //使用 serialHelper.setBaudRate(botes[position]) 方法，设置串口的波特率为用户选择的波特率。
                serialHelper.setBaudRate(botes[position]);
                //btOpen.setEnabled(true) 方法，启用一个按钮（可能是打开串口的按钮，名为 btOpen）
                btOpen.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpAdapter spAdapter3 = new SpAdapter(this);
        spAdapter3.setDatas(databits);//设置数据位
        spDatab.setAdapter(spAdapter3);

        spDatab.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serialHelper.close();
                serialHelper.setDataBits(Integer.parseInt(databits[position]));//设置数据位，先将数据转换为整数
                btOpen.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpAdapter spAdapter4 = new SpAdapter(this);
        spAdapter4.setDatas(paritys);//设置传入校验位
        spParity.setAdapter(spAdapter4);

        spParity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serialHelper.close();
                serialHelper.setParity(position);//设置校验位
                btOpen.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpAdapter spAdapter5 = new SpAdapter(this);
        spAdapter5.setDatas(stopbits);//设置停止位
        spStopb.setAdapter(spAdapter5);

        spStopb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serialHelper.close();
                serialHelper.setStopBits(Integer.parseInt(stopbits[position]));
                btOpen.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SpAdapter spAdapter6 = new SpAdapter(this);
        spAdapter6.setDatas(flowcons);//设置流控位
        spFlowcon.setAdapter(spAdapter6);

        spFlowcon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serialHelper.close();
                serialHelper.setFlowCon(position);
                btOpen.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

         /*
            设置OPEN按钮的监听器，点击之后就执行try{}里面的命令
         */
        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    /*
                    open()方法里面的包括了读写线程
                     */
                    serialHelper.open();//在这里就已经打开了读写线程了
                    btOpen.setEnabled(false);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "msg: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (SecurityException ex) {
                    Toast.makeText(MainActivity.this, "msg: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
         /*
        给发送按钮绑定监听器
         */
        btSend.setOnClickListener(new View.OnClickListener() {
            SimpleDateFormat sDateFormat = new SimpleDateFormat("hh:mm:ss.SSS");
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.radioButton1) {
                    if (edInput.getText().toString().length() > 0) {
                        if (serialHelper.isOpen()) {
                            if (edInput.getText().toString().startsWith("AT"))
                            {
                                serialHelper.sendTxt(edInput.getText().toString().concat("\n"));
                                logListAdapter.addData(sDateFormat.format(new Date())+" Tx:==>"+edInput.getText().toString().concat("\n"));
                            } else if (edInput.getText().toString().equals("+++")) {
                                serialHelper.sendTxt(edInput.getText().toString());
                                logListAdapter.addData(sDateFormat.format(new Date()) + " Tx:==>" + edInput.getText().toString());
                                try {
                                    Thread.sleep(1800);
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                                serialHelper.sendTxt("atk");
                                logListAdapter.addData(sDateFormat.format(new Date()) + " Tx:==>" + "atk");
                            }else
                            {
                                serialHelper.sendTxt(edInput.getText().toString());
                                logListAdapter.addData(sDateFormat.format(new Date()) + " Tx:==>" + edInput.getText().toString());
                            }
                            if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
                                recy.smoothScrollToPosition(logListAdapter.getData().size());//划到最底部，使得数据一直出现
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "串口没打开", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "请填写数据", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (edInput.getText().toString().length() > 0) {
                        if (serialHelper.isOpen()) {
                            if (edInput.getText().toString().startsWith("AT"))
                            {
                                serialHelper.sendHex(edInput.getText().toString().concat("\n"));
                                logListAdapter.addData(sDateFormat.format(new Date())+" Tx:==>" + edInput.getText().toString().concat("\n"));
                            } else if (edInput.getText().toString().equals("+++")) {
                                serialHelper.sendHex(edInput.getText().toString());
                                logListAdapter.addData(sDateFormat.format(new Date()) + " Tx:==>" + edInput.getText().toString());
                                try {
                                    Thread.sleep(1800);
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }
                                serialHelper.sendHex("atk");
                                logListAdapter.addData(sDateFormat.format(new Date()) + " Tx:==>" + "atk");
                            }else
                            {
                                serialHelper.sendHex(edInput.getText().toString());
                                logListAdapter.addData(sDateFormat.format(new Date()) + " Tx:==>" + edInput.getText().toString());
                            }
                            if (logListAdapter.getData() != null && logListAdapter.getData().size() > 0) {
                                recy.smoothScrollToPosition(logListAdapter.getData().size());
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "串口没打开", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), "请先填写数据", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    //这是 Android 应用程序中的一个方法，通常在 Activity 类中定义，用于创建并填充菜单（Menu）的内容。
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //创建一个 MenuInflater 对象 inflater，用于从 XML 文件中加载菜单资源。
        MenuInflater inflater = getMenuInflater();
        //使用 inflater 对象加载指定的菜单资源。这里的 R.menu.main 表示要加载的菜单资源的标识符。
        // 通常，菜单资源会以 XML 文件的形式存储在应用的 res/menu 目录下。
        // inflate 方法会将 XML 文件中定义的菜单项添加到传入的 menu 对象中。
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /*
    这段代码的作用是在用户选择了特定菜单项（"menu_clean"）时，执行与清理操作相关的逻辑。
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clean:
                logListAdapter.clean(); //清空
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
