All Pull Requests Add-on for Atlassian Stash with Mergeability status
=====================================================================


[![plugin_screen.PNG](https://bitbucket.org/repo/Lx9E7y/images/573324582-plugin_screen.PNG)](https://marketplace.atlassian.com/vendors/1212031)

It displays as an icon information about what is blocking you to do the merge:

 * Insufficient branch permissions
 * Not all required builds are successful yet
 * Requires approvers
 * Resolve all merge conflicts first
 * Requires all tasks to be resolved

Development
-----------

Useful Atlassian SDK commands:

* atlas-run   -- installs this plugin into the product and starts it on localhost
* atlas-debug -- same as atlas-run, but allows a debugger to attach at port 5005
* atlas-cli   -- after atlas-run or atlas-debug, opens a Maven command line window:
                 - 'pi' reinstalls the plugin into the running product instance
* atlas-help  -- prints description for all commands in the SDK

Full documentation is always available at:

https://developer.atlassian.com/docs/getting-started