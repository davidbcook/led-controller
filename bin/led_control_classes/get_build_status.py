#!/usr/bin/env python

# Gets the build status of specific Bamboo builds from Jut's Bamboo instance

import requests

# The list of builds we're interested in
builds = ["CAM-AMT", "CAM-CMT","CAM-SMOKE","CAM-IMT","CAM-QUAM","CAM-JUM","CAM-VIM","CAM-EAM","CAM-SMT"]
#builds = ["CAM-PMT"]
build_results = []

# Get the status of each build and add it to the build_results list
for build in builds:
  r = requests.get('https://jut-io.atlassian.net/builds/rest/api/latest/result/' + build + '-latest.json', auth=('robot','mrroboto'))
  build_results.append(r.json()['buildState'])
  print r.json()['buildState']

resultfile = open('/Users/davidcook/code/scratch/build-status-leds/build_status_display/data/build_results.txt', 'w+')

failed_count = 0

# Count the number of builds that failed
for result in build_results:
  if result == "Failed":
    failed_count += 1

# If any of the builds failed, write Failed to the file. If they all passed, write Successful
if failed_count > 0:
  resultfile.write("Failed\n")
else:
  resultfile.write("Successful\n")

resultfile.close()
