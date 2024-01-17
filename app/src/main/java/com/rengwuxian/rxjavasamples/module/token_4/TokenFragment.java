// (c)2016 Flipboard Inc, All Rights Reserved.

package com.rengwuxian.rxjavasamples.module.token_4;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rengwuxian.rxjavasamples.BaseFragment;
import com.rengwuxian.rxjavasamples.databinding.FragmentTokenBinding;
import com.rengwuxian.rxjavasamples.network.Network;
import com.rengwuxian.rxjavasamples.R;
import com.rengwuxian.rxjavasamples.network.api.FakeApi;
import com.rengwuxian.rxjavasamples.model.FakeThing;
import com.rengwuxian.rxjavasamples.model.FakeToken;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class TokenFragment extends BaseFragment<FragmentTokenBinding> {
    void upload() {
        viewBinding.swipeRefreshLayout.setRefreshing(true);
        unsubscribe();
        final FakeApi fakeApi = Network.getFakeApi();
        disposable = fakeApi.getFakeToken("fake_auth_code")
                .flatMap(new Function<FakeToken, Observable<FakeThing>>() {
                    @Override
                    public Observable<FakeThing> apply(FakeToken fakeToken) {
                        return fakeApi.getFakeData(fakeToken);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FakeThing>() {
                    @Override
                    public void accept(FakeThing fakeData) {
                        viewBinding.swipeRefreshLayout.setRefreshing(false);
                        viewBinding.tokenTv.setText(getString(R.string.got_data, fakeData.id, fakeData.name));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        viewBinding.swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), R.string.loading_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewBinding.swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        viewBinding.swipeRefreshLayout.setEnabled(false);
        viewBinding.requestBt.setOnClickListener(v -> upload());
        viewBinding.tipBt.tipBt.setOnClickListener((v) -> tip());
    }

    @NonNull
    @Override
    public FragmentTokenBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentTokenBinding.inflate(inflater, container, false);
    }

    @Override
    protected int getDialogRes() {
        return R.layout.dialog_token;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_token;
    }
}
