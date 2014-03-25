#!/usr/bin/ruby

require 'net/http'
require 'open-uri'

http = Net::HTTP.new('localhost', 8182)
# The total number of trips
response = http.send_request('GET', '/trips')
puts response

#The total number of trips between start and end date
response = http.send_request('GET', URI::encode('/trips?start=Wed Oct 23 13:13:00 -0700 2013&end=Wed Oct 29 13:13:00 -0700 2013'))
puts response

#The total number of trips in the last hour
response = http.send_request('GET', URI::encode('/trips?last=true'))
puts response

#Total number of clients who have taken trips
response = http.send_request('GET', '/clients/count')
puts response

#Total number of clients who have taken trips between dates
response = http.send_request('GET', URI::encode('/clients/count?start=Wed Oct 26 13:13:00 -0700 2013&end=Wed Oct 29 13:13:00 -0700 2013'))
puts response

#Total number of miles per client
response = http.send_request('GET', '/clients/miles/yntmlnlncedqmtz')
puts response

#Total number of miles per client within date range start and end
response = http.send_request('GET', URI::encode('/clients/miles/yntmlnlncedqmtz?start=Wed Oct 26 13:13:00 -0700 2013&end=Wed Oct 29 13:13:00 -0700 2013'))
puts response

#avg fare paid by city
response = http.send_request('GET', '/city/1/fare/avg')
puts response

#average fare paid by city within a time range
response = http.send_request('GET', URI::encode('/city/1/fare/avg?start=Wed Oct 26 13:13:00 -0700 2013&end=Wed Oct 29 13:13:00 -0700 2013'))
puts response

#median rating for a particular driver
response = http.send_request('GET', '/drivers/ztjsynqubjlnbkx/rating/median')
puts response

