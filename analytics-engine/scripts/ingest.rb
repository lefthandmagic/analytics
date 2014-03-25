#!/usr/bin/ruby

require 'net/http'

#ingests the random seed data that was created in the seed_data.rb script
#into the server. Please run this once you bring the server up invoking the
# com.uber.analytics.Main class, and before running the query.rb test script
# which runs test requests against this dataset

seed_data_path = File.expand_path("..",Dir.pwd) + "/test_data/seed_data.txt";
line_num=0
File.open(seed_data_path).each do |line|
  params = line.split(",")
  paramString = "client_id=#{params[0]}&driver_id=#{params[1]}&start_time=#{params[2]}
  &latitude=#{params[3]}&longitude=#{params[4]}&fare=#{params[5]}&distance=#{params[6]}
  &rating=#{params[7]}" if params.size == 8
  http = Net::HTTP.new('localhost', 8182)
  response = http.send_request('PUT', '/trips', paramString) if paramString
end
