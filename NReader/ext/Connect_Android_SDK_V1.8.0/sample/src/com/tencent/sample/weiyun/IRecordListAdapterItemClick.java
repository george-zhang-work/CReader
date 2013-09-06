package com.tencent.sample.weiyun;


public interface IRecordListAdapterItemClick {

    //查看缩略图
    public void onDeleteClick(int position);
    
    //下载文件
    public void onModifyClick(int position);
    
    //删除云端图片
    public void onGetClick(int position);
}
