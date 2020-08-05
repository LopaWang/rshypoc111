package com.ly.rshypoc.view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;


import com.ly.rshypoc.R;
import com.ly.rshypoc.adapter.BaseRecyclerAdapter;
import com.ly.rshypoc.util.OnLoadMore;
import com.ly.rshypoc.util.OnRefresh;
import com.ly.rshypoc.util.OnRefreshLoadMore;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

/**
 * @author Administrator
 */
public class XRecyclerView extends LinearLayout {

    private Context mContext;
    private RecyclerView recyclerView;
    private SmartRefreshLayout SRL;
    protected PageLayout mPageLayout;
    public final static int ERROR = 0, Loading = 1, Empty = 2, Hide = 3;
    private RecyclerView.Adapter adapter;
    private View empty;
    private String emptyTxt= "～暂无数据，敬请期待～";


    public XRecyclerView(Context context) {
        this(context, null);
    }

    public XRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public XRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setSmartRefreshLayout();
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.recycler_layout, this, true);
        recyclerView = findViewById(R.id.xRecycler);
        empty = LayoutInflater.from(getContext()).inflate(R.layout.layout_poc_empty, null);
        setEmpty(R.layout.layout_poc_empty);
        SRL = findViewById(R.id.SRL);
        SRL.setEnableRefresh(false);
        SRL.setEnableLoadMore(false);
        //设置在内容不满一页的时候，是否可以上拉加载更多
        SRL.setEnableLoadMoreWhenContentNotFull(false);
        setPageLayout(SRL);
    }

    public void setEmpty(int layoutId) {
        empty = LayoutInflater.from(getContext()).inflate(layoutId, null);
    }

    public void setEmptyText(String txt) {
        mPageLayout.setTxt(txt);
    }

    private void setSmartRefreshLayout() {
        //设置全局的Header
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> new ClassicsHeader(context));
        //设置全局的Footer
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new ClassicsFooter(context));
    }

    /**
     * 设置适配器
     */
    public void setAdapter(RecyclerView.Adapter baseAdapter) {
        if (baseAdapter == null) {
            recyclerView.setAdapter(null);
            return;
        }
        this.adapter = baseAdapter;
        if (adapter.getItemCount() == 0) {
            //显示空布局
            showType(Loading);
        } else {
            //显示正界面
            showType(Hide);
        }
        //默认线性布局
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        }

        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            if (adapter instanceof BaseRecyclerAdapter) {
                ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (((BaseRecyclerAdapter) adapter).isHeaderView(position) ||
                                ((BaseRecyclerAdapter) adapter).isBottomView(position)) ?
                                ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount() : 1;
                    }
                });
            }
        }
        recyclerView.setAdapter(adapter);
    }


    /**
     * 回弹效果
     */
    public void overScroll(boolean isScroll) {
        //是否启用纯滚动模式
        SRL.setEnablePureScrollMode(isScroll);
        //是否启用越界回弹
        SRL.setEnableOverScrollBounce(isScroll);
        //是否启用越界拖动（仿苹果效果）1.0.4
        SRL.setEnableOverScrollDrag(isScroll);
    }

    /**
     * 嵌套滚动功能
     */
    public void setEnableNestedScroll(boolean enabled) {
        SRL.setEnableNestedScroll(enabled);
        SRL.setEnableLoadMoreWhenContentNotFull(enabled);
    }

    /**
     * 设置布局
     */
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
    }

    /**
     * 添加分割线
     */
    public void addItemDecoration(RecyclerView.ItemDecoration decoration) {
        recyclerView.addItemDecoration(decoration);
    }

    /**
     * 刷新加载
     */
    public void setOnRefreshLoadMore(OnRefreshLoadMore loadMore) {
        SRL.setEnableRefresh(true);
        SRL.setEnableLoadMore(true);
        SRL.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore.loadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadMore.refresh();
            }
        });
    }

    public void autoRefresh() {
        SRL.autoRefresh();
    }

    public void autoLoadMore() {
        SRL.autoLoadMore();
    }


    public void stopRefresh() {
        stopRefresh(0);
    }

    public void stopRefresh(int time) {
        SRL.finishRefresh(time);
    }

    public void stopLoadMore() {
        stopLoadMore(0);
    }

    public void stopLoadMore(int time) {
        SRL.finishLoadMore(time);
    }

    public void scrollToPosition(int pos) {
        recyclerView.scrollToPosition(pos);
    }

    /**
     * 刷新
     */
    public void setOnRefresh(OnRefresh listener) {
        SRL.setEnableRefresh(true);
        SRL.setOnRefreshListener(refreshLayout -> {
            listener.refresh();
        });
    }

    /**
     * 加载
     */
    public void setLoadMore(OnLoadMore listener) {
        SRL.setEnableLoadMore(true);
        SRL.setOnLoadMoreListener(refreshLayout -> {
            listener.loadMore();
        });
    }

    /**
     * 没有加载更多
     */
    public void setNoMore() {
        SRL.finishLoadMoreWithNoMoreData();
        notifyDataSetChanged();
    }

    /**
     * 恢复没有更多数据的原始状态
     */
    public void resetNoMoreData() {
        SRL.resetNoMoreData();
    }

    private void setPageLayout(View view) {
        //默认布局
        mPageLayout = new PageLayout.Builder(mContext)
                .initPage(view)
                .setOnEmptyListener(this::setOnRetry)
                //错误布局点击事件--重新刷新
                .setOnRetryListener(this::setOnRetry)
                .create();
        showType(Loading);
    }

    public void notifyDataSetChanged() {
        if (adapter.getItemCount() == 0) {
            //显示空布局
            showType(Empty);
        } else {
            if (adapter instanceof BaseRecyclerAdapter) {
                if (((BaseRecyclerAdapter) adapter).getList().size() == 0) {
                    if (!((BaseRecyclerAdapter) adapter).isAddView(empty)) {
                        ((BaseRecyclerAdapter) adapter).addFooterView(empty, 0);
                    }
                } else {
                    ((BaseRecyclerAdapter) adapter).removeFooterView(empty);
                }
            }
            //显示正界面
            showType(Hide);
            adapter.notifyDataSetChanged();
        }
        stopRefresh();
        stopLoadMore();
    }

    /**
     * 错误布局点击
     */
    public void setOnRetry() {
        SRL.autoRefresh();
        showType(Loading);
    }

    public void showError() {
        if (mPageLayout == null) {
            return;
        }
        mPageLayout.showError();
        stopRefresh();
        stopLoadMore();
    }

    public void showEmpty() {
        if (mPageLayout == null) {
            return;
        }
        mPageLayout.showEmpty();
        stopRefresh();
        stopLoadMore();
    }

    public void showLoading() {
        if (mPageLayout == null) {
            return;
        }
        mPageLayout.showLoading();
    }

    public void hide() {
        if (mPageLayout == null) {
            return;
        }
        mPageLayout.hide();
    }


    public void showType(int type) {
        if (mPageLayout == null) {
            return;
        }
        switch (type) {
            case ERROR:
                mPageLayout.showError();
                break;
            case Loading:
                mPageLayout.showLoading();
                break;
            case Empty:
                mPageLayout.showEmpty();
                break;
            case Hide:
                mPageLayout.hide();
                break;
            default:
                break;
        }
    }
}
