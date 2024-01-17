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
import com.rengwuxian.rxjavasamples.databinding.GridItemBinding;
import com.rengwuxian.rxjavasamples.model.Item;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.DebounceViewHolder> {
    List<Item> images;

    @NonNull
    @Override
    public ItemListAdapter.DebounceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GridItemBinding binding =
                GridItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DebounceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ItemListAdapter.DebounceViewHolder holder, int position) {
        Item image = images.get(position);
        Glide.with(holder.itemView.getContext()).load(image.imageUrl).into(holder.imageIv);
        holder.descriptionTv.setText(image.description);
    }

    @Override
    public int getItemCount() {
        return images == null ? 0 : images.size();
    }

    public void setItems(List<Item> images) {
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
