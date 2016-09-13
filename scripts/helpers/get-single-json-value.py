import sys
import json
from types import *

if ( len(sys.argv) != 3 ):
	print "Illegal arguments."
	print "   <json-Key> <src-document>"
	sys.exit(1)


jsonKey = sys.argv[1]
jsonFile = open( sys.argv[2], 'r' )

jsonString = jsonFile.read()
jsonObj = json.loads( jsonString )

def findSingleJsonValue( current ):
	value = None
	result = None
	currentType = type(current);
	
	if ( currentType is DictType ):
		if ( jsonKey in current ):
			result = current[jsonKey]
		else:
			for value in current.itervalues():
				found = findSingleJsonValue( value )
				if ( found is not None ):
					if ( result is None ):
						result = found;
					else:
						raise Exception( "Value not unique for key: " + jsonKey )
	elif ( currentType is ListType ):
		for value in current:
			found = findSingleJsonValue( value )
			if ( found is not None ):
				if ( result is None ):
					result = found;
				else:
					raise Exception( "Value not unique for key: " + jsonKey )
	else:
		raise Exception("Not yet supported type: ", currentType)
		
	return result;
   

result = findSingleJsonValue( jsonObj )

if ( result is None ):
		raise Exception( "Value not found for key: " + jsonKey )

print result

