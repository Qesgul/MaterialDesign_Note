package com.trendmicro.materialdesign_note.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trendmicro.materialdesign_note.R;
import com.trendmicro.materialdesign_note.act.EditActivity;
import com.trendmicro.materialdesign_note.act.PhotoViewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zheng_liu on 2018/1/30.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder>  {
    private Context mContext;
    private List<Image> messages;
    private OnRecyclerViewItemClickListener listener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View View;
       ImageView iv_img;

        public ViewHolder(View itemView) {
            super(itemView);
            View = itemView;
            iv_img=itemView.findViewById(R.id.iv_img);
        }
    }
    public List<Image> getImages() {
        //由于图片未选满时，最后一张显示添加图片，因此这个方法返回真正的已选图片
        return new ArrayList<>(messages.subList(1, messages.size()));
    }

    public ImageAdapter(Context mContext,List<Image> message) {
        this.messages=message;
        this.mContext = mContext;
    }
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_image, parent, false);
        final ViewHolder holder=new ImageAdapter.ViewHolder(view);
        // item click
        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageAdapter.ViewHolder holder, int position) {
        Image item = messages.get(position);
        if (position == 0) {
            holder.iv_img.setImageResource(R.drawable.selector_image_add);
        } else {
            displayImage(mContext, item.path, holder.iv_img, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void displayImage(Context context, String path, ImageView imageView, int width, int height) {
        File file = new File(path);
        Glide.with(context)                             //配置上下文
                .load(file)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .error(R.drawable.ic_default_image)           //设置错误图片
                .placeholder(R.drawable.ic_default_image)     //设置占位图片
                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存全尺寸
                .into(imageView);
    }

}
