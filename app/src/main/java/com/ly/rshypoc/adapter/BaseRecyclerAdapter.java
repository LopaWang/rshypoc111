package com.ly.rshypoc.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.ly.rshypoc.util.AnimationType;
import com.ly.rshypoc.util.AnimationUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolder> {
    public Context context;
    private List<T> list;
    private LayoutInflater inflater;
    private int[] itemLayoutId;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;
    private RecyclerView recyclerView;
    private DisplayMetrics dm;

    private LinearLayout mHeaderLayout;
    private LinearLayout mFooterLayout;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }


    public int addFooterView(View footer) {
        return addFooterView(footer, -1, LinearLayout.VERTICAL);
    }

    public int addFooterView(View footer, int index) {
        return addFooterView(footer, index, LinearLayout.VERTICAL);
    }


    public int addFooterView(View footer, int index, int orientation) {
        if (mFooterLayout == null) {
            mFooterLayout = new LinearLayout(footer.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mFooterLayout.setOrientation(LinearLayout.VERTICAL);
                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mFooterLayout.setOrientation(LinearLayout.HORIZONTAL);
                mFooterLayout.setLayoutParams(new RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        final int childCount = mFooterLayout.getChildCount();
        if (index < 0 || index > childCount) {
            index = childCount;
        }
        mFooterLayout.addView(footer, index);
        if (mFooterLayout.getChildCount() == 1) {
            int position = getFooterViewPosition();
            if (position != -1) {
                notifyItemInserted(position);
            }
        }
        return index;
    }

    private int getFooterViewPosition() {
        return getHeaderLayoutCount() + list.size();
    }

    public int getHeaderLayoutCount() {
        if (mHeaderLayout == null || mHeaderLayout.getChildCount() == 0) {
            return 0;
        }
        return 1;
    }

    public int addHeaderView(View header) {
        return addHeaderView(header, -1);
    }


    public int addHeaderView(View header, int index) {
        return addHeaderView(header, index, LinearLayout.VERTICAL);
    }

    public int addHeaderView(View header, final int index, int orientation) {
        if (mHeaderLayout == null) {
            mHeaderLayout = new LinearLayout(header.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                mHeaderLayout.setOrientation(LinearLayout.VERTICAL);
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                mHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
                mHeaderLayout.setLayoutParams(new RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        final int childCount = mHeaderLayout.getChildCount();
        int mIndex = index;
        if (index < 0 || index > childCount) {
            mIndex = childCount;
        }
        mHeaderLayout.addView(header, mIndex);
        if (mHeaderLayout.getChildCount() == 1) {
            int position = 0;
            notifyItemInserted(position);
        }
        return mIndex;
    }

    public int getFooterLayoutCount() {
        if (mFooterLayout == null || mFooterLayout.getChildCount() == 0) {
            return 0;
        }
        return 1;
    }

    /**
     * 同时显示 empty 和 footer时 此方法会先移除empty
     */
    public void removeFooterView() {
        if (getFooterLayoutCount() == 0) {
            return;
        }
        mFooterLayout.removeView(mFooterLayout.getChildAt(0));
        if (mFooterLayout.getChildCount() == 0) {
            int position = getFooterViewPosition();
            if (position != -1) {
                notifyItemRemoved(position);
            }
        }
    }

    public void removeFooterView(View footer) {
        if (getFooterLayoutCount() == 0) {
            return;
        }
        mFooterLayout.removeView(footer);
        if (mFooterLayout.getChildCount() == 0) {
            int position = getFooterViewPosition();
            if (position != -1) {
                notifyItemRemoved(position);
            }
        }
    }

    public boolean isAddView(View view) {
        if (mFooterLayout != null) {
            int footerIndex = mFooterLayout.indexOfChild(view);
            return footerIndex != -1;
        }
        return false;
    }


    /**
     * 判断当前item是否是HeadView
     */
    public boolean isHeaderView(int position) {
        return getHeaderLayoutCount() != 0 && position < getHeaderLayoutCount();
    }

    /**
     * 判断当前item是否是FooterView
     */
    public boolean isBottomView(int position) {
        return getFooterLayoutCount() != 0 && position >= (getHeaderLayoutCount() + list.size());
    }

    /**
     * 定义一个点击事件接口回调
     */
    public interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(RecyclerView parent, View view, int position);
    }

    public void setData(List<T> list) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 插入一项
     */
    public void addData(T item, int position) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(position, item);
        notifyItemInserted(position + getHeaderLayoutCount());
        compatibilityDataSizeChanged(1);
    }

    public void addData(@NonNull Collection<? extends T> newData) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.addAll(newData);
        notifyDataSetChanged();
//        notifyItemRangeInserted(list.size() - newData.size() + getHeaderLayoutCount(), newData.size());
//        compatibilityDataSizeChanged(newData.size());
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public List<T> getList() {
        return list;
    }


    /**
     * 删除一项
     *
     * @param position 删除位置
     */
    public void delete(@IntRange(from = 0) int position) {
        list.remove(position);
        notifyItemRemoved(position);
        compatibilityDataSizeChanged(0);
        notifyItemRangeChanged(position, list.size() - position);
    }

    private void compatibilityDataSizeChanged(int size) {
        final int dataSize = list == null ? 0 : list.size();
        if (dataSize == size) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int numHeaderView = getHeaderLayoutCount();
        if (position < numHeaderView) {
            return ViewTypeSpec.makeItemViewTypeSpec(position, ViewTypeSpec.HEADER);
        }
        final int adjPosition = position - numHeaderView;
        final int itemCount = list.size();

        if (adjPosition >= itemCount) {
            return ViewTypeSpec.makeItemViewTypeSpec(adjPosition - itemCount, ViewTypeSpec.FOOTER);
        }
        return checkLayout(list.get(adjPosition), adjPosition);
    }


    /**
     * 多条目类型的时候要重写这个方法,默认第0个布局
     */
    public int checkLayout(T item, int position) {
        return 0;
    }

    public BaseRecyclerAdapter(Context context, List<T> list, int... itemLayoutId) {
        this.context = context;
        this.list = list;
        this.itemLayoutId = itemLayoutId;
        inflater = LayoutInflater.from(context);
        dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    public BaseRecyclerAdapter(Context context, int... itemLayoutId) {
        this.context = context;
        this.list = new ArrayList<>();
        this.itemLayoutId = itemLayoutId;
        inflater = LayoutInflater.from(context);
        dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public BaseRecyclerHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        final int type = ViewTypeSpec.getType(viewType);
        final int value = ViewTypeSpec.getValue(viewType);
        if (type == ViewTypeSpec.HEADER) {
            return BaseRecyclerHolder.getRecyclerHolder(context, mHeaderLayout);
        }
        if (type == ViewTypeSpec.FOOTER) {
            return BaseRecyclerHolder.getRecyclerHolder(context, mFooterLayout);
        }
        View view = inflater.inflate(itemLayoutId[viewType], parent, false);
        return BaseRecyclerHolder.getRecyclerHolder(context, view);
    }

    @Override
    public void onBindViewHolder( BaseRecyclerHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            if (listener != null && view != null && recyclerView != null) {
                int position1 = position - getHeaderLayoutCount();
                if (position1 >= 0 && position1 < getList().size()) {
                    listener.onItemClick(recyclerView, view, position1);
                }
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (longClickListener != null && view != null && recyclerView != null) {
                int position12 = position - getHeaderLayoutCount();
                if (position12 >= 0 && position12 < getList().size()) {
                    longClickListener.onItemLongClick(recyclerView, view, position12);
                }
                return true;
            }
            return false;
        });
        final int adjPosition = position - getHeaderLayoutCount();
        if (ViewTypeSpec.getType(getItemViewType(position)) != ViewTypeSpec.HEADER && ViewTypeSpec.getType(getItemViewType(position)) != ViewTypeSpec.FOOTER) {
            if (list.get(adjPosition) != null) {
                convert(holder, list.get(adjPosition), adjPosition);
            }
        }
        addAnimation(holder);
    }

    private AnimationType mAnimationType;
    private int mAnimationDuration = 300;
    private boolean showItemAnimationEveryTime = false;

    /**
     * 设置动画样式
     */
    public void setItemAnimation(AnimationType animationType) {
        mAnimationType = animationType;
    }

    /**
     * 动画时间
     */
    public void setItemAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
    }

    /**
     * 是否每次都会执行动画,默认是false
     */
    public void setShowItemAnimationEveryTime(boolean showItemAnimationEveryTime) {
        this.showItemAnimationEveryTime = showItemAnimationEveryTime;
    }

    private void addAnimation(final BaseRecyclerHolder holder) {
        if (mAnimationType != null && showItemAnimationEveryTime) {
            new AnimationUtil()
                    .setAnimationType(mAnimationType)
                    .setTargetView(holder.itemView)
                    .setDuration(mAnimationDuration)
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderLayoutCount() + getFooterLayoutCount() + list.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    static class ViewTypeSpec {
        static final int TYPE_SHIFT = 30;
        static final int TYPE_MASK = 0x3 << TYPE_SHIFT;

        static final int UNSPECIFIED = 0;
        static final int HEADER = 1 << TYPE_SHIFT;
        static final int FOOTER = 2 << TYPE_SHIFT;

        @IntDef({UNSPECIFIED, HEADER, FOOTER})
        @Retention(RetentionPolicy.SOURCE)
        @interface ViewTypeSpecMode {
        }

        static int makeItemViewTypeSpec(@IntRange(from = 0, to = (1 << TYPE_SHIFT) - 1) int value, @ViewTypeSpecMode int type) {
            return (value & ~TYPE_MASK) | (type & TYPE_MASK);
        }

        static int getType(int viewType) {
            return (viewType & TYPE_MASK);
        }

        static int getValue(int viewType) {
            return (viewType & ~TYPE_MASK);
        }
    }


    /**
     * 填充RecyclerView适配器的方法，子类需要重写
     *
     * @param holder   ViewHolder
     * @param item     子项
     * @param position 位置
     */
    public abstract void convert(BaseRecyclerHolder holder, T item, int position);
}
