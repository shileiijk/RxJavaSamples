// (c)2016 Flipboard Inc, All Rights Reserved.

package com.rengwuxian.rxjavasamples.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rengwuxian.rxjavasamples.R;
import com.rengwuxian.rxjavasamples.databinding.GridItemBinding;
import com.rengwuxian.rxjavasamples.model.ZhuangbiImage;

import java.util.List;

public class ZhuangbiListAdapter extends RecyclerView.Adapter<ZhuangbiListAdapter.DebounceViewHolder> {
    List<ZhuangbiImage> images;

    @NonNull
    @Override
    public DebounceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GridItemBinding binding =
                GridItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DebounceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(DebounceViewHolder holder, int position) {
        ZhuangbiImage image = images.get(position);
        Glide.with(holder.itemView.getContext()).load(image.image_url).into(holder.imageIv);
        holder.descriptionTv.setText(image.description);
    }

    @Override
    public int getItemCount() {
        return images == null ? 0 : images.size();
    }

    public void setImages(List<ZhuangbiImage> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    static class DebounceViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIv;
        TextView descriptionTv;

        public DebounceViewHolder(GridItemBinding binding) {
            super(binding.getRoot());
            imageIv = binding.imageIv;
            descriptionTv = binding.descriptionTv;
        }
    }
}
