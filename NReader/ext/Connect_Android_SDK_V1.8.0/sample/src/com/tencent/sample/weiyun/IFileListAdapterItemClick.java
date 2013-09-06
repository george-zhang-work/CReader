package com.tencent.sample.weiyun;


public interface IFileListAdapterItemClick {

    //查看缩略图
    public void onThumbPicClick(int position);
    
    //下载文件
    public void onDownloadClick(int position, int actiontype);
    
    //删除云端图片
    public void onDeleteClick(int position, int actiontype);
}
