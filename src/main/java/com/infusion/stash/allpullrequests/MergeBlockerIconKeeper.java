/**
 * 
 */
package com.infusion.stash.allpullrequests;

import java.util.HashMap;
import java.util.Map;

/**
 * Since somehow SOY templates doesn't support enum's
 * 
 * @author jwagan
 *
 */
public final class MergeBlockerIconKeeper {
    
    public static final MergeBlockerIconKeeper INSUFFICIENT_BRANCH_PERMISSIONS = new MergeBlockerIconKeeper("Insufficient branch permissions", "user-minus_16.png", "user-minus");
    public static final MergeBlockerIconKeeper CROSS = new MergeBlockerIconKeeper("DEFAULT", "cross_16.png", "cross");
    public static final MergeBlockerIconKeeper SUCCESSFUL_BUILD = new MergeBlockerIconKeeper("Not all required builds are successful yet", "wrench_16.png", "wrench");
    public static final MergeBlockerIconKeeper REQUIRES_APPROVERS = new MergeBlockerIconKeeper("Requires approvers", "users_16.png", "users");
    public static final MergeBlockerIconKeeper MERGE_CONFLICT = new MergeBlockerIconKeeper("Resolve all merge conflicts first", "github2_16.png", "github");
    public static final MergeBlockerIconKeeper ALL_TASKS = new MergeBlockerIconKeeper("Requires all tasks to be resolved", "paste_16.png", "paste");
    
    public static final Map<String, MergeBlockerIconKeeper> VALUES_MAP = new HashMap<String, MergeBlockerIconKeeper>();
    
    static {
        VALUES_MAP.put(INSUFFICIENT_BRANCH_PERMISSIONS.getMessage(), INSUFFICIENT_BRANCH_PERMISSIONS);
        VALUES_MAP.put(SUCCESSFUL_BUILD.getMessage(), SUCCESSFUL_BUILD);
        VALUES_MAP.put(REQUIRES_APPROVERS.getMessage(), REQUIRES_APPROVERS);
        VALUES_MAP.put(MERGE_CONFLICT.getMessage(), MERGE_CONFLICT);
        VALUES_MAP.put(ALL_TASKS.getMessage(), ALL_TASKS);
    }
    
    private final String message;
    private final String iconFileName;
    private final String cssStyleName;

    private MergeBlockerIconKeeper(final String message, final String iconFileName, final String cssStyleName) {
        this.message = message;
        this.iconFileName = iconFileName;
        this.cssStyleName = cssStyleName;
    }
    
    private MergeBlockerIconKeeper(final MergeBlockerIconKeeper mergeBlockerIconKeeper, final String customMessage) {
        this.message = customMessage;
        this.iconFileName = mergeBlockerIconKeeper.iconFileName;
        this.cssStyleName = mergeBlockerIconKeeper.cssStyleName;
    }

    public static MergeBlockerIconKeeper getMergeBlockerIconByMessage(final String message) {
        MergeBlockerIconKeeper mergeBlockerIconKeeper = VALUES_MAP.get(message);
        if (mergeBlockerIconKeeper == null) {
            mergeBlockerIconKeeper = new MergeBlockerIconKeeper(CROSS, message);
        }
        
        return mergeBlockerIconKeeper;
    }
    
    public String getIconFileName() {
        
        return iconFileName;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getCssStyleName() {
        return cssStyleName;
    }
}
