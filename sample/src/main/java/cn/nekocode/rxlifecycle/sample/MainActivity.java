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
package cn.nekocode.rxlifecycle.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import cn.nekocode.rxlifecycle.LifecyclePublisher;
import cn.nekocode.rxlifecycle.RxLifecycle;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tellLifecycleState();

        final Button button1 = (Button) findViewById(R.id.button1);
        final Button button2 = (Button) findViewById(R.id.button2);
        final Button button3 = (Button) findViewById(R.id.button3);
        final Button button4 = (Button) findViewById(R.id.button4);
        final Button button5 = (Button) findViewById(R.id.button5);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testFlowable();
                button1.setEnabled(false);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testObservable();
                button2.setEnabled(false);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testCompletable();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testSingle();
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testMaybe();
            }
        });
    }

    private void tellLifecycleState() {
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
    }

    private void testFlowable() {
        Flowable.interval(0, 2, TimeUnit.SECONDS)
                .compose(RxLifecycle.bind(this).<Long>withFlowable())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long n) throws Exception {
                        toast("Flowable -> " + n.toString());
                    }
                });
    }

    private void testObservable() {
        Observable.interval(0, 2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycle.bind(this).<Long>withObservable(LifecyclePublisher.ON_STOP))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long n) throws Exception {
                        System.out.println("Observable -> " + "-------------------------");
                    }
                });
    }

    private void testCompletable() {
        Completable.timer(3, TimeUnit.SECONDS)
                .compose(RxLifecycle.bind(this).withCompletable())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        toast("Completable -> onComplete()");
                    }
                });
    }

    private void testSingle() {
        Single.timer(3, TimeUnit.SECONDS)
                .compose(RxLifecycle.bind(this).<Long>withSingle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long n) throws Exception {
                        toast("Single -> onSuccess()");
                    }
                });
    }

    private void testMaybe() {
        Maybe.timer(3, TimeUnit.SECONDS)
                .compose(RxLifecycle.bind(this).<Long>withMaybe())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long n) throws Exception {
                        toast("Maybe -> onSuccess()");
                    }
                });
    }

    private void toast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}
