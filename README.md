### 说明，该库fork自https://github.com/zhihu/RxLifecycle
对原库拓展了指定某个生命周期来终止事件，并将两库（一个是v4支持库）合并了，考虑到可能需要结合自身需求，
所以没有像原库提pr，而是fork下来修改使用。

### 新增用法
```
Observable.interval(0, 2, TimeUnit.SECONDS)
        .compose(RxLifecycle.bind(MainActivity.this).<Long>withObservable(LifecyclePublisher.ON_STOP))
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long n) throws Exception {
                toast("Observable -> " + n.toString());
            }
        });
```
**withObservable**新增参数用于指定某个生命周期。

**withFlowable、withCompletable、withSingle、withMaybe**同理

我是原文分割线
---

# RxLifecycle (non-invasive)

This library is a **non-invasive** version of [RxLifecycle](https://github.com/trello/RxLifecycle). It can help you to automatically complete the observable sequences based on `Activity` or `Fragment`'s lifecycle. There is [an article](https://zhuanlan.zhihu.com/p/24992118) about how it works.

**Supports only RxJava 2 now.**

## Usage

Use the `Transformer`s provided. `bind(your activity or fragment).with(observable type)`.

```
RxLifecycle.bind(activity).withFlowable()
RxLifecycle.bind(activity).withObservable()
RxLifecycle.bind(activity).withCompletable()
RxLifecycle.bind(activity).withSingle()
RxLifecycle.bind(activity).withMaybe()
```

And then compose it to your original observable.

```
Observable.interval(0, 2, TimeUnit.SECONDS)
        .compose(RxLifecycle.bind(MainActivity.this).<Long>withObservable())
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long n) throws Exception {
                toast("Observable -> " + n.toString());
            }
        });
```

**That's all. You needn't to extend your activity or fragment.**

You can also observe the lifecycle events by using the `.asFlowable()` or `.asObservable()` methods to convert the `RxLifecycle` to a `Flowable` or `Observable`.

```
RxLifecycle.bind(this)
        .asFlowable()
        .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@LifecyclePublisher.Event Integer event) throws Exception {
                switch (event) {
                    case LifecyclePublisher.ON_START:
                        toast("Your activity is started.");
                        break;

                    case LifecyclePublisher.ON_STOP:
                        toast("Your activity is stopped.");
                        break;
                }
            }
        });
```

In addition, you can also bind observables to the `FragmentManager` or [`LifecyclePublisher`](rxlifecycle/src/main/java/cn/nekocode/rxlifecycle/LifecyclePublisher.java).

## Sample

Check out the [sample](sample/src/main/java/cn/nekocode/rxlifecycle/sample/MainActivity.java) for more detail.
