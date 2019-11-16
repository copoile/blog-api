package cn.poile.blog.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: yaohw
 * @create: 2019-11-14 17:51
 **/
public abstract class AbstractListWarp<S, T> {


    public abstract T warp(S source);

    /**
     * list集合转换
     * @param sourceList
     * @return
     */
    public List<T> warpList(List<S> sourceList) {
        List<T> targetList = new ArrayList<>();
        if (sourceList != null && !sourceList.isEmpty()) {
            for (S source : sourceList) {
                targetList.add(warp(source));
            }
        }
        return targetList;
    }
}
