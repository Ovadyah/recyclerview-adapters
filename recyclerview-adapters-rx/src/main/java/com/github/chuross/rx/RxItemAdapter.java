package com.github.chuross.rx;


import android.content.Context;

import com.github.chuross.recyclerviewadapters.BaseItemAdapter;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public abstract class RxItemAdapter<I, VH extends RecyclerView.ViewHolder> extends BaseItemAdapter<I, VH> {

    private Disposable disposable;
    private Flowable<List<I>> flowable;
    private OnItemDataChangedListener itemDataChangedListener;
    private List<I> cachedItems = Collections.emptyList();

    public RxItemAdapter(@NonNull Context context, @NonNull Flowable<List<I>> flowable) {
        super(context);
        this.flowable = flowable;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        disposable = flowable.filter(new Predicate<List<I>>() {
            @Override
            public boolean test(List<I> is) throws Exception {
                return isAttached();
            }
        }).subscribe(new Consumer<List<I>>() {
            @Override
            public void accept(List<I> is) throws Exception {
                cachedItems = is;
                notifyDataSetChanged();
                if (itemDataChangedListener != null) itemDataChangedListener.onDataChanged();
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (disposable != null) disposable.dispose();
        if (cachedItems != null) cachedItems = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return cachedItems.size();
    }

    @NonNull
    @Override
    public I get(int position) {
        return cachedItems.get(position);
    }

    public void setOnDataSetChangedListener(OnItemDataChangedListener listener) {
        itemDataChangedListener = listener;
    }
}
