package com.ly.rshypoc.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;


import com.ly.rshypoc.R;
import com.ly.rshypoc.bean.TreeNode;
import com.ly.rshypoc.util.TreeHelper;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * @author 郑山
 * @date 2020/4/8
 */

public class TreeAdapter extends BaseRecyclerAdapter<TreeNode> {

    private OnItemClickListener listener;
    /**
     * 当前用户是否为发起人
     */
    private boolean isOwner = false;

    public TreeAdapter(Context context, List<TreeNode> list, boolean isOwner,OnItemClickListener listener) {
        super(context, list, R.layout.item_tree_one, R.layout.item_tree_two, R.layout.item_tree_three);
        this.listener = listener;
        this.isOwner = isOwner;
    }

    @Override
    public int checkLayout(TreeNode item, int position) {
        return item.getLevel();
    }

    @Override
    public void convert(BaseRecyclerHolder holder, TreeNode item, int position) {
        switch (getItemViewType(position)) {
            case 0:
            case 1:
                holder.itemView.setOnClickListener(v -> {
                    onNodeToggled(item);
                    holder.getView(R.id.arrow_img).animate().rotation(item.isExpanded() ? 90 : 0).setDuration(200).start();
                });
                holder.getView(R.id.arrow_img).setRotation(item.isExpanded() ? 90 : 0);
                break;
            case 2:
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "onClick: item.isSelected()111 =" + item.isSelected() + "   isOwner = " + isOwner);
                            selectNode(!item.isSelected(), item);
                            holder.setCheck(R.id.checkBox, item.isSelected());
                            listener.itemClickEnd(item);

                    }
                });
            default:
                break;
        }
        holder.setText(R.id.node_name, item.getValue())
                .setOnClick(R.id.checkBox, v -> {
                    Log.i(TAG, "onClick: item.isSelected() =" + item.isSelected() + "   isOwner = " + isOwner);
                        selectNode(!item.isSelected(), item);
                        if(item.isSelected()){
                        }

                    if (getItemViewType(position)==2){
                        listener.itemClickEnd(item);
                    }else {
                        listener.itemClickNoEnd(item);
                    }
                })
                .setCheck(R.id.checkBox, item.isSelected());

    }

    //是否展开
    private void onNodeToggled(TreeNode treeNode) {
        treeNode.setExpanded(!treeNode.isExpanded());
        if (!treeNode.hasChild()) {
            Log.e("----", "到底了: " + treeNode.getValue());
        }
        if (treeNode.isExpanded()) {
            expandNode(treeNode);
        } else {
            collapseNode(treeNode);
        }
    }

    //展开
    private void expandNode(TreeNode treeNode) {
        if (treeNode == null) {
            return;
        }
        List<TreeNode> additionNodes = TreeHelper.expandNode(treeNode, false);
        int index = getList().indexOf(treeNode);
        if (index < 0 || index > getList().size() - 1 || additionNodes == null) {
            return;
        }
        getList().addAll(index + 1, additionNodes);
        notifyItemRangeInserted(index + 1, additionNodes.size());
    }

    //关闭
    private void collapseNode(TreeNode treeNode) {
        if (treeNode == null) {
            return;
        }
        List<TreeNode> removedNodes = TreeHelper.collapseNode(treeNode, false);
        int index = getList().indexOf(treeNode);
        if (index < 0 || index > getList().size() - 1 || removedNodes == null) {
            return;
        }
        getList().removeAll(removedNodes);
        notifyItemRangeRemoved(index + 1, removedNodes.size());
    }

    //是否选中
    private void selectNode(boolean checked, TreeNode treeNode) {
        Log.i(TAG, "selectNode: 是否选中 checked = " + checked + "   isOwner = " + isOwner);
        if(isOwner){
            treeNode.setSelected(checked);
            selectChildren(treeNode, checked);
            selectParentIfNeed(treeNode, checked);
            Log.i(TAG, "进来");
        }else {
            if(checked){
                treeNode.setSelected(checked);
                selectChildren(treeNode, checked);
                selectParentIfNeed(treeNode, checked);
                Log.i(TAG, "进来1111111");
            }
        }

    }

    //选中子类
    private void selectChildren(TreeNode treeNode, boolean checked) {
        List<TreeNode> impactedChildren = TreeHelper.selectNodeAndChild(treeNode, checked);
        int index = getList().indexOf(treeNode);
        if (index != -1 && impactedChildren.size() > 0) {
            notifyItemRangeChanged(index, impactedChildren.size() + 1);
        }
    }

    //是否全选
    private void selectParentIfNeed(TreeNode treeNode, boolean checked) {
        List<TreeNode> impactedParents = TreeHelper.selectParentIfNeedWhenNodeSelected(treeNode, checked);
        if (impactedParents.size() > 0) {
            for (TreeNode parent : impactedParents) {
                int position = getList().indexOf(parent);
                if (position != -1)
                    notifyItemChanged(position);
            }
        }
    }

    public interface OnItemClickListener {
        /**
         * 点击非末级菜单
         */
        void itemClickNoEnd(TreeNode treeNode);

        /**
         * 点击末级菜单
         */
        void itemClickEnd(TreeNode treeNode);
    }
}
