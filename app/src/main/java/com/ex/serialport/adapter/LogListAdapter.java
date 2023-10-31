package com.ex.serialport.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ex.serialport.R;

import java.util.List;


/**
 *
 */
/*
    这段代码是一个 Android 应用中的 RecyclerView 适配器，用于显示串口通信的日志信息。
 */

/*
LogListAdapter 类继承自 BaseQuickAdapter：

BaseQuickAdapter 是一个开源库（Chad Library），用于简化 RecyclerView 适配器的创建和使用。
它提供了一种便捷的方式来创建和配置 RecyclerView 的适配器，减少了大量的样板代码。
 */
public class LogListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    /*
    这个构造函数接收一个 List 参数 list，该参数用于初始化适配器。
    super(R.layout.item_layout, list) 调用父类的构造函数，传递了两个参数：
    R.layout.item_layout：表示每个列表项的布局文件，该布局文件包含了一个 TextView 用于显示日志信息。
    list：用于初始化适配器的数据列表。
     */
    public LogListAdapter(List list) {
        super(R.layout.item_layout, list);
    }

    /*
    convert(BaseViewHolder helper, String item) 方法：
    这个方法用于将数据绑定到 RecyclerView 的每个列表项上。
    helper 参数是一个 BaseViewHolder 对象，用于访问列表项的视图控件。
    item 参数是要显示的日志信息字符串。
    helper.setText(R.id.textView, item) 设置 TextView 控件的文本内容为传入的日志信息 item。
     */
    @Override
    protected void convert(BaseViewHolder helper, String item) {

        helper.setText(R.id.textView, item);

    }

    /**
     * 清空
     */
      /*
    clean() 方法：

    这个方法用于清除适配器中的数据，即清空日志列表。
    this.getData().clear() 清空适配器的数据列表。
    notifyDataSetChanged() 通知适配器数据发生变化，以便刷新 RecyclerView。
     */
    public void clean() {
        this.getData().clear();
        notifyDataSetChanged();
    }

}
