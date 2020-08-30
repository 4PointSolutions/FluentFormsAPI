# TODO List

This file contains a list of things that I would like to do to improve on the existing API

1. Test the omission of a SubmitURL to make sure it works.  If it doesn't we may need to add a reasonable default if it is omitted (for example an empty List or List with one enty - an empty String).

1. Modify setXci to take a byte[] or a Path in addition to taking a Document object.  This will help remove boilerplate code for setting the XCI.

1. Add support for not specifying a data file in renderForm().  Use overloading to differentiate between when a data
file is omitted and when a null is passed in by mistake. 
   
1. Change PathOrUrl to use an enum for PathOrUrl type.  Then switch all the cascading if statements with isPath()/isUrl()/isCrxUrl() into switch statements.

1. It would be nice if we checked for the existence of a form with a crx: URL before called AEM so that we could
distinguish between the case where an XDP is missing and when something else bad happens (missing XDP should be a
BAD REQUEST return instead of INTERNAL SERVER ERROR.