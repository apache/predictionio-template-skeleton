"""
Import sample data for classification engine
"""

import predictionio
import argparse

def import_events(client, file):
  f = open(file, 'r')
  count = 0
  print "Importing data..."
  for line in f:
    data = line.rstrip('\r\n').split(",")
    plan = data[0]
    attr = data[1].split(" ")

    client.create_event(
      event="$set",
      entity_type="training_point",
      entity_id=str(count), # use the count num as user ID
      properties= {
        "attr0" : float(attr[0]),
        "attr1" : float(attr[1]),
        "attr2" : float(attr[2]),
        "attr3" : float(attr[3]),
        "attr4" : float(attr[4]),
        "attr5" : float(attr[5]),
        "attr6" : float(attr[6]),
        "attr7" : float(attr[7]),
        "plan" : float(plan)
      }
    )
    count += 1
  f.close()
  print "%s events are imported." % count

if __name__ == '__main__':
  parser = argparse.ArgumentParser(
    description="Import sample data for classification engine")
  parser.add_argument('--access_key', default='invald_access_key')
  parser.add_argument('--url', default="http://localhost:7070")
  parser.add_argument('--file', default="./data/sample_data.txt")

  args = parser.parse_args()
  print args

  client = predictionio.EventClient(
    access_key=args.access_key,
    url=args.url,
    threads=5,
    qsize=500)
  import_events(client, args.file)
