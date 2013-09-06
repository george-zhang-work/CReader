package com.tencent.sample.weiyun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.tencent.sample.R;
import com.tencent.sample.Util;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: melody
 * Date: 13-4-27
 * Time: 下午4:33
 * To change this template use File | Settings | File Templates.
 */
public class RecordListAdapter extends BaseAdapter {

    public List<RecordItem> record_list;
    Context context;
    private IRecordListAdapterItemClick mItemClick;

    public RecordListAdapter(Context ctx, IRecordListAdapterItemClick itemClick){
        context=ctx;
        mItemClick=itemClick;
    }

    @Override
    public Object getItem(int arg0) {
        if (record_list == null) {
            return null;
        } else {
            return record_list.get(arg0);
        }
    }

    @Override
    public int getCount(){
        if (record_list == null) {
            return 0;
        } else {
            return record_list.size();
        }
    }

    //ViewHolder静态类
    static class ViewHolder
    {
        public TextView keyText;
        public Button delBtn;
        public Button modifyBtn;
        public Button getBtn;
    }

    @Override
    public long getItemId(int arg0) {
        if (record_list == null) {
            return 0;
        } else {
            return arg0;
        }
    }

    @Override
    public View getView(final int arg0, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        final RecordItem item=record_list.get(arg0);
        if(arg1 == null){
            arg1 = LayoutInflater.from(context).inflate(R.layout.weiyun_recordlist_item, null);
            viewHolder=new ViewHolder();
            viewHolder.keyText=(TextView)arg1.findViewById(R.id.key);
            viewHolder.delBtn=(Button)arg1.findViewById(R.id.del);
            viewHolder.modifyBtn=(Button)arg1.findViewById(R.id.modify);
            viewHolder.getBtn=(Button)arg1.findViewById(R.id.get);
            arg1.setTag(viewHolder);
        }
        else{
            viewHolder=(ViewHolder)arg1.getTag();
        }
        viewHolder.keyText.setText(Util.hexToString(item.key));
        viewHolder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClick.onDeleteClick(arg0);
            }
        });
        viewHolder.modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClick.onModifyClick(arg0);
            }
        });
        viewHolder.getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClick.onGetClick(arg0);
            }
        });
        return arg1;
    }

    public static class RecordItem{
        public String key;
        public String value;
    }

}
