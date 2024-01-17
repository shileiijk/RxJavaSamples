// (c)2016 Flipboard Inc, All Rights Reserved.

package com.rengwuxian.rxjavasamples.module.cache_6;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.rengwuxian.rxjavasamples.BaseFragment;
import com.rengwuxian.rxjavasamples.R;
import com.rengwuxian.rxjavasamples.adapter.ItemListAdapter;
import com.rengwuxian.rxjavasamples.databinding.FragmentCacheBinding;
import com.rengwuxian.rxjavasamples.model.Item;
import com.rengwuxian.rxjavasamples.module.cache_6.data.Data;

import java.util.List;

import io.reactivex.functions.Consumer;

public class CacheFragment extends BaseFragment<FragmentCacheBinding> {
    ItemListAdapter adapter = new ItemListAdapter();
    private long startingTime;

    void clearMemoryCache() {
        Data.getInstance().clearMemoryCache();
        adapter.setItems(null);
        Toast.makeText(getActivity(), R.string.memory_cache_cleared, Toast.LENGTH_SHORT).show();
    }

    void clearMemoryAndDiskCache() {
        Data.getInstance().clearMemoryAndDiskCache();
        adapter.setItems(null);
        Toast.makeText(getActivity(), R.string.memory_and_disk_cache_cleared, Toast.LENGTH_SHORT).show();
    }

    void load() {
        viewBinding.swipeRefreshLayout.setRefreshing(true);
        startingTime = System.currentTimeMillis();
        unsubscribe();
        disposable = Data.getInstance()
                .subscribeData(new Consumer<List<Item>>() {
                    @Override
                    public void accept(@NonNull List<Item> items) throws Exception {
                        viewBinding.swipeRefreshLayout.setRefreshing(false);
                        int loadingTime = (int) (System.currentTimeMillis() - startingTime);
                        viewBinding.loadingTimeTv.setText(getString(R.string.loading_time_and_source, loadingTime, Data.getInstance().getDataSourceText()));
                        adapter.setItems(items);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        viewBinding.swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), R.string.loading_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @NonNull
    @Override
    public FragmentCacheBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentCacheBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewBinding.cacheRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        viewBinding.cacheRv.setAdapter(adapter);
        viewBinding.swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        viewBinding.swipeRefreshLayout.setEnabled(false);
        viewBinding.loadBt.setOnClickListener(v -> load());
        viewBinding.clearMemoryCacheBt.setOnClickListener(v -> clearMemoryCache());
        viewBinding.clearMemoryAndDiskCacheBt.setOnClickListener(v -> clearMemoryAndDiskCache());
        viewBinding.tipBt.tipBt.setOnClickListener((v) -> tip());
    }

    @Override
    protected int getDialogRes() {
        return R.layout.dialog_cache;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_cache;
    }
}
