/**
 * 
 */
package sk.oxygene.stash.allpullrequests;

import com.thoughtworks.selenium.webdriven.commands.GetValue;


/**
 * @author jwagan
 *
 */
public enum MergeBlockerIcon {

    INSUFFICIEN_BRANCH_PERMISSIONS("Insufficient branch permissions", "user-minus_16.png"),
    CROSS("DEFAULT", "cross_16.png");
    
    
    private MergeBlockerIcon(String message, String iconFileName) {
        this.message = message;
        this.iconFileName = iconFileName;
        this.name();
    }    

    private final String message;
    public final String iconFileName;
    
    public String getIconFileName() {
        
        return iconFileName;
    }
    
    private String getMessage() {
        return message;
    }
    
    public static String getIconFileNameByMessage(String message) {
        String tempIconFileName = CROSS.getIconFileName();
        
        for(MergeBlockerIcon mergeBlockerIcons : values()) {
            if(message.equalsIgnoreCase(mergeBlockerIcons.getMessage())) {
                tempIconFileName = mergeBlockerIcons.getIconFileName();
            }
        }
        
        return tempIconFileName;
    }
    
    public static MergeBlockerIcon getMergeBlockerIconByMessage(String message) {
        for(MergeBlockerIcon mergeBlockerIcons : values()) {
            if(message.equalsIgnoreCase(mergeBlockerIcons.getMessage())) {
                return mergeBlockerIcons;
            }
        }
        
        return CROSS;
    }
    
    @Override
    public String toString() {
        return getIconFileName();
    }
    
    
}
