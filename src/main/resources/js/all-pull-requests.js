define('plugin/all-pull-requests', ['jquery', 'underscore', 'aui', 'model/page-state', 'exports'], function ($, _, AJS, pageState, exports) {
    exports.onReady = function (buttonSelector) {
        $.ajax(AJS.contextPath() + '/rest/all-pull-requests/1.0/count', {
            data: {
                project: pageState.getProject().get('key')
            },
            success: function (data) {
                if (data.count) {
                    $(buttonSelector).append(' ').append(aui.badges.badge({'text': data.count}));
                }
            }
        });
    }
});

AJS.$(function() {
    require('plugin/all-pull-requests').onReady('#project-pull-requests-button');
});
