// (c)2016 Flipboard Inc, All Rights Reserved.

package com.rengwuxian.rxjavasamples.module.zip_3;

import android.graphics.Color;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.rengwuxian.rxjavasamples.BaseFragment;
import com.rengwuxian.rxjavasamples.databinding.FragmentZipBinding;
import com.rengwuxian.rxjavasamples.network.Network;
import com.rengwuxian.rxjavasamples.R;
import com.rengwuxian.rxjavasamples.adapter.ItemListAdapter;
import com.rengwuxian.rxjavasamples.model.Item;
import com.rengwuxian.rxjavasamples.model.ZhuangbiImage;
import com.rengwuxian.rxjavasamples.util.GankBeautyResultToItemsMapper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ZipFragment extends BaseFragment<FragmentZipBinding> {
    ItemListAdapter adapter = new ItemListAdapter();

    void load() {
        viewBinding.swipeRefreshLayout.setRefreshing(true);
        unsubscribe();
        disposable = Observable
                .zip(Network.getGankApi().getBeauties(200, 1).map(GankBeautyResultToItemsMapper.getInstance()), Network.getZhuangbiApi().search("装逼"),
                        new BiFunction<List<Item>, List<ZhuangbiImage>, List<Item>>() {
                            @Override
                            public List<Item> apply(List<Item> gankItems, List<ZhuangbiImage> zhuangbiImages) {
                                List<Item> items = new ArrayList<Item>();
                                for (int i = 0; i < gankItems.size() / 2 && i < zhuangbiImages.size(); i++) {
                                    items.add(gankItems.get(i * 2));
                                    items.add(gankItems.get(i * 2 + 1));
                                    Item zhuangbiItem = new Item();
                                    ZhuangbiImage zhuangbiImage = zhuangbiImages.get(i);
                                    zhuangbiItem.description = zhuangbiImage.description;
                                    zhuangbiItem.imageUrl = zhuangbiImage.image_url;
                                    items.add(zhuangbiItem);
                                }
                                return items;
                            }
                        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Item>>() {
                    @Override
                    public void accept(@NonNull List<Item> items) throws Exception {
                        viewBinding.swipeRefreshLayout.setRefreshing(false);
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
        viewBinding.gridRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        viewBinding.gridRv.setAdapter(adapter);
        viewBinding.swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        viewBinding.swipeRefreshLayout.setEnabled(false);
        viewBinding.zipLoadBt.setOnClickListener((v) -> load());
        viewBinding.tipBt.tipBt.setOnClickListener((v) -> tip());
    }

    @androidx.annotation.NonNull
    @Override
    public FragmentZipBinding getBinding(@androidx.annotation.NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentZipBinding.inflate(inflater, container, false);
    }

    @Override
    protected int getDialogRes() {
        return R.layout.dialog_zip;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_zip;
    }
}
