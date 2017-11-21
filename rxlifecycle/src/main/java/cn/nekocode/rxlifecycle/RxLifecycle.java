/*
 * Copyright 2016 nekocode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.nekocode.rxlifecycle;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import cn.nekocode.rxlifecycle.transformer.BindLifecycleCompletableTransformer;
import cn.nekocode.rxlifecycle.transformer.BindLifecycleFlowableTransformer;
import cn.nekocode.rxlifecycle.transformer.BindLifecycleMaybeTransformer;
import cn.nekocode.rxlifecycle.transformer.BindLifecycleObservableTransformer;
import cn.nekocode.rxlifecycle.transformer.BindLifecycleSingleTransformer;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.SingleTransformer;

/**
 * @author nekocode (nekocode.cn@gmail.com)
 */
public class RxLifecycle {
    private static final String FRAGMENT_TAG = "_BINDING_FRAGMENT_";
    private final LifecyclePublisher lifecyclePublisher;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static RxLifecycle bind(@NonNull Activity targetActivity) {
        return bind(targetActivity.getFragmentManager());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static RxLifecycle bind(@NonNull Fragment targetFragment) {
        return bind(targetFragment.getChildFragmentManager());
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public static RxLifecycle bind(@NonNull FragmentManager fragmentManager) {
        BindingFragment fragment = (BindingFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new BindingFragment();

            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(fragment, FRAGMENT_TAG);
            transaction.commit();

        } else if (Build.VERSION.SDK_INT >= 13 && fragment.isDetached()) {
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.attach(fragment);
            transaction.commit();
        }

        return bind(fragment.getLifecyclePublisher());
    }

    public static RxLifecycle bind(@NonNull LifecyclePublisher lifecyclePublisher) {
        return new RxLifecycle(lifecyclePublisher);
    }

    private RxLifecycle() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    private RxLifecycle(@NonNull LifecyclePublisher lifecyclePublisher) {
        this.lifecyclePublisher = lifecyclePublisher;
    }

    public Flowable<Integer> asFlowable() {
        return lifecyclePublisher.getBehavior();
    }

    public Observable<Integer> asObservable() {
        return lifecyclePublisher.getBehavior().toObservable();
    }

    public <T> FlowableTransformer<T, T> withFlowable() {
        return withFlowable(LifecyclePublisher.DEFAULT);
    }

    public <T> FlowableTransformer<T, T> withFlowable(@LifecyclePublisher.Event int event) {
        return new BindLifecycleFlowableTransformer<T>(lifecyclePublisher.getBehavior(), event);
    }

    public <T> ObservableTransformer<T, T> withObservable() {
        return withObservable(LifecyclePublisher.DEFAULT);
    }

    public <T> ObservableTransformer<T, T> withObservable(@LifecyclePublisher.Event int event) {
        return new BindLifecycleObservableTransformer<T>(lifecyclePublisher.getBehavior(), event);
    }

    public CompletableTransformer withCompletable() {
        return withCompletable(LifecyclePublisher.DEFAULT);
    }

    public CompletableTransformer withCompletable(@LifecyclePublisher.Event int event) {
        return new BindLifecycleCompletableTransformer(lifecyclePublisher.getBehavior(), event);
    }

    public <T> SingleTransformer<T, T> withSingle() {
        return withSingle(LifecyclePublisher.DEFAULT);
    }

    public <T> SingleTransformer<T, T> withSingle(@LifecyclePublisher.Event int event) {
        return new BindLifecycleSingleTransformer<T>(lifecyclePublisher.getBehavior(), event);
    }

    public <T> MaybeTransformer<T, T> withMaybe() {
        return withMaybe(LifecyclePublisher.DEFAULT);
    }

    public <T> MaybeTransformer<T, T> withMaybe(@LifecyclePublisher.Event int event) {
        return new BindLifecycleMaybeTransformer<T>(lifecyclePublisher.getBehavior(), event);
    }
}
