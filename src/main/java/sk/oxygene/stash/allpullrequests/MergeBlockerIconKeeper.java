/**
 * 
 */
package sk.oxygene.stash.allpullrequests;

/**
 * Since somehow SOY templates doesn't support enum's
 * 
 * @author jwagan
 *
 */
public class MergeBlockerIconKeeper {
    
    public static final MergeBlockerIconKeeper INSUFFICIENT_BRANCH_PERMISSIONS = new MergeBlockerIconKeeper("Insufficient branch permissions", "user-minus_16.png", "user-minus");
    public static final MergeBlockerIconKeeper CROSS = new MergeBlockerIconKeeper("DEFAULT", "cross_16.png", "cross");
    public static final MergeBlockerIconKeeper SUCCESSFUL_BUILD = new MergeBlockerIconKeeper("Not all required builds are successful yet", "wrench_16.png", "wrench");
    public static final MergeBlockerIconKeeper REQUIRES_APPROVERS = new MergeBlockerIconKeeper("Requires approvers", "users_16.png", "users");
    public static final MergeBlockerIconKeeper MERGE_CONFLICT = new MergeBlockerIconKeeper("Resolve all merge confilcts first", "github2_16.png", "github");
    public static final MergeBlockerIconKeeper ALL_TASKS = new MergeBlockerIconKeeper("Requires all tasks to be resolved", "paste_16.png", "paste");
    
    public static final MergeBlockerIconKeeper[] values = new MergeBlockerIconKeeper[] {
            INSUFFICIENT_BRANCH_PERMISSIONS, CROSS, SUCCESSFUL_BUILD, REQUIRES_APPROVERS, MERGE_CONFLICT, ALL_TASKS };

    private MergeBlockerIconKeeper(String message, String iconFileName, String cssStyleName) {
        this.message = message;
        this.iconFileName = iconFileName;
        this.cssStyleName = cssStyleName;
    }
    
    private MergeBlockerIconKeeper(MergeBlockerIconKeeper mergeBlockerIconKeeper, String customMessage) {
        this.message = customMessage;
        this.iconFileName = mergeBlockerIconKeeper.iconFileName;
        this.cssStyleName = mergeBlockerIconKeeper.cssStyleName;
    }

    private final String message;
    private final String iconFileName;
    private final String cssStyleName;

    public static String getIconFileNameByMessage(String message) {
        String tempIconFileName = CROSS.getIconFileName();
        
        for(MergeBlockerIconKeeper mergeBlockerIcons : values) {
            if(message.equalsIgnoreCase(mergeBlockerIcons.getMessage())) {
                tempIconFileName = mergeBlockerIcons.getIconFileName();
            }
        }
        
        return tempIconFileName;
    }
    
    public static MergeBlockerIconKeeper getMergeBlockerIconByMessage(String message) {
        for(MergeBlockerIconKeeper mergeBlockerIcons : values) {
            if(message.equalsIgnoreCase(mergeBlockerIcons.getMessage())) {
                return mergeBlockerIcons;
            }
        }
        
        return new MergeBlockerIconKeeper(CROSS, message);
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
