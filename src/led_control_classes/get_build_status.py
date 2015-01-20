#!/usr/bin/env python

# Gets the build status of specific Bamboo builds from your Bamboo instance. This should be run every couple minutes on your machine using something like cron.

import requests

# The list of builds we're interested in
builds = ['<LIST OF BUILDS>']
build_results = []

# Get the status of each build and add it to the build_results list
for build in builds:
  r = requests.get('https://<YOUR ATLASSIAN ONDEMAND URL>/builds/rest/api/latest/result/' + build + '-latest.json', auth=('<USERNAME>','<PASSWORD>'))
  build_results.append(r.json()['buildState'])
  print r.json()['buildState']

resultfile = open('<PATH TO BUILD RESULTS FILE>', 'w+')

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
