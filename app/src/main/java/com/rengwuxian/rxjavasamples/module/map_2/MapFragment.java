// (c)2016 Flipboard Inc, All Rights Reserved.

package com.rengwuxian.rxjavasamples.module.map_2;

import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.rengwuxian.rxjavasamples.BaseFragment;
import com.rengwuxian.rxjavasamples.databinding.FragmentMapBinding;
import com.rengwuxian.rxjavasamples.network.Network;
import com.rengwuxian.rxjavasamples.R;
import com.rengwuxian.rxjavasamples.adapter.ItemListAdapter;
import com.rengwuxian.rxjavasamples.model.Item;
import com.rengwuxian.rxjavasamples.util.GankBeautyResultToItemsMapper;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MapFragment extends BaseFragment<FragmentMapBinding> {
    private int page = 0;

    ItemListAdapter adapter = new ItemListAdapter();

    void previousPage() {
        loadPage(--page);
        if (page == 1) {
            viewBinding.previousPageBt.setEnabled(false);
        }
    }

    void nextPage() {
        loadPage(++page);
        if (page == 2) {
            viewBinding.previousPageBt.setEnabled(true);
        }
    }

    private void loadPage(int page) {
        viewBinding.swipeRefreshLayout.setRefreshing(true);
        unsubscribe();
        disposable = Network.getGankApi()
                .getBeauties(10, page)
                .map(GankBeautyResultToItemsMapper.getInstance())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Item>>() {
                    @Override
                    public void accept(@NonNull List<Item> items) throws Exception {
                        viewBinding.swipeRefreshLayout.setRefreshing(false);
                        viewBinding.pageTv.setText(getString(R.string.page_with_number, MapFragment.this.page));
                        adapter.setItems(items);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        viewBinding.swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), R.string.loading_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onViewCreated(@androidx.annotation.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewBinding.gridRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        viewBinding.gridRv.setAdapter(adapter);
        viewBinding.swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        viewBinding.swipeRefreshLayout.setEnabled(false);
        viewBinding.previousPageBt.setOnClickListener(v -> previousPage());
        viewBinding.nextPageBt.setOnClickListener(v -> nextPage());
        viewBinding.tipBt.tipBt.setOnClickListener((v) -> tip());
    }

    @NonNull
    @Override
    public FragmentMapBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentMapBinding.inflate(inflater, container, false);
    }

    @Override
    protected int getDialogRes() {
        return R.layout.dialog_map;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_map;
    }
}
