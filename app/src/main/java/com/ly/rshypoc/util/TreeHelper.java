/*
 * Copyright 2016 - 2017 ShineM (Xinyuan)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under.
 */

package com.ly.rshypoc.util;


import com.ly.rshypoc.bean.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 树形目录
 */
public class TreeHelper {

    /**
     * 展开全部
     * */
    public static void expandAll(TreeNode node) {
        if (node == null) {
            return;
        }
        expandNode(node, true);
    }

    /**
     * 展开节点并计算可见的添加节点。
     * @param treeNode     目标节点
     * @param includeChild 是否扩展子类
     */
    public static List<TreeNode> expandNode(TreeNode treeNode, boolean includeChild) {
        List<TreeNode> expandChildren = new ArrayList<>();

        if (treeNode == null) {
            return expandChildren;
        }

        treeNode.setExpanded(true);

        if (!treeNode.hasChild()) {
            return expandChildren;
        }

        for (TreeNode child : treeNode.getChildren()) {
            expandChildren.add(child);

            if (includeChild || child.isExpanded()) {
                expandChildren.addAll(expandNode(child, includeChild));
            }
        }

        return expandChildren;
    }

    /**
     * 展开相同的深度(级别)节点。
     *
     * @param root  资源
     * @param level 展开级别
     */
    public static void expandLevel(TreeNode root, int level) {
        if (root == null) {
            return;
        }

        for (TreeNode child : root.getChildren()) {
            if (child.getLevel() == level) {
                expandNode(child, false);
            } else {
                expandLevel(child, level);
            }

        }
    }
    /**
     * 关闭全部
     * @param node  资源
     */
    public static void collapseAll(TreeNode node) {
        if (node == null) {
            return;
        }
        for (TreeNode child : node.getChildren()) {
            performCollapseNode(child, true);
        }
    }

    /**
     * 折叠节点并计算可见的删除节点。
     *
     * @param node
     * @param includeChild
     */
    public static List<TreeNode> collapseNode(TreeNode node, boolean includeChild) {
        List<TreeNode> treeNodes = performCollapseNode(node, includeChild);
        node.setExpanded(false);
        return treeNodes;
    }

    private static List<TreeNode> performCollapseNode(TreeNode node, boolean includeChild) {
        List<TreeNode> collapseChildren = new ArrayList<>();

        if (node == null) {
            return collapseChildren;
        }
        if (includeChild) {
            node.setExpanded(false);
        }
        for (TreeNode child : node.getChildren()) {
            collapseChildren.add(child);

            if (child.isExpanded()) {
                collapseChildren.addAll(performCollapseNode(child, includeChild));
            } else if (includeChild) {
                performCollapseNodeInner(child);
            }
        }

        return collapseChildren;
    }

    /**
     * 折叠所有子节点递归
     *
     * @param node target node to collapse
     */
    private static void performCollapseNodeInner(TreeNode node) {
        if (node == null) {
            return;
        }
        node.setExpanded(false);
        for (TreeNode child : node.getChildren()) {
            performCollapseNodeInner(child);
        }
    }
    /**
     * 关闭节点。
     *
     * @param root  资源
     * @param level 级别
     */
    public static void collapseLevel(TreeNode root, int level) {
        if (root == null) {
            return;
        }
        for (TreeNode child : root.getChildren()) {
            if (child.getLevel() == level) {
                collapseNode(child, false);
            } else {
                collapseLevel(child, level);
            }
        }
    }

    /**
     * 所有节点
     * */
    public static List<TreeNode> getAllNodes(TreeNode root) {
        List<TreeNode> allNodes = new ArrayList<>();
        fillNodeList(allNodes, root);
        allNodes.remove(root);
        return allNodes;
    }

    private static void fillNodeList(List<TreeNode> treeNodes, TreeNode treeNode) {
        treeNodes.add(treeNode);
        if (treeNode.hasChild()) {
            for (TreeNode child : treeNode.getChildren()) {
                fillNodeList(treeNodes, child);
            }
        }
    }

    /**
     * 选择节点和节点的子节点，返回可见节点
     *
     * @param treeNode
     * @param select
     * @return
     */
    public static List<TreeNode> selectNodeAndChild(TreeNode treeNode, boolean select) {
        List<TreeNode> expandChildren = new ArrayList<>();

        if (treeNode == null) {
            return expandChildren;
        }

        treeNode.setSelected(select);

        if (!treeNode.hasChild()) {
            return expandChildren;
        }

        if (treeNode.isExpanded()) {
            for (TreeNode child : treeNode.getChildren()) {
                expandChildren.add(child);

                if (child.isExpanded()) {
                    expandChildren.addAll(selectNodeAndChild(child, select));
                } else {
                    selectNodeInner(child, select);
                }
            }
        } else {
            selectNodeInner(treeNode, select);
        }
        return expandChildren;
    }

    private static void selectNodeInner(TreeNode treeNode, boolean select) {
        if (treeNode == null) {
            return;
        }
        treeNode.setSelected(select);
        if (treeNode.hasChild()) {
            for (TreeNode child : treeNode.getChildren()) {
                selectNodeInner(child, select);
            }
        }
    }

    /**
     * 当所有兄弟被选中时选择父，否则取消选择父，检查父递归。
     *
     * @param treeNode
     * @param select
     * @return
     */
    public static List<TreeNode> selectParentIfNeedWhenNodeSelected(TreeNode treeNode, boolean select) {
        List<TreeNode> impactedParents = new ArrayList<>();
        if (treeNode == null) {
            return impactedParents;
        }

        //ensure that the node's level is bigger than 1(first level is 1)
        TreeNode parent = treeNode.getParent();
        if (parent == null || parent.getParent() == null) {
            return impactedParents;
        }

        List<TreeNode> brothers = parent.getChildren();
        int selectedBrotherCount = 0;
        for (TreeNode brother : brothers) {
            if (brother.isSelected()) selectedBrotherCount++;
        }

        if (select && selectedBrotherCount == brothers.size()) {
            parent.setSelected(true);
            impactedParents.add(parent);
            impactedParents.addAll(selectParentIfNeedWhenNodeSelected(parent, true));
        } else if (!select && selectedBrotherCount == brothers.size() - 1) {
            // only the condition that the size of selected's brothers
            // is one less than total count can trigger the deselect
            parent.setSelected(false);
            impactedParents.add(parent);
            impactedParents.addAll(selectParentIfNeedWhenNodeSelected(parent, false));
        }
        return impactedParents;
    }

    /**
     * 获取当前节点下的选中节点，包括自身
     * @param treeNode
     */
    public static List<TreeNode> getSelectedNodes(TreeNode treeNode) {
        List<TreeNode> selectedNodes = new ArrayList<>();
        if (treeNode == null) {
            return selectedNodes;
        }

        if (treeNode.isSelected() && treeNode.getParent() != null) selectedNodes.add(treeNode);

        for (TreeNode child : treeNode.getChildren()) {
            selectedNodes.addAll(getSelectedNodes(child));
        }
        return selectedNodes;
    }

    /**
     *在当前节点下获取选中的节点，当节点至少有一个选中的子节点(递归所有子节点)时，包含它自己freturn true，否则返回false
     *
     * @param treeNode
     * @return
     */
    public static boolean hasOneSelectedNodeAtLeast(TreeNode treeNode) {
        if (treeNode == null || treeNode.getChildren().size() == 0) {
            return false;
        }
        List<TreeNode> children = treeNode.getChildren();
        for (TreeNode child : children) {
            if (child.isSelected() || hasOneSelectedNodeAtLeast(child)) {
                return true;
            }
        }
        return false;
    }
}
