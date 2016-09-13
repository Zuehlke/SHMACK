import sys
import json
from types import *

if ( len(sys.argv) != 3 ):
	print "Illegal arguments."
	print "   <Output-Key> <src-document>"
	sys.exit(1)


outputKey = sys.argv[1]
jsonFile = open( sys.argv[2], 'r' )

jsonString = jsonFile.read()
jsonObj = json.loads( jsonString )

def getStackOutputValue( current ):
	value = None
	result = None
	currentType = type(current);
	
	if ( currentType is DictType ):
		if ( 'OutputKey' in current ):
			if ( current['OutputKey'] == outputKey ):
				result = current['OutputValue']
		else:
			for value in current.itervalues():
				found = getStackOutputValue( value )
				if ( found is not None ):
					if ( result is None ):
						result = found;
					else:
						raise Exception( "Value not unique for key: " + jsonKey )
	elif ( currentType is ListType ):
		for value in current:
			found = getStackOutputValue( value )
			if ( found is not None ):
				if ( result is None ):
					result = found;
				else:
					raise Exception( "Value not unique for key: " + jsonKey )
	elif ( currentType is UnicodeType ):
		# intentionaly do nothing
		result = None
	elif ( currentType is BooleanType ):
		# intentionaly do nothing
		result = None
	else:
		raise Exception("Not yet supported type: ", currentType)
		
	return result;
   

result = getStackOutputValue( jsonObj )

if ( result is None ):
		raise Exception( "Value not found for key: " + jsonKey )

print result

