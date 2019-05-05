# TODO List

This file contains a list of things that I would like to do to improve on the existing API

1. Test the omission of a SubmitURL to make sure it works.  If it doesn't we may need to add a reasonable default if it is omitted (for example an empty List or List with one enty - an empty String).

1. Modify the setContentRoot to take a Path or URL rather than a String.

1. Modify setXci to take a byte[] or a Path in addition to taking a Document object.  This will help remove boilerplate code for setting the XCI.
   