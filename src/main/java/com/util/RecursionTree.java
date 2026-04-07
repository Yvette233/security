package com.util;

import com.common.BasicManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

public class RecursionTree<T> {
    public final static Log log = LogFactory.getLog(RecursionTree.class);
    String idName;
    String parentIdName;
    String childrenName;
    private BasicManager basicManager = new BasicManager();

    public RecursionTree() {
        this.idName = "id";
        this.parentIdName = "parentId";
        this.childrenName = "children";
    }

    public RecursionTree(String idName, String parentIdName, String childrenName) {
        this.idName = idName;
        this.parentIdName = parentIdName;
        this.childrenName = childrenName;
    }

    //建立树形结构
    public List<T> builTree(List<T> list) {
        List<T> tree = new ArrayList<T>();
        for (T node : getRootNodeList(list)) {
            T bNode = (T) node;
            node = buildChildTree(list, bNode);
            tree.add(node);
        }
        return tree;
    }

    //递归，建立子树形结构
    public T buildChildTree(List<T> list, T node) {

        List<T> children = new ArrayList<T>();
        for (T t : list) {
            //根节点不可能是其他对象的子
            if (ObjectUtils.isEmpty(basicManager.getValue(t, this.parentIdName)))
                continue;
            if (basicManager.getValue(t, this.parentIdName).equals(basicManager.getValue(node, this.idName))) {
                children.add(buildChildTree(list, t));
            }
        }
        basicManager.setValue(node, this.childrenName, children);
        return node;
    }

    //得到节点所有子
    public void getAllChildren(List<T> list, T node, List<T> allChildren) {

        for (T t : list) {
            //根节点不可能是其他对象的子
            if (ObjectUtils.isEmpty(basicManager.getValue(t, this.parentIdName)))
                continue;
            if (basicManager.getValue(t, this.parentIdName).equals(basicManager.getValue(node, this.idName))) {
                getAllChildren(list, t, allChildren);
                allChildren.add(t);
            }
        }
    }

    //获取根节点
    public List<T> getRootNodeList(List<T> list) {
        List<T> rootList = new ArrayList<T>();
        int hierarchy = Integer.MAX_VALUE;
        for (T t : list) {
            int currentHierachy = (int) basicManager.getValue(t, "hierarchy");
            if (currentHierachy < hierarchy) {
                hierarchy = currentHierachy;
                rootList.clear();
                rootList.add(t);
            } else if (currentHierachy == hierarchy) {
                rootList.add(t);
            }
        }

        return rootList;
    }


}
