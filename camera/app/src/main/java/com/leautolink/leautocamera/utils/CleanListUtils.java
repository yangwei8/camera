package com.leautolink.leautocamera.utils;

import com.leautolink.leautocamera.domain.ListingInfo;

import java.util.List;

/**
 * 清理List的工具类
 * Created by tianwei1 on 2016/3/7.
 */
public class CleanListUtils {
    private static final java.lang.String TAG = "CleanListUtils";

    /**
     * 清理ListingInfo
     *
     * @param listingInfo
     */
    public static void releaseListingInfo(ListingInfo listingInfo) {
        if (listingInfo != null) {
            List<ListingInfo.FileInfo> listing = listingInfo.getListing();
            if (listing != null && listing.size() > 0) {
                int listingSize = listing.size();
                for (int i = 0; i < listingSize; i++) {
                    ListingInfo.FileInfo fileInfo = listing.remove(0);
                    if (!listing.contains(fileInfo)) {
                        fileInfo = null;
                    }
                }
            }
            if (0 == listing.size()) {
                Logger.i(TAG, "listing.size():" + listing.size());
                listing = null;
            }
        }
        listingInfo = null;
    }
}
