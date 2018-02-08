package com.smdt.androidapi.adapter;

import android.util.SparseArray;
import android.view.View;

/**
 * Description: viewHolder工具类
 * AUTHOR: Champion Dragon
 * created at 2017/11/16
 **/

public class ViewHolderUtil {
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHoler = (SparseArray<View>) view.getTag();
        if (viewHoler == null) {
            viewHoler = new SparseArray<View>();
            view.setTag(viewHoler);
        }
        View childrenView = viewHoler.get(id);
        if (childrenView == null) {
            childrenView = view.findViewById(id);
            viewHoler.put(id, childrenView);
        }
        return (T) childrenView;
    }
}
