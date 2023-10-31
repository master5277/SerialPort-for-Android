package com.ex.serialport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ex.serialport.R;

/**
 */

public class SpAdapter extends BaseAdapter {

    String[] datas;//字符串数组
    Context mContext;//成员变量 mContext 用于存储上下文对象，通常是调用适配器的活动或视图的上下文。

    public SpAdapter(Context context) {
        this.mContext = context;
    } //构造函数接受一个上下文对象，并用于初始化适配器。
    /*
    setDatas(String[] datas)：这个方法用于设置要显示的数据，传入一个字符串数组并调用 notifyDataSetChanged() 来通知适配器数据已更改。
     */
    public void setDatas(String[] datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }
    /*
    如果 datas（即数据数组）为 null，则返回 0，表示适配器中没有数据项。
    如果 datas 不为 null，则返回 datas.length，表示适配器中的数据项数量等于数据数组的长度。
     */
    @Override
    public int getCount() {
        return datas == null ? 0 : datas.length;
    }
    //回指定位置的数据项，如果数据为空，则返回 null。
    @Override
    public Object getItem(int position) {
        return datas == null ? null : datas[position];
    }
    //回指定位置的数据项的 ID，通常使用位置作为 ID
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler hodler = null;//下面定义了ViewHolder类
        if (convertView == null) {//convertView 是一个用于重用视图的参数。如果为 null，表示当前没有可重用的视图，需要创建一个新的视图。
            hodler = new ViewHodler();
            /*
            通过 LayoutInflater 从布局资源文件 R.layout.item_layout 中创建一个视图，并将其赋给 convertView。
            从 convertView 中查找 TextView 控件，并将其赋给 hodler.mTextView。
            使用 setTag 方法将 hodler 对象与 convertView 关联，以便在以后的重用中可以快速访问 hodler
             */
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout, null);
            hodler.mTextView = (TextView) convertView;
            convertView.setTag(hodler);
        } else {
            //convertView 中获取之前关联的 hodler 对象，以便重用其中的子视图。
            hodler = (ViewHodler) convertView.getTag();
        }
         /*
        这行代码的作用是将适配器中特定位置 position 的数据项（存储在 datas 数组中）的文本内容
        设置到 hodler 对象中的 mTextView 控件上，从而在视图中显示该文本内容。
         */
        hodler.mTextView.setText(datas[position]);

        return convertView;
    }
    //ViewHodler 内部类：这是一个用于保存视图中子视图的内部类，这样可以避免多次查找子视图，提高了列表的性能。 ViewHodler 类只包含一个 TextView。
    private static class ViewHodler {
        TextView mTextView;
    }
}
