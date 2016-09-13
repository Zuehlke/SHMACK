import sys
import json
from types import *

if ( len(sys.argv) != 4 ):
	print "Illegal arguments."
	print "   <path-to-node-info-file.json> <0-based slave index> <attribute>"
	sys.exit(1)


jsonFile = open( sys.argv[1], 'r' )
slaveIndex = int(sys.argv[2])
attribute = sys.argv[3]

jsonString = jsonFile.read()
jsonObj = json.loads( jsonString )


print jsonObj[slaveIndex][attribute]

