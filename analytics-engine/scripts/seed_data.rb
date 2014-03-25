#!/usr/bin/ruby

# This is a script to create some seed data to work with the application
# This script creates 100 unique clients and 10 unique drivers and 1000 trips
# client ids/driver ids are 15 char strings
# Time of the trip is from today and next two weeks
# latitude is represented by float values between -90 to + 90.
# longitude is represented by float values between - 180 to +180
# fares are variable float values from 5 - 200 (in dollars)
# distance is variable between 2 - 40 (in miles)
# ratings are from 1 - 5 (5 star rating model)
# The seed data is comma separated value


require 'fileutils'

i = 0
num = 1000
client_array = []
driver_array = []
while i < 100 do
	clientid = (0...15).map{ ('a'..'z').to_a[rand(26)] }.join
	if !client_array.include?(clientid)
		client_array << clientid 
		i+=1
	end
end
i = 0;
while i < 10 do
	driverid = (0...15).map{ ('a'..'z').to_a[rand(26)] }.join
	if !client_array.include?(driverid) && !driver_array.include?(driverid)
		driver_array << driverid
		i+=1
	end
end

seed_data_path = File.expand_path("..",Dir.pwd) + "/temp/seed_data.txt";
dir = File.dirname(seed_data_path)
FileUtils.mkdir_p(dir) unless File.directory?(dir)
out_file = File.new(seed_data_path, "w")
i = 0;
while i < num  do
   out_file.puts(client_array[rand(client_array.length)] + 
   "," + driver_array[rand(driver_array.length)] + 
   "," + Time.at(((Time.now + (2*7*24*60*60)).to_f - Time.now.to_f)*rand + Time.now.to_f).to_s + 
   "," + (rand * (180) - 90).to_s + 
   "," + (rand * (360) - 180).to_s + 
   "," + (rand * (195) + 5).to_s +
   "," + (rand * (38) + 2).to_s +
   "," + (rand(5) + 1).to_s
   )
   i+=1
end
out_file.close
