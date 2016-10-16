package br.com.leonardowistuba.rx.socket;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import static rx.android.MainThreadSubscription.verifyMainThread;

/**
 * Created by leonardowistuba on 10/16/16.
 */
public class FabObservable implements Observable.OnSubscribe<View> {
    final FloatingActionButton mFloatingActionButton;

    public FabObservable(FloatingActionButton floatingActionButton) {
        mFloatingActionButton = floatingActionButton;
    }

    @Override
    public void call(final Subscriber<? super View> subscriber) {
        verifyMainThread();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(view);
                }
            }
        };

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                mFloatingActionButton.setOnClickListener(null);
            }
        });

        mFloatingActionButton.setOnClickListener(onClickListener);
    }
}
