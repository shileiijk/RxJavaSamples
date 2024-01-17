// (c)2016 Flipboard Inc, All Rights Reserved.

package com.rengwuxian.rxjavasamples.module.elementary_1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.rengwuxian.rxjavasamples.BaseFragment;
import com.rengwuxian.rxjavasamples.databinding.FragmentElementaryBinding;
import com.rengwuxian.rxjavasamples.network.Network;
import com.rengwuxian.rxjavasamples.R;
import com.rengwuxian.rxjavasamples.adapter.ZhuangbiListAdapter;
import com.rengwuxian.rxjavasamples.model.ZhuangbiImage;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ElementaryFragment extends BaseFragment<FragmentElementaryBinding> {
    ZhuangbiListAdapter adapter = new ZhuangbiListAdapter();

    void onTagChecked(CompoundButton searchRb, boolean checked) {
        if (checked) {
            unsubscribe();
            adapter.setImages(null);
            viewBinding.swipeRefreshLayout.setRefreshing(true);
            search(searchRb.getText().toString());
        }
    }

    private void search(String key) {
        disposable = Network.getZhuangbiApi()
                .search(key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ZhuangbiImage>>() {
                    @Override
                    public void accept(@NonNull List<ZhuangbiImage> images) throws Exception {
                        viewBinding.swipeRefreshLayout.setRefreshing(false);
                        adapter.setImages(images);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewBinding.gridRv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        viewBinding.gridRv.setAdapter(adapter);
        viewBinding.swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        viewBinding.swipeRefreshLayout.setEnabled(false);
        viewBinding.searchRb1.setOnCheckedChangeListener(this::onTagChecked);
        viewBinding.searchRb2.setOnCheckedChangeListener(this::onTagChecked);
        viewBinding.searchRb3.setOnCheckedChangeListener(this::onTagChecked);
        viewBinding.searchRb4.setOnCheckedChangeListener(this::onTagChecked);
        viewBinding.tipBt.tipBt.setOnClickListener((v) -> tip());
    }

    @NonNull
    @Override
    public FragmentElementaryBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentElementaryBinding.inflate(inflater, container, false);
    }

    @Override
    protected int getDialogRes() {
        return R.layout.dialog_elementary;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_elementary;
    }
}
