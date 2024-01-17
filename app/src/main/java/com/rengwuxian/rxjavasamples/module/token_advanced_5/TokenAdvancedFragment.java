// (c)2016 Flipboard Inc, All Rights Reserved.

package com.rengwuxian.rxjavasamples.module.token_advanced_5;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rengwuxian.rxjavasamples.BaseFragment;
import com.rengwuxian.rxjavasamples.databinding.FragmentTokenAdvancedBinding;
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

public class TokenAdvancedFragment extends BaseFragment<FragmentTokenAdvancedBinding> {
    final FakeToken cachedFakeToken = new FakeToken(true);
    boolean tokenUpdated;

    void invalidateToken() {
        cachedFakeToken.expired = true;
        Toast.makeText(getActivity(), R.string.token_destroyed, Toast.LENGTH_SHORT).show();
    }

    void upload() {
        tokenUpdated = false;
        viewBinding.swipeRefreshLayout.setRefreshing(true);
        unsubscribe();
        final FakeApi fakeApi = Network.getFakeApi();
        disposable = Observable.just(1)
                .flatMap(new Function<Object, Observable<FakeThing>>() {
                    @Override
                    public Observable<FakeThing> apply(Object o) {
                        return cachedFakeToken.token == null
                                ? Observable.<FakeThing>error(new NullPointerException("Token is null!"))
                                : fakeApi.getFakeData(cachedFakeToken);
                    }
                })
                .retryWhen(new Function<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> apply(Observable<? extends Throwable> observable) {
                        return observable.flatMap(new Function<Throwable, Observable<?>>() {
                            @Override
                            public Observable<?> apply(Throwable throwable) {
                                if (throwable instanceof IllegalArgumentException || throwable instanceof NullPointerException) {
                                    return fakeApi.getFakeToken("fake_auth_code")
                                            .doOnNext(new Consumer<FakeToken>() {
                                                @Override
                                                public void accept(FakeToken fakeToken) {
                                                    tokenUpdated = true;
                                                    cachedFakeToken.token = fakeToken.token;
                                                    cachedFakeToken.expired = fakeToken.expired;
                                                }
                                            });
                                }
                                return Observable.error(throwable);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FakeThing>() {
                    @Override
                    public void accept(FakeThing fakeData) {
                        viewBinding.swipeRefreshLayout.setRefreshing(false);
                        String token = cachedFakeToken.token;
                        if (tokenUpdated) {
                            token += "(" + getString(R.string.updated) + ")";
                        }
                        viewBinding.tokenTv.setText(getString(R.string.got_token_and_data, token, fakeData.id, fakeData.name));
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
        viewBinding.invalidateTokenBt.setOnClickListener(v -> invalidateToken());
        viewBinding.requestBt.setOnClickListener(v -> upload());
        viewBinding.tipBt.tipBt.setOnClickListener((v) -> tip());
    }

    @NonNull
    @Override
    public FragmentTokenAdvancedBinding getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentTokenAdvancedBinding.inflate(inflater, container, false);
    }

    @Override
    protected int getDialogRes() {
        return R.layout.dialog_token_advanced;
    }

    @Override
    protected int getTitleRes() {
        return R.string.title_token_advanced;
    }
}
