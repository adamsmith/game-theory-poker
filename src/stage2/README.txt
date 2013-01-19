Random Notes
------------
No parallelism.





File naming convention for stage 4 folder
-----------------------------------------

Subfolders are 0, 3, 4, and 5.

Each subfolder contains a file for each {52 choose 2} possible hand cards.  Each one of these
files contains the standard binary format headers, followed by a series of bytes representing
a cluster ID for each implicit hand.  The clustering is meaningful across other holecard
files, not within a file.






binary file formats
-------------------

First short is a versionID.  The next short[10] is defined based on the versionID.  Formally,

     bytenum : type : semantic
	 0-1 : short : versionID
	 2-21: short[10] : versionID-specific header

	 for vidClusterIDs_holeCardsConstant_boardCardsImplicit
	  header[0] = holeCard[0]
	  header[1] = holeCard[1]
	  header[2] = numBoardCards (must be 0 or 3 or 4)
	  header[3] = numClusters
	  header[4] through header[9] = 0
	  