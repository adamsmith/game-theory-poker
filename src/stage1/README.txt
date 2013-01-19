binary file formats
-------------------

First short is a versionID.  The next short[10] is defined based on the versionID.  Formally,

     bytenum : type : semantic
	 0-1 : short : versionID
	 2-21: short[10] : versionID-specific header

	 for vidScoreMap_holeCardsConstant_boardCardsImplicit
	  header[0] = holeCard[0]
	  header[1] = holeCard[1]
	  header[2] = numBoardCards (must be 0 or 3 or 4)
	  header[3] through header[9] = 0
	
	 for vidScoreOnly_holeCardsConstant_boardCardsImplicit
	  header[0] = holeCard[0]
	  header[1] = holeCard[1]
	  header[2] = numBoardCards (must be 0 or 3 or 4 or 5)
	  header[3] through header[9] = 0
	  
	  