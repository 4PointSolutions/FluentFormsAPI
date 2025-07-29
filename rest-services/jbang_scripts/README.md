# Creating `deploy_it_assets.jar`

Execute the following command from this directory to create the necessary `deploy_it_assets.jar` file in the ff_it_files directory:  
`jbang export fatjar --verbose --output ..\test_containers\ff_it_files\deploy_it_assets.jar .\deploy_it_assets.java`

# Developer Tips

When making changes to the script, it is helpful if you execute the following command to create a maven project under
the current directory.  You can them import that project in Eclipse and work with the code like normal Java code
(taking advantage of Eclipse's incremental compilation, error messages, etc.).

`jbang export maven .\deploy_it_assets.java`

When you have completed your change, you can copy/paste the modified code back into the original deploy_it_assets.java and delete 
the maven project in Eclipse (checking the box to delete the files on disk).